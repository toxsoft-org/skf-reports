package org.toxsoft.skf.reports.gui.rtchart;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.graphics.*;
import org.toxsoft.core.tsgui.graphics.fonts.impl.*;
import org.toxsoft.core.tsgui.graphics.lines.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.*;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.temporal.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.reports.chart.utils.gui.*;
import org.toxsoft.skf.reports.chart.utils.gui.console.*;
import org.toxsoft.skf.reports.chart.utils.gui.panels.*;
import org.toxsoft.skf.reports.chart.utils.gui.tools.axes_markup.*;
import org.toxsoft.skf.reports.gui.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Компонента, отображающая график одного параметра в реальном времени. <br>
 * TODO дофига общего с ChartPanel - требуется рефакторинг
 *
 * @author vs
 * @author dima
 */
public class RtChartPanel
    extends TsPanel {

  int refreshInterval = 1000;

  Runnable refreshTimer;
  Display  display;
  boolean  timerStopped = false;
  IG2Chart chart;

  Button btnPageLeft;
  Button btnStepLeft;
  Button btnStepRight;
  Button btnPageRight;

  Button btnVisir;
  Button btnLegend;
  Button btnConsole;

  ValedComboSelector<ETimeUnit>   timeUnitCombo;
  final IStringMapEdit<YAxisInfo> axisInfoes = new StringMap<>();

  ETimeUnit      axisTimeUnit  = null;
  G2ChartConsole console       = null;
  ConsoleWindow  consoleWindow = null;

  IListEdit<RtGraphDataSet> graphDataSetList = new ElemArrayList<>();
  private GraphicInfo       graphInfo;
  // наша Y шкала
  private IYAxisDef yAxisDef;
  // id набора данных
  private String graphDataSetId;

  static class YAxisInfo {

    private final String                      id;
    private final Pair<String, String>        unitInfo;
    private final IStringMapEdit<GraphicInfo> graphicInfoes = new StringMap<>();

    private Double min = null;
    private Double max = null;

    YAxisInfo( String aId, Pair<String, String> aUnitInfo ) {
      id = aId;
      unitInfo = aUnitInfo;
    }

    String id() {
      return id;
    }

    IStringMapEdit<GraphicInfo> graphicInfoes() {
      return graphicInfoes;
    }

    void putGraphicInfo( GraphicInfo aGraphInfo ) {
      Pair<Double, Double> minMax = aGraphInfo.minMax();
      if( min == null || min > minMax.left() ) {
        min = minMax.left();
      }
      if( max == null || max < minMax.right() ) {
        max = minMax.right();
      }
      graphicInfoes.put( aGraphInfo.id(), aGraphInfo );
    }

    Pair<String, String> unitInfo() {
      return unitInfo;
    }
  }

  /**
   * Панель для отображения одного графика реального времени
   *
   * @param aParent родительская панель
   * @param aContext контекст приложения
   * @param aGraphTemplate описания шаблона графика
   * @param aConnection соединение с сервером
   */
  public RtChartPanel( Composite aParent, ITsGuiContext aContext, IVtGraphTemplate aGraphTemplate,
      ISkConnection aConnection ) {
    super( aParent, aContext );
    setLayout( new BorderLayout() );

    ISkCoreApi serverApi = aConnection.coreApi();
    createToolBar();
    // формируем запрос к одноименному сервису
    IStringMap<IDtoQueryParam> queryParams = ReportTemplateUtilities.formQueryParams( aGraphTemplate );
    ISkConnectionSupplier connSupp = tsContext().get( ISkConnectionSupplier.class );

    ISkQueryProcessedData processData =
        connSupp.defConn().coreApi().hqService().createProcessedQuery( IOptionSet.NULL );

    processData.prepare( queryParams );
    // по берем данные за последние 10 минут
    TimeInterval reqInterval =
        new TimeInterval( System.currentTimeMillis() - 10L * 60L * 1000L, System.currentTimeMillis() );

    processData.exec( new QueryInterval( EQueryIntervalType.OSOE, reqInterval.startTime(), reqInterval.endTime() ) );

    // асинхронное получение данных
    processData.genericChangeEventer().addListener( aSource -> {
      ISkQueryProcessedData q = (ISkQueryProcessedData)aSource;
      if( q.state() == ESkQueryState.READY ) {
        int i = 0;
        IList<ITimedList<?>> requestAnswer = ReportTemplateUtilities.createResult( processData, queryParams );
        Display display2 = aContext.get( Display.class );
        for( IVtGraphParam graphParam : aGraphTemplate.listParams() ) {
          graphDataSetList.add( new RtGraphDataSet( display2, graphParam, serverApi, requestAnswer.get( i++ ) ) );
        }
        init();
        start();
      }
    } );

    addDisposeListener( aEvent -> onDispose() );
  }

  void onDispose() {
    for( RtGraphDataSet graphDataSet : graphDataSetList ) {
      graphDataSet.close();
    }
  }

  void init() {
    // создаем компоненту график
    createChart();
    // наполняем ее данными отчета
    Composite chartComp = chart.createControl( this );
    for( int i = 0; i < graphDataSetList.size(); i++ ) {
      RtGraphDataSet rtGraphDataSet = graphDataSetList.get( i );
      fillChartData( rtGraphDataSet, rtGraphDataSet.getGraphParam() );
      createYAxis( chart, rtGraphDataSet.getGraphParam() );
      createPlot( graphDataSetList.get( i ).getGraphParam() );
    }
    chartComp.setLayoutData( BorderLayout.CENTER );
    console = new G2ChartConsole( (G2Chart)chart );

    display = chart.getControl().getDisplay();

    refreshTimer = () -> {
      if( !display.isDisposed() && !timerStopped ) {
        onTimerTick();
        display.timerExec( refreshInterval, refreshTimer );
      }
    };
    layout();
  }

  void createPlot( IVtGraphParam aGraphParam ) {
    PlotDefTuner plotTuner = new PlotDefTuner( tsContext() );
    RGB plotColor = aGraphParam.color().rgb();
    int lineWidth = aGraphParam.lineWidth();
    EDisplayFormat displayFormat = aGraphParam.displayFormat();
    plotTuner.setLineInfo( TsLineInfo.ofWidth( lineWidth ) );
    plotTuner.setRGBA( new RGBA( plotColor.red, plotColor.green, plotColor.blue, 255 ) );
    plotTuner.setDisplayFormat( displayFormat );
    plotTuner.setRenderingKind( aGraphParam.isLadder() ? EGraphicRenderingKind.LADDER : EGraphicRenderingKind.LINE );
    plotTuner.setSetPointsList( aGraphParam.setPoints() );

    IPlotDef plotDef = graphInfo.createPlotDef( plotTuner );
    chart.plotDefs().add( plotDef );
  }

  void createYAxis( IG2Chart aChart, IVtGraphParam aGraphParam ) {
    for( YAxisInfo axisInfo : axisInfoes ) {
      double min = axisInfo.graphicInfoes().values().get( 0 ).minMax().left().doubleValue();
      double max = axisInfo.graphicInfoes().values().get( 0 ).minMax().right().doubleValue();
      for( GraphicInfo chartInfo : axisInfo.graphicInfoes() ) {
        if( chartInfo.minMax().left().doubleValue() < min ) {
          min = chartInfo.minMax().left().doubleValue();
        }
        if( chartInfo.minMax().right().doubleValue() > max ) {
          max = chartInfo.minMax().right().doubleValue();
        }
      }
      yAxisDef = createYAxisDef( axisInfo.id(), min, max, aGraphParam.displayFormat().format(), axisInfo.unitInfo() );
      aChart.yAxisDefs().add( yAxisDef );
    }
  }

  IYAxisDef createYAxisDef( String aId, double aMin, double aMax, String aFormatStr, Pair<String, String> aUnitInfo ) {
    YAxisTuner yTuner = new YAxisTuner( tsContext() );
    yTuner.setFormatString( aFormatStr );

    AxisMarkupTuner mt = new AxisMarkupTuner( aMin, aMax );
    MarkUpInfo mi = mt.tuneAxisMarkup( aMin, aMax, 5, 15 );

    yTuner.setStartValue( mi.bgnValue );
    yTuner.setEndValue( mi.bgnValue + mi.step * mi.qttyOfSteps );
    yTuner.setStepValue( mi.step );

    yTuner.setTitle( aUnitInfo.right() );
    yTuner.setTitleOrientation( ETsOrientation.VERTICAL );
    yTuner.setTitleFont( new FontInfo( "Arial", 18, false, false ) );

    return yTuner.createAxisDef( aId, aUnitInfo.right(), aUnitInfo.left() );
  }

  private void fillChartData( RtGraphDataSet aGraphDataSet, IVtGraphParam aGraphParam ) {
    IList<ITemporalAtomicValue> values = aGraphDataSet.getValues( ITimeInterval.NULL );
    Pair<Double, Double> minMax = calcMinMax( values );

    graphDataSetId = ReportTemplateUtilities.graphDataSetId( aGraphParam );

    YAxisInfo axisInfo;
    if( axisInfoes.hasKey( aGraphParam.unitId() ) ) {
      axisInfo = axisInfoes.getByKey( aGraphParam.unitId() );
    }
    else {
      axisInfo = new YAxisInfo( graphDataSetId, new Pair<>( aGraphParam.unitId(), aGraphParam.unitName() ) );
      axisInfoes.put( aGraphParam.unitId(), axisInfo );
    }

    chart.dataSets().add( aGraphDataSet );

    IStridable graphStridable = new Stridable( graphDataSetId, aGraphParam.title(), aGraphParam.description() );

    graphInfo = new GraphicInfo( graphStridable, axisInfo.id(), graphDataSetId, minMax, aGraphParam.isLadder() );
    axisInfo.putGraphicInfo( graphInfo );
  }

  /**
   * Вычисление min & max диапазона значений
   *
   * @param aValues значения выборки с сервиса данных
   * @return пара значений {@link Pair}
   */
  Pair<Double, Double> calcMinMax( IList<ITemporalAtomicValue> aValues ) {

    if( aValues.size() > 0 ) {
      double max = Double.NEGATIVE_INFINITY;
      double min = Double.POSITIVE_INFINITY;
      // счетчик присвоенных значений
      int assignedValuesCounter = 0;
      for( ITemporalAtomicValue value : aValues ) {
        if( !(value.value().isAssigned()) ) {
          continue;
        }
        assignedValuesCounter++;
        double dVal = value.value().asDouble();
        if( dVal < min ) {
          min = dVal;
        }
        if( dVal > max ) {
          max = dVal;
        }
      }

      if( min > 0 ) {
        min = 0;
      }

      min = Math.ceil( min );

      return assignedValuesCounter >= 2 ? new Pair<>( Double.valueOf( min ), Double.valueOf( max ) )
          : new Pair<>( Double.valueOf( 0 ), Double.valueOf( 10 ) );
    }
    return new Pair<>( Double.valueOf( 0 ), Double.valueOf( 10 ) );
  }

  private void createChart() {

    TimeAxisTuner tuner = new TimeAxisTuner( tsContext() );
    // настройка шкалы времении
    axisTimeUnit = ETimeUnit.MIN01;
    timeUnitCombo.setSelectedItem( axisTimeUnit );
    tuner.setTimeUnit( axisTimeUnit );
    // настройка шкалы времении
    // шкала 10 минут, текущее время на отметке 80%
    long startTime = System.currentTimeMillis() - 8 * axisTimeUnit.timeInMills();
    long endTime = startTime + 10 * axisTimeUnit.timeInMills();

    tuner.setTimeInterval( new TimeInterval( startTime, endTime ), false );
    IXAxisDef xAxisDef = tuner.createAxisDef();
    chart = G2ChartUtils.createChart( tsContext() );

    chart.setXAxisDef( xAxisDef );
  }

  /**
   * Запустить таймер обновления
   */
  public void start() {
    display.timerExec( refreshInterval, refreshTimer );
  }

  /**
   * Остановить таймер обновления
   */
  public void stop() {
    timerStopped = true;
  }

  void onTimerTick() {
    ITimeInterval ti = ((G2Chart)chart).xAxisModel().timeInterval();
    long time = System.currentTimeMillis();
    chart.console().locateX( time - (long)(ti.duration() * 0.8) );
    // проверяем что мы не выскочили из зоны видимой части шкалы
    IAtomicValue startYAxis = chart.console().getY1( yAxisDef.id() );
    IAtomicValue endYAxis = chart.console().getY2( yAxisDef.id() );
    IG2DataSet dataSet = chart.dataSets().getByKey( graphDataSetId );
    Pair<ITemporalAtomicValue, ITemporalAtomicValue> lastPair = dataSet.locate( System.currentTimeMillis() );
    if( !lastPair.left().equals( ITemporalAtomicValue.NULL ) && lastPair.left().value().isAssigned() ) {
      IAtomicValue lastValue = lastPair.left().value();
      if( lastValue.asDouble() >= endYAxis.asDouble() || lastValue.asDouble() <= startYAxis.asDouble() ) {
        // сдвигаем шкалу так чтобы новое значение стало посредине шкалы
        // changed by dima 07.07.25 (sitting in the bunker of Baikonur)
        // double shiftY = lastValue.asDouble() <= startYAxis.asDouble() ? -50 : 50;
        double shiftY = calculateShift( startYAxis, endYAxis, lastValue );
        chart.console().shiftYAxis( graphDataSetId, shiftY );
      }
    }
    try {
      // dima 31.05.23 тут может случится ошибка
      chart.refresh();
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
    }
  }

  double calculateShift( IAtomicValue aStartYAxis, IAtomicValue aEndYAxis, IAtomicValue aLastValue ) {
    double retVal = 0;
    // calculate axis visible length
    float axisLength = av2Float( aEndYAxis ) - av2Float( aStartYAxis );
    // calculate value in the middle of visible axis
    float axisCenterVal = av2Float( aStartYAxis ) + axisLength / 2;
    // delta between visual center and last value
    float delta = av2Float( aLastValue ) - axisCenterVal;
    // value in percents to shift axis
    double shiftY = (delta / axisLength) * 100;
    chart.console().shiftYAxis( graphDataSetId, shiftY );

    return retVal;
  }

  /**
   * Convert Atomic value to float
   *
   * @param aValue value to convert
   * @return value in type float
   */
  private static float av2Float( IAtomicValue aValue ) {
    float retVal = 0.0f;
    switch( aValue.atomicType() ) {
      case FLOATING: {
        retVal = aValue.asFloat();
        break;
      }
      case BOOLEAN:
        retVal = !aValue.asBool() ? 0.0f : 1.0f;
        break;
      case INTEGER:
        retVal = aValue.asInt();
        break;
      case NONE:
      case STRING:
      case TIMESTAMP:
      case VALOBJ:
      default:
        throw new TsUnsupportedFeatureRtException();
    }
    return retVal;

  }

  void createToolBar() {

    String pluginId = Activator.PLUGIN_ID;
    ImageDescriptor imgDescr;

    Composite comp = new Composite( this, SWT.NONE );
    comp.setLayout( new GridLayout( 20, false ) );
    comp.setLayoutData( BorderLayout.NORTH );

    btnVisir = new Button( comp, SWT.CHECK );
    btnVisir.setText( "Визир" );
    btnVisir.setSelection( false );
    imgDescr = AbstractUIPlugin.imageDescriptorFromPlugin( pluginId, "icons/is24x24/vizir.png" ); //$NON-NLS-1$
    btnVisir.setImage( imgDescr.createImage() );
    // явно удаляем ранее загруженную картинку
    btnVisir.addDisposeListener( aE -> btnVisir.getImage().dispose() );

    btnVisir.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        chart.visir().setVisible( btnVisir.getSelection() );
        chart.refresh();
      }
    } );

    btnConsole = new Button( comp, SWT.CHECK );
    btnConsole.setText( "Пульт" );
    imgDescr = AbstractUIPlugin.imageDescriptorFromPlugin( pluginId, "icons/is24x24/manage_pult.png" ); //$NON-NLS-1$
    btnConsole.setImage( imgDescr.createImage() );
    // явно удаляем ранее загруженную картинку
    btnConsole.addDisposeListener( aE -> btnConsole.getImage().dispose() );

    btnConsole.setSelection( false );
    btnConsole.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( !btnConsole.getSelection() && consoleWindow != null ) {
          consoleWindow.dispose();
          consoleWindow = null;
        }
        else {
          consoleWindow = new ConsoleWindow( getParent(), (G2Chart)chart, tsContext() );
          consoleWindow.shell().addDisposeListener( aE -> {
            btnConsole.setSelection( false );
            consoleWindow = null;
          } );
        }
      }
    } );

    CLabel l = new CLabel( comp, SWT.CENTER );
    l.setText( "Цена деления:" );

    IList<ETimeUnit> values = new ElemArrayList<>( ETimeUnit.values() );
    ITsVisualsProvider<ETimeUnit> visualsProvider = ETimeUnit::nmName;
    timeUnitCombo = new ValedComboSelector<>( tsContext(), values, visualsProvider );
    timeUnitCombo.createControl( comp );
    timeUnitCombo.eventer().addListener( ( aSource, aEditFinished ) -> {
      ETimeUnit tu = timeUnitCombo.selectedItem();
      if( tu != null ) {
        axisTimeUnit = tu;
        chart.console().setTimeUnit( tu );
        chart.refresh();
      }
    } );

  }

}
