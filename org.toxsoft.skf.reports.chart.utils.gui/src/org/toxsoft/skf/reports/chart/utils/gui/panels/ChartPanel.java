package org.toxsoft.skf.reports.chart.utils.gui.panels;

import static org.toxsoft.skf.reports.chart.utils.gui.IChartUtilsGuiSharedResources.*;
import static org.toxsoft.skf.reports.chart.utils.gui.IReportsChartUtilsGuiConstants.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.chart.renderers.IStdG2GraphicRendererOptions;
import org.toxsoft.core.tsgui.graphics.ETsOrientation;
import org.toxsoft.core.tsgui.graphics.colors.ETsColor;
import org.toxsoft.core.tsgui.graphics.fonts.impl.FontInfo;
import org.toxsoft.core.tsgui.graphics.icons.EIconSize;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.utils.ITsVisualsProvider;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.valed.controls.basic.ValedComboSelector;
import org.toxsoft.core.tslib.bricks.geometry.impl.TsPoint;
import org.toxsoft.core.tslib.bricks.time.ITimeInterval;
import org.toxsoft.core.tslib.bricks.time.impl.TimeInterval;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.coll.impl.ElemArrayList;
import org.toxsoft.core.tslib.coll.primtypes.IStringMapEdit;
import org.toxsoft.core.tslib.coll.primtypes.impl.StringMap;
import org.toxsoft.core.tslib.utils.Pair;
import org.toxsoft.skf.reports.chart.utils.gui.console.ConsoleWindow;
import org.toxsoft.skf.reports.chart.utils.gui.console.TimeAxisTuner;
import org.toxsoft.skf.reports.chart.utils.gui.tools.axes_markup.AxisMarkupTuner;
import org.toxsoft.skf.reports.chart.utils.gui.tools.axes_markup.MarkUpInfo;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;

/**
 * Панель для отображения отчета в виде графиков.
 *
 * @author vs
 * @author dima // conversion to ts4
 */
public class ChartPanel
    extends TsPanel {

  private static final String FONT_NAME = "Arial"; //$NON-NLS-1$

  Button btnPageLeft;
  Button btnStepLeft;
  Button btnStepRight;
  Button btnPageRight;
  Button btnSelect;

  Button btnVisir;
  Button btnLegend;

  Button btnConsole;
  Button btnPrint;

  G2Chart        chart        = null;
  ETimeUnit      axisTimeUnit = null;
  G2ChartConsole console      = null;
  // private IVtGraphTemplate template = null;
  final ISkCoreApi serverApi;

  ValedComboSelector<ETimeUnit> timeUnitCombo;

  final IStringMapEdit<GraphicInfo> graphicInfoes = new StringMap<>();
  final IStringMapEdit<YAxisInfo>   axisInfoes    = new StringMap<>();
  LegendWindow                      legendWindow  = null;
  ConsoleWindow                     consoleWindow = null;

  String chartTitle;

  /**
   * Конструктор панели
   *
   * @param aParent панель родителя
   * @param aContext контекст приложения
   */
  public ChartPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    setLayout( new BorderLayout() );

    ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();
    serverApi = conn.coreApi();
    createToolBar();
  }

  void createToolBar() {

    // EIconSize tabIconSize = hdpiService().getJFaceCellIconsSize();
    EIconSize tabIconSize = EIconSize.IS_24X24;

    Composite comp = new Composite( this, SWT.NONE );
    // comp.setLayout( new RowLayout() );
    comp.setLayout( new GridLayout( 20, false ) );
    comp.setLayoutData( BorderLayout.NORTH );

    btnPageLeft = new Button( comp, SWT.PUSH );
    btnPageLeft.setToolTipText( STR_DISPLAY_BACK );
    btnPageLeft.setImage( iconManager().loadStdIcon( ICONID_SHIFT_DISPLAY_LEFT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnPageLeft.addDisposeListener( aE -> btnPageLeft.getImage().dispose() );

    btnPageLeft.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( console != null && axisTimeUnit != null ) {
          console.locateX( console.getX1() - (console.getX2() - console.getX1()) );
          chart.refresh();
        }
      }
    } );

    btnStepLeft = new Button( comp, SWT.PUSH );
    btnStepLeft.setToolTipText( STR_BACK );
    btnStepLeft.setImage( iconManager().loadStdIcon( ICONID_SHIFT_STEP_LEFT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnStepLeft.addDisposeListener( aE -> btnStepLeft.getImage().dispose() );

    btnStepLeft.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( console != null && axisTimeUnit != null ) {
          console.locateX( console.getX1() - axisTimeUnit.timeInMills() );
          chart.refresh();
        }
      }
    } );

    btnStepRight = new Button( comp, SWT.PUSH );
    btnStepRight.setToolTipText( STR_FORWARD );
    btnStepRight.setImage( iconManager().loadStdIcon( ICONID_SHIFT_STEP_RIGHT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnStepRight.addDisposeListener( aE -> btnStepRight.getImage().dispose() );

    btnStepRight.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( chart != null && axisTimeUnit != null ) {
          console.locateX( console.getX1() + axisTimeUnit.timeInMills() );
          chart.refresh();
        }
      }
    } );

    btnPageRight = new Button( comp, SWT.PUSH );
    btnPageRight.setToolTipText( STR_DISPLAY_FORWARD );
    btnPageRight.setImage( iconManager().loadStdIcon( ICONID_SHIFT_DISPLAY_RIGHT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnPageRight.addDisposeListener( aE -> btnPageRight.getImage().dispose() );

    btnPageRight.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( console != null && axisTimeUnit != null ) {
          console.locateX( console.getX1() + (console.getX2() - console.getX1()) );
          chart.refresh();
        }
      }
    } );

    btnSelect = new Button( comp, SWT.PUSH );
    btnSelect.setText( STR_CHOICE );
    btnSelect.setImage( iconManager().loadStdIcon( ICONID_GRAPHIC_LIST, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnSelect.addDisposeListener( aE -> btnSelect.getImage().dispose() );

    btnSelect.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        IListEdit<IPlotDef> visiblePlots = new ElemArrayList<>();
        IListEdit<IPlotDef> hiddenPlots = new ElemArrayList<>();

        for( GraphicInfo graphInfo : graphicInfoes ) {
          if( graphInfo.isVisibe() ) {
            visiblePlots.add( graphInfo.plotDef() );
          }
          else {
            hiddenPlots.add( graphInfo.plotDef() );
          }
        }

        Pair<IList<IPlotDef>, IList<IPlotDef>> result;
        result = PanelPlotSelection.selectPlots( getShell(), new Pair<>( visiblePlots, hiddenPlots ), tsContext() );
        if( result != null ) {
          for( GraphicInfo graphInfo : graphicInfoes ) {
            if( result.left().hasElem( graphInfo.plotDef() ) ) {
              graphInfo.setVisible( true );
              console.setPlotVisible( graphInfo.plotDef().id(), true );
            }
            else {
              graphInfo.setVisible( false );
              console.setPlotVisible( graphInfo.plotDef().id(), false );
            }
          }
          chart.refresh();
        }
      }
    } );

    btnVisir = new Button( comp, SWT.CHECK );
    btnVisir.setText( STR_VIZIER );
    btnVisir.setSelection( false );
    btnVisir.setImage( iconManager().loadStdIcon( ICONID_VIZIR, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnVisir.addDisposeListener( aE -> btnVisir.getImage().dispose() );

    btnVisir.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        chart.visir().setVisible( btnVisir.getSelection() );
        chart.refresh();
      }
    } );

    btnLegend = new Button( comp, SWT.CHECK );
    btnLegend.setText( STR_LEGEND );
    btnLegend.setImage( iconManager().loadStdIcon( ICONID_LEGENDA_ON, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnLegend.addDisposeListener( aE -> btnLegend.getImage().dispose() );

    btnLegend.setSelection( false );
    btnLegend.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( !btnLegend.getSelection() && legendWindow != null ) {
          legendWindow.dispose();
          legendWindow = null;
        }
        else {
          legendWindow = new LegendWindow( getParent(), chart.plotDefs(), tsContext() );
          legendWindow.shell().addDisposeListener( aE -> {
            btnLegend.setSelection( false );
            legendWindow = null;
          } );
        }
      }
    } );

    btnConsole = new Button( comp, SWT.CHECK );
    btnConsole.setText( STR_N_CONTROL_PANEL );
    btnConsole.setImage( iconManager().loadStdIcon( ICONID_MANAGE_PULT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnConsole.addDisposeListener( aE -> btnConsole.getImage().dispose() );

    btnConsole.setSelection( false );
    btnConsole.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( !btnConsole.getSelection() && consoleWindow != null ) {
          consoleWindow.dispose();
          consoleWindow = null;
        }
        else {
          consoleWindow = new ConsoleWindow( getParent(), chart, tsContext() );
          consoleWindow.shell().addDisposeListener( aE -> {
            btnConsole.setSelection( false );
            consoleWindow = null;
          } );
        }
      }
    } );

    btnPrint = new Button( comp, SWT.PUSH );
    btnPrint.setText( STR_PRINT );
    btnPrint.setImage( iconManager().loadStdIcon( ICONID_DOCUMENT_PRINT, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    // btnPrint.addDisposeListener( aE -> btnPrint.getImage().dispose() );

    btnPrint.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        PrintDialog dialog = new PrintDialog( getShell(), SWT.NULL );
        PrinterData printerDefaults = new PrinterData();
        printerDefaults.scope = PrinterData.PAGE_RANGE;
        printerDefaults.orientation = PrinterData.LANDSCAPE;
        dialog.setPrinterData( printerDefaults );

        PrinterData printerData = dialog.open();

        if( printerData == null ) {
          return; // отказ от печати
        }
        Printer printer = new Printer( printerData );
        GC printerGc = createPrintGc( printer, new TsPoint( 5, 5 ), new TsPoint( 5, 5 ) );
        try {
          if( printer.startJob( "printChart" ) ) { //$NON-NLS-1$
            chart.print( printerGc );
            // напечатаем название шаблона
            Point chartSize = chart.getControl().getSize();
            Color oldColor = printerGc.getForeground();
            printerGc.setForeground( colorManager().getColor( ETsColor.BLACK ) );
            // String chartTitle = template.title().strip().length() > 0 ? template.title() : template.nmName();
            Point titleSize = printerGc.textExtent( chartTitle );
            printerGc.drawText( chartTitle, chartSize.x / 2 - titleSize.x / 2, (int)(chartSize.y * 0.05), true );
            printerGc.setForeground( oldColor );
            if( legendWindow != null ) {
              // напечатаем еще название шаблона отчетов
              Point p = legendWindow.shell().getLocation();
              p = toControl( p );
              Transform tr = new Transform( printer );
              printerGc.getTransform( tr );
              tr.translate( p.x, p.y );
              printerGc.setTransform( tr );
              tr.dispose();
              legendWindow.print( printerGc );
            }
            printer.endPage();
          }
        }
        finally {
          if( printerGc != null ) {
            printerGc.dispose();
            printer.endJob();
            printer.dispose();
          }
        }
      }
    } );

    CLabel l = new CLabel( comp, SWT.CENTER );
    l.setText( STR_SCALE_VALUE );

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

  void clear() {
    graphicInfoes.clear();
    axisInfoes.clear();
  }

  /**
   * Shows list of data at graphic component.
   *
   * @param aAnswer IList - list of data
   * @param aGraphicInfoes IList - list of graphic infoes
   * @param aAxisInfoes IStringMapEdit - Y axes infoes
   * @param aAggrStep ETimeUnit - aggregation step
   * @param aChartTitle String - chart title.
   * @param aFromBegin boolean true - from th begining, false - from the ending.
   */
  public void setReportAnswer( IList<IG2DataSet> aAnswer, IList<GraphicInfo> aGraphicInfoes,
      IStringMapEdit<YAxisInfo> aAxisInfoes, ETimeUnit aAggrStep, String aChartTitle, boolean aFromBegin ) {

    clear();
    // проверяем что есть смысл строить график
    if( aAnswer.isEmpty() ) {
      return;
    }
    chartTitle = aChartTitle;
    for( GraphicInfo gi : aGraphicInfoes ) {
      graphicInfoes.put( gi.id(), gi );
    }

    for( String axisId : aAxisInfoes.keys() ) {
      axisInfoes.put( axisId, aAxisInfoes.getByKey( axisId ) );
    }

    // создаем компоненту график
    createChart( aAnswer, aAggrStep, aFromBegin );
    Composite chartComp = chart.createControl( this );
    // наполняем ее данными отчета
    fillChartData( aAnswer );
    createYAxises( chart );
    createPlots();
    chartComp.setLayoutData( BorderLayout.CENTER );
    console = new G2ChartConsole( chart );
  }

  private void fillChartData( IList<IG2DataSet> aAnswer ) {
    for( int i = 0; i < aAnswer.size(); i++ ) {
      chart.dataSets().add( aAnswer.get( i ) );
    }
  }

  private void createChart( IList<IG2DataSet> aAnswer, ETimeUnit aAggrStep, boolean aFromBegin ) {
    TimeAxisTuner tuner = new TimeAxisTuner( tsContext() );
    // настройка шкалы времении
    axisTimeUnit = getAxisTimeUnit( aAggrStep );
    timeUnitCombo.setSelectedItem( axisTimeUnit );
    tuner.setTimeUnit( axisTimeUnit );
    // диапазон данных
    IG2DataSet dataSet = aAnswer.first();
    // настройка шкалы времении - диапазон значений
    TimeInterval visTimeInterval = visualTimeInterval( aFromBegin, dataSet, aAggrStep );
    tuner.setTimeInterval( visTimeInterval, false );
    IXAxisDef xAxisDef = tuner.createAxisDef();
    chart = (G2Chart)G2ChartUtils.createChart( tsContext() );
    chart.setXAxisDef( xAxisDef );
  }

  private static TimeInterval visualTimeInterval( boolean aFromBegin, IG2DataSet aDataSet, ETimeUnit aAggrStep ) {
    ETimeUnit axisTimeUnit = getAxisTimeUnit( aAggrStep );
    // настройка шкалы времении - диапазон значений
    long startTime = System.currentTimeMillis() - 12 * axisTimeUnit.timeInMills();
    long endTime = System.currentTimeMillis();
    if( !aDataSet.getValues( ITimeInterval.NULL ).isEmpty() ) {
      if( aFromBegin ) {
        startTime = aDataSet.getValues( ITimeInterval.NULL ).first().timestamp();
        endTime = startTime + 12 * axisTimeUnit.timeInMills();
      }
      else {
        endTime = aDataSet.getValues( ITimeInterval.NULL ).last().timestamp();
        startTime = endTime - 12 * axisTimeUnit.timeInMills();
      }
    }
    return new TimeInterval( startTime, endTime );

  }

  // настройка шкалы времении в зависимости от шага агрегации
  private static ETimeUnit getAxisTimeUnit( ETimeUnit aAggrStep ) {
    ETimeUnit retVal = ETimeUnit.MIN10;

    retVal = switch( aAggrStep ) {
      case DAY -> ETimeUnit.WEEK;
      case HOUR01 -> ETimeUnit.HOUR04;
      case HOUR02 -> ETimeUnit.HOUR08;
      case HOUR04 -> ETimeUnit.HOUR12;
      case HOUR08 -> ETimeUnit.HOUR12;
      case HOUR12 -> ETimeUnit.DAY;
      case MIN01 -> ETimeUnit.MIN10;
      case MIN05 -> ETimeUnit.MIN15;
      case MIN10 -> ETimeUnit.HOUR01;
      case MIN15 -> ETimeUnit.HOUR01;
      case MIN20 -> ETimeUnit.HOUR01;
      case MIN30 -> ETimeUnit.HOUR02;
      case SEC01 -> ETimeUnit.SEC10;
      case SEC02 -> ETimeUnit.SEC20;
      case SEC03 -> ETimeUnit.SEC30;
      case SEC05 -> ETimeUnit.MIN01;
      case SEC10 -> ETimeUnit.MIN01;
      case SEC15 -> ETimeUnit.MIN01;
      case SEC20 -> ETimeUnit.MIN01;
      case SEC30 -> ETimeUnit.MIN05;
      case WEEK -> ETimeUnit.HOUR12;
      case YEAR -> ETimeUnit.WEEK;
    };
    return retVal;
  }

  void createYAxises( IG2Chart aChart ) {
    for( YAxisInfo axisInfo : axisInfoes ) {
      double min = axisInfo.graphicInfoes().values().get( 0 ).minMax().left().doubleValue();
      double max = axisInfo.graphicInfoes().values().get( 0 ).minMax().right().doubleValue();
      // формат по умолчанию 2 знака
      EDisplayFormat displayFormat = EDisplayFormat.AS_INTEGER;
      for( GraphicInfo graphInfo : axisInfo.graphicInfoes() ) {
        if( graphInfo.minMax().left().doubleValue() < min ) {
          min = graphInfo.minMax().left().doubleValue();
        }
        if( graphInfo.minMax().right().doubleValue() > max ) {
          max = graphInfo.minMax().right().doubleValue();
        }
        EDisplayFormat graphDisplFmt = IStdG2GraphicRendererOptions.VALUES_DISPLAY_FORMAT
            .getValue( graphInfo.plotDef().rendererParams().params() ).asValobj();
        if( graphDisplFmt.ordinal() > displayFormat.ordinal() ) {
          displayFormat = graphDisplFmt;
        }
      }
      IYAxisDef yAxisDef = createYAxisDef( axisInfo.id(), min, max, displayFormat.format(), axisInfo.unitInfo() );
      aChart.yAxisDefs().add( yAxisDef );
    }
  }

  IYAxisDef createYAxisDef( String aId, double aMin, double aMax, String aFormatStr, Pair<String, String> aUnitInfo ) {
    YAxisTuner yTuner = new YAxisTuner( tsContext() );

    // yTuner.setStartValue( aMin );
    // yTuner.setEndValue( aMax );
    yTuner.setFormatString( aFormatStr );

    // double val = Math.abs( (aMax - aMin) / 10 );
    // int exp = calcExponent( val );

    AxisMarkupTuner mt = new AxisMarkupTuner( aMin, aMax );
    MarkUpInfo mi = mt.tuneAxisMarkup( aMin, aMax, 5, 15 );

    yTuner.setStartValue( mi.bgnValue );
    yTuner.setEndValue( mi.bgnValue + mi.step * mi.qttyOfSteps );
    yTuner.setStepValue( mi.step );

    yTuner.setTitle( aUnitInfo.right() );
    yTuner.setTitleOrientation( ETsOrientation.VERTICAL );
    yTuner.setTitleFont( new FontInfo( FONT_NAME, 18, false, false ) );

    return yTuner.createAxisDef( aId, aUnitInfo.right(), aUnitInfo.left() );
  }

  void createPlots() {
    // int idx = 0;
    for( GraphicInfo graphInfo : graphicInfoes ) {
      // PlotDefTuner plotTuner = new PlotDefTuner( tsContext() );
      // RGB plotColor = aTemplate.listParams().get( idx ).color().rgb();
      // int lineWidth = aTemplate.listParams().get( idx ).lineWidth();
      // EDisplayFormat displayFormat = aTemplate.listParams().get( idx ).displayFormat();
      // plotTuner.setLineInfo( TsLineInfo.ofWidth( lineWidth ) );
      // plotTuner.setRGBA( new RGBA( plotColor.red, plotColor.green, plotColor.blue, 255 ) );
      // plotTuner.setDisplayFormat( displayFormat );
      // IVtGraphParam graphParam = aTemplate.listParams().get( idx );
      // plotTuner.setSetPointsList( graphParam.setPoints() );

      IPlotDef plotDef = graphInfo.plotDef();// .createPlotDef( plotTuner );
      chart.plotDefs().add( plotDef );
      // idx++;
    }
  }

  /**
   * Обновляем всю компоненту
   */
  public void refresh() {
    chart.refresh();
  }

  /**
   * Возвращает подготовленный для печати графический контекст.
   * <p>
   *
   * @param aPrinter Printer - принтер
   * @param aLtMargins TsPoint - левый и верхний отступы в миллиметрах
   * @param aRbMargins TsPoint - правый и нижний отступы в миллиметрах
   * @return GC - подготовленный для печати графический контекст
   */
  private GC createPrintGc( Printer aPrinter, TsPoint aLtMargins, TsPoint aRbMargins ) {
    GC gc = new GC( aPrinter );

    Point printerDpi = aPrinter.getDPI();
    double dpp = Math.min( printerDpi.x / 24.5, printerDpi.y / 24.5 );
    int marginLeft = (int)(aLtMargins.x() * dpp);
    int marginTop = (int)(aLtMargins.y() * dpp);
    int marginRight = (int)(aRbMargins.x() * dpp);
    int marginBottom = (int)(aRbMargins.y() * dpp);

    Rectangle pr = aPrinter.getClientArea();
    pr.width -= (marginLeft + marginRight);
    pr.height -= (marginTop + marginBottom);

    Point cs = chart.getControl().getSize();

    float k = Math.min( (float)pr.width / cs.x, (float)pr.height / cs.y );

    Transform tr = new Transform( aPrinter );
    tr.translate( marginLeft, marginTop );
    tr.scale( k, k );
    gc.setTransform( tr );
    tr.dispose();

    return gc;
  }
}
