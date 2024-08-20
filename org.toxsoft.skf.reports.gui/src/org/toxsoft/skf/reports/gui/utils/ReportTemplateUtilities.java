package org.toxsoft.skf.reports.gui.utils;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.skf.reports.gui.utils.ISkResources.*;
import static org.toxsoft.uskat.core.api.hqserv.ISkHistoryQueryServiceConstants.*;

import java.text.*;
import java.util.*;
import java.util.List;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.jasperreports.gui.main.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.graphics.colors.*;
import org.toxsoft.core.tsgui.graphics.lines.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.temporal.*;
import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.reports.chart.utils.gui.dataset.*;
import org.toxsoft.skf.reports.chart.utils.gui.panels.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.impl.dto.*;

import net.sf.jasperreports.engine.*;

/**
 * Утилитный класс для работы с шаблонами
 *
 * @author Max
 * @author dima
 */
public class ReportTemplateUtilities {

  public static String JR_PARAM_FIELD_PREFIX = "$F{";
  public static String JR_PARAM_PARAM_PREFIX = "$P{";

  public static String JR_PARAM_FIELD_FORMAT = JR_PARAM_FIELD_PREFIX + "%s}";
  public static String JR_PARAM_PARAM_FORMAT = JR_PARAM_PARAM_PREFIX + "%s}";

  // по умолчанию берем данные за последние 6 час
  static TimeInterval initValues =
      new TimeInterval( System.currentTimeMillis() - 6 * 60L * 60L * 1000L, System.currentTimeMillis() );

  private static boolean IS_SAME_TIME_IN_EACH_COLUMN = true;

  private static final String FMT_TIME_COLUMN_VALUE = "dd.MM HH:mm:ss"; //$NON-NLS-1$

  protected static ChartPanel popupChart;

  /**
   * Формат идентификатора параметра запроса данных.
   */
  static final String QUERY_PARAM_ID_FORMAT = "param%d"; //$NON-NLS-1$

  /**
   * Формат идентификатора столбца модели - в качестве параметра - номер столбца, начиная с 0 [int].
   */
  public static final String MODEL_COLUMN_ID_FORMAT = "column%d"; //$NON-NLS-1$

  /**
   * Формат идентификатора столбца времени модели - в качестве параметра - номер столбца, начиная с 0 [int].
   */
  public static final String MODEL_TIME_COLUMN_ID_FORMAT = "timeColumn%d"; //$NON-NLS-1$

  /**
   * Формирует параметры запроса на основе информации из шаблона отчёта
   *
   * @param aReportTemplate IVtReportTemplate - шаблон отчёта
   * @return IStringMap<IDtoQueryParam> - массив параметров отчёта (одна сущность на одно запрашиваемое данное) -
   *         идентификатор каждой сущности имеет формат paramN, где N - номер, начиная с нуля, совпадающий с порядком
   *         параметров в шаблоне (param0, param1, param2 ...)
   */
  public static IStringMap<IDtoQueryParam> formQueryParams( IVtReportTemplate aReportTemplate ) {
    return template2Query( aReportTemplate.listParams(),
        Integer.valueOf( (int)aReportTemplate.aggrStep().timeInMills() ) );
  }

  /**
   * Формирует параметры запроса на основе информации из шаблона графика
   *
   * @param aGraphTemplate { {@link IVtGraphTemplate } - шаблон графика
   * @return IStringMap<IDtoQueryParam> - массив параметров отчёта (одна сущность на одно запрашиваемое данное) -
   *         идентификатор каждой сущности имеет формат paramN, где N - номер, начиная с нуля, совпадающий с порядком
   *         параметров в шаблоне (param0, param1, param2 ...)
   */
  public static IStringMap<IDtoQueryParam> formQueryParams( IVtGraphTemplate aGraphTemplate ) {
    return template2Query( aGraphTemplate.listParams(),
        Integer.valueOf( (int)aGraphTemplate.aggrStep().timeInMills() ) );
  }

  /**
   * По списку параметров шаблона формирует карту параметров для запроса к сервису запросов
   *
   * @param aTemplateParams параметры шаблона
   * @param aAggrStep шаг агрегации
   * @return карта параметров запроса к одноименному сервису
   */
  public static IStringMap<IDtoQueryParam> template2Query( String aQueryParamprefix,
      IList<? extends IVtTemplateParam> aTemplateParams, Integer aAggrStep ) {
    IStringMapEdit<IDtoQueryParam> result = new StringMap<>();

    for( int i = 0; i < aTemplateParams.size(); i++ ) {
      IVtTemplateParam param = aTemplateParams.get( i );

      Gwid gwid = param.gwid();
      ITsCombiFilterParams filter = ITsCombiFilterParams.ALL;

      EAggregationFunc aggrFunc = param.aggrFunc();
      String funcId = convertFunc( aggrFunc );

      IOptionSetEdit funcArgs = new OptionSet();
      // задаем интервал агрегации
      funcArgs.setInt( HQFUNC_ARG_AGGREGAION_INTERVAL, aAggrStep.intValue() );

      IDtoQueryParam qParam = DtoQueryParam.create( gwid, filter, funcId, funcArgs );

      result.put( aQueryParamprefix + String.format( QUERY_PARAM_ID_FORMAT, Integer.valueOf( i ) ), qParam );
    }
    return result;
  }

  /**
   * По списку параметров шаблона формирует карту параметров для запроса к сервису запросов
   *
   * @param aTemplateParams параметры шаблона
   * @param aAggrStep шаг агрегации
   * @return карта параметров запроса к одноименному сервису
   */
  public static IStringMap<IDtoQueryParam> template2Query( IList<? extends IVtTemplateParam> aTemplateParams,
      Integer aAggrStep ) {
    return template2Query( TsLibUtils.EMPTY_STRING, aTemplateParams, aAggrStep );
  }

  /**
   * Creates M5 Model according to report template params.
   *
   * @param aReportTemplate IVtReportTemplate - report template params.
   * @return IM5Model - M5 Model according to report template params.
   */
  public static IM5Model<IStringMap<IAtomicValue>> createM5ModelForTemplate( IVtReportTemplate aReportTemplate ) {
    return new ReportM5Model( aReportTemplate, IS_SAME_TIME_IN_EACH_COLUMN );
  }

  /**
   * Creates M5 data provider according to report template params and report data.
   *
   * @param aReportTemplate IVtReportTemplate - report template params.
   * @param aReportData IList - report data.
   * @return IM5ItemsProvider - M5 data provider.
   */
  public static IM5ItemsProvider<IStringMap<IAtomicValue>> createM5ItemProviderForTemplate(
      IVtReportTemplate aReportTemplate, IList<ITimedList<?>> aReportData ) {
    return new ReportM5ItemProvider( aReportTemplate, aReportData, IS_SAME_TIME_IN_EACH_COLUMN );
  }

  /**
   * Перевод значение enum в id понятный сервису запросов
   *
   * @param aAggrFunc enum
   * @return String
   */
  public static String convertFunc( EAggregationFunc aAggrFunc ) {
    switch( aAggrFunc ) {
      case AVERAGE:
        return HQFUNC_ID_AVERAGE;
      case MAX:
        return HQFUNC_ID_MAX;
      case MIN:
        return HQFUNC_ID_MIN;
      case COUNT:
        return HQFUNC_ID_COUNT;
      case SUM:
        return HQFUNC_ID_SUM;
      default:
        break;
    }
    return null;
  }

  /**
   * По шаблону графика и результату запроса к сервису отчетов создает список наборов данных для графической компоненты
   *
   * @param aGraphTemplate {@link IVtGraphTemplate} - шаблон графика
   * @param aReportData - результат запроса к сервису отчетов
   * @return - список наборов данных для графика
   */
  public static IList<IG2DataSet> createG2DataSetList( IVtGraphTemplate aGraphTemplate,
      IList<ITimedList<?>> aReportData ) {
    IListEdit<IG2DataSet> retVal = new ElemArrayList<>();
    IList<IVtGraphParam> graphParams = aGraphTemplate.listParams();
    // создаем нужные наборы данных
    for( int i = 0; i < graphParams.size(); i++ ) {
      IVtGraphParam graphParam = graphParams.get( i );
      String gdsId = graphDataSetId( graphParam );
      G2HistoryDataSet dataSet = new G2HistoryDataSet( gdsId );
      retVal.add( dataSet );
      // наполняем его данными
      ITimedList<?> timedList = aReportData.get( i );
      dataSet.setValues( convertList2List( timedList ) );
    }
    return retVal;
  }

  /**
   * Выдает id для набора данных одного графика. Использует gwid параметра и функцию агрегации.
   *
   * @param aGraphParam {@link IVtGraphParam } - описание одного параметра графика
   * @return строка id набора данных
   */
  public static String graphDataSetId( IVtGraphParam aGraphParam ) {
    return String.format( "%s_%s_%s_%s", aGraphParam.gwid().classId(), aGraphParam.gwid().strid(), //$NON-NLS-1$
        aGraphParam.gwid().propId(), aGraphParam.aggrFunc().id() );
  }

  /**
   * Из набора данных результата запроса готовит набор данных одного графика
   *
   * @param aTimedList ответ сервиса данных
   * @return набор данных одного графика
   */
  public static IList<ITemporalAtomicValue> convertList2List( ITimedList<?> aTimedList ) {
    IListEdit<ITemporalAtomicValue> retVal = new ElemArrayList<>();
    for( Object value : aTimedList ) {
      if( value instanceof TemporalAtomicValue tav ) {
        retVal.add( tav );
      }
    }
    return retVal;
  }

  public static JRDataSource createReportDetailDataSource( IList<IVtSpecReportParam> aFieldParams,
      IList<ITimedList<?>> aFieldData, boolean aHasSummary ) {

    List<Map<String, IAtomicValue>> mapList = new ArrayList<>();

    if( aFieldParams.size() == 0 || aFieldData.size() == 0 ) {
      Map<String, IAtomicValue> val = new HashMap<>();
      val.put( "1", AvUtils.avStr( "value" ) ); //$NON-NLS-1$//$NON-NLS-2$
      mapList.add( val );
      // Формируем dataset с одной строкой
      return new ReportDetailDataSource( mapList );
    }

    ReportM5ItemProvider provider =
        new ReportM5ItemProvider( aFieldData, aHasSummary, IS_SAME_TIME_IN_EACH_COLUMN, aFieldParams );

    IList<IStringMap<IAtomicValue>> items = provider.listItems();

    // int number = 1;
    for( IStringMap<IAtomicValue> item : items ) {
      Map<String, IAtomicValue> rowValues = new HashMap<>();
      mapList.add( rowValues );
      for( int i = 0; i < aFieldParams.size(); i++ ) {
        String id = String.format( MODEL_COLUMN_ID_FORMAT, Integer.valueOf( i ) );

        IAtomicValue columnValue = item.getByKey( id );
        rowValues.put( getPureJrParamIdFromTemplateJrParamId( aFieldParams.get( i ).jrParamId() ), columnValue );

      }

      // rowValues.put( ROW_NUMBER_FILED, AvUtils.avInt( number ) );
      // number++;
    }

    // Формируем dataset
    return new ReportDetailDataSource( mapList );
  }

  /**
   * Возвращает очищенный идентификатор параметра (поля) JR шаблона из идентификатора, который используется в s5 шаблоне
   *
   * @param aTemplateJrParamId String - идентификатор, который используется в s5 шаблоне
   * @return String - очищенный идентификатор параметра (поля) JR шаблона
   */
  public static String getPureJrParamIdFromTemplateJrParamId( String aTemplateJrParamId ) {
    return aTemplateJrParamId.substring( 3, aTemplateJrParamId.length() - 1 );
  }

  /**
   * Standart M5 model of a report, fields have the same meaning as parametors of a report template
   *
   * @author max
   */
  static class ReportM5Model
      extends M5Model<IStringMap<IAtomicValue>> {

    /**
     * Model Id.
     */
    public static final String MODEL_ID_FORMAT = "org.toxsoft.uskat.reptempl.utils.ReportM5Model.%s"; //$NON-NLS-1$

    private IVtReportTemplate reportTemplate;

    /**
     * Конструктор модели по шаблону отчёта.
     *
     * @param aReportTemplate IVtReportTemplate - report template.
     * @param aIsSameTimeInEachColumn boolean - признак того, что время всех элементов в одной строке отчёта - одно
     *          (один столбец времени).
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public ReportM5Model( IVtReportTemplate aReportTemplate, boolean aIsSameTimeInEachColumn ) {
      super( generateId( aReportTemplate ), (Class)IStringMap.class );

      reportTemplate = aReportTemplate;

      IListEdit<IM5FieldDef<IStringMap<IAtomicValue>, ?>> fDefs = new ElemArrayList<>();

      IList<IVtReportParam> reportParams = aReportTemplate.listParams();

      if( aIsSameTimeInEachColumn ) {
        String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( 0 ) );

        M5AttributeFieldDef<IStringMap<IAtomicValue>> timeField =
            createFieldDef( timeId, STR_N_TIME_COLUMN, STR_N_TIME_COLUMN, EDisplayFormat.AS_INTEGER );
        fDefs.add( timeField );
      }

      for( int i = 0; i < reportParams.size(); i++ ) {
        IVtReportParam param = reportParams.get( i );

        String fieldName = param.title();
        String fieldDescr = param.description();
        EDisplayFormat displayFormat = param.displayFormat();

        if( !aIsSameTimeInEachColumn ) {
          String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( i ) );
          M5AttributeFieldDef<IStringMap<IAtomicValue>> jointModelTimeField =
              createFieldDef( timeId, String.format( FMT_N_TIME_COLUMN, fieldName ),
                  String.format( FMT_N_TIME_COLUMN, fieldDescr ), displayFormat );
          fDefs.add( jointModelTimeField );
        }

        String id = String.format( MODEL_COLUMN_ID_FORMAT, Integer.valueOf( i ) );

        M5AttributeFieldDef<IStringMap<IAtomicValue>> jointModelField =
            createFieldDef( id, fieldName, fieldDescr, displayFormat );
        fDefs.add( jointModelField );
      }

      addFieldDefs( fDefs );
    }

    /**
     * Returns report template.
     *
     * @return IVtReportTemplate - report template.
     */
    public IVtReportTemplate getReportTemplate() {
      return reportTemplate;
    }

    public static String generateId( IVtReportTemplate aReportTemplate ) {
      return String.format( MODEL_ID_FORMAT, aReportTemplate.id() );
    }

    /**
     * Создаёт описание поля модели отчёта
     *
     * @param aId String - идентификатор поля
     * @param aNmName String - отображаемое имя поля
     * @param aDescr String - описание поля
     * @param aDisplayFormat EDisplayFormat - формат отображения значений поля
     * @return M5AttributeFieldDef - описание поля модели отчёта.
     */
    private static M5AttributeFieldDef<IStringMap<IAtomicValue>> createFieldDef( String aId, String aNmName,
        String aDescr, EDisplayFormat aDisplayFormat ) {

      M5AttributeFieldDef<IStringMap<IAtomicValue>> fDef = new M5AttributeFieldDef<>( aId, STRING ) {

        @Override
        protected IAtomicValue doGetFieldValue( IStringMap<IAtomicValue> aEntity ) {
          return aEntity.findByKey( id() );
        }

      };

      fDef.setNameAndDescription( aNmName, aDescr );
      fDef.setDefaultValue( IAtomicValue.NULL );
      fDef.setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
      return fDef;
    }
  }

  static class ReportM5ItemProvider
      extends M5DefaultItemsProvider<IStringMap<IAtomicValue>> {

    private IList<ITimedList<?>>      reportData;
    private boolean                   isSameTimeInEachColumn;
    private boolean                   hasSummary;
    IList<? extends IVtTemplateParam> reportParams;

    /**
     * Конструктор поставщика данных по шаблону и результату запроса.
     *
     * @param aReportTemplate IVtReportTemplate - шаблон запроса.
     * @param aReportData IList - результат запроса.
     * @param aIsSameTimeInEachColumn boolean - признак того, что время всех элементов в одной строке отчёта - одно
     *          (один столбец времени).
     */
    public ReportM5ItemProvider( IVtReportTemplate aReportTemplate, IList<ITimedList<?>> aReportData,
        boolean aIsSameTimeInEachColumn ) {
      super();
      reportData = aReportData;
      isSameTimeInEachColumn = aIsSameTimeInEachColumn;
      hasSummary = aReportTemplate.hasSummary();
      reportParams = aReportTemplate.listParams();
      formItems();
    }

    /**
     * Конструктор поставщика данных по результату запроса .
     *
     * @param aReportData IList - результат запроса.
     * @param aIsSameTimeInEachColumn boolean - признак того, что время всех элементов в одной строке отчёта - одно
     *          (один столбец времени).
     * @param aHasSummary boolean - признак наличия итоговой строки отчёта
     * @param aFieldParams IList - параметры отчёта
     */
    public ReportM5ItemProvider( IList<ITimedList<?>> aReportData, boolean aIsSameTimeInEachColumn, boolean aHasSummary,
        IList<? extends IVtTemplateParam> aFieldParams ) {
      super();
      reportData = aReportData;
      isSameTimeInEachColumn = aIsSameTimeInEachColumn;
      hasSummary = aHasSummary;
      reportParams = aFieldParams;
      formItems();
    }

    private void formItems() {
      items().clear();

      if( reportParams.size() == 0 ) {
        return;
      }

      // количество строк - максимальное количество элементов в истории одного из параметров
      int rowCount = 0;
      int longestColumnIndex = 0;

      IListEdit<IAggrigationFunction> aggrFuncs = new ElemArrayList<>();

      for( int i = 0; i < reportParams.size(); i++ ) {
        ITimedList<?> timedList = reportData.get( i );
        if( timedList.size() > rowCount ) {
          rowCount = timedList.size();
          longestColumnIndex = i;
        }

        if( hasSummary ) {
          IVtTemplateParam param = reportParams.get( i );
          aggrFuncs.add( createAggrigationFunction( param.aggrFunc() ) );
        }
        else {
          aggrFuncs.add( IAggrigationFunction.EMPTY_AFFR_FUNC );
        }
      }

      // набор со временем самого длинного столбца
      IStringListEdit timeColumnValue = new StringArrayList();
      if( isSameTimeInEachColumn ) {
        ITimedList<?> longestTimedList = reportData.get( longestColumnIndex );
        for( int j = 0; j < rowCount; j++ ) {
          ITemporalAtomicValue val = (ITemporalAtomicValue)longestTimedList.get( j );
          String strTime = convertTime( val );
          timeColumnValue.add( strTime );
        }
      }

      for( int j = 0; j < rowCount; j++ ) {
        IStringMapEdit<IAtomicValue> rowValues = new StringMap<>();
        if( isSameTimeInEachColumn ) {
          String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( 0 ) );
          rowValues.put( timeId, AvUtils.avStr( timeColumnValue.get( j ) ) );
        }

        for( int i = 0; i < reportParams.size(); i++ ) {
          IVtTemplateParam param = reportParams.get( i );
          EDisplayFormat displayFormat = param.displayFormat();

          ITimedList<?> timedList = reportData.get( i );

          ITemporalAtomicValue val =
              j < timedList.size() ? (TemporalAtomicValue)timedList.get( j ) : EMPTY_TEMPORAL_ATOMIC_VALUE;

          if( !isSameTimeInEachColumn ) {
            String strTime = convertTime( val );
            String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( i ) );
            rowValues.put( timeId, AvUtils.avStr( strTime ) );
          }

          String strVal = convertValueToView( val.value(), displayFormat );
          String id = String.format( MODEL_COLUMN_ID_FORMAT, Integer.valueOf( i ) );

          rowValues.put( id, AvUtils.avStr( strVal ) );

          // вызов аггрегации для текущего значения
          aggrFuncs.get( i ).nextValue( val.value() );
        }

        items().add( rowValues );
      }

      if( hasSummary ) {

        IStringMapEdit<IAtomicValue> summaryRowValues = new StringMap<>();
        if( isSameTimeInEachColumn ) {
          String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( 0 ) );
          summaryRowValues.put( timeId, AvUtils.avStr( SUMMARY_FIELD_NAME_STR ) );
        }

        for( int i = 0; i < reportParams.size(); i++ ) {
          if( !isSameTimeInEachColumn ) {
            String timeId = String.format( MODEL_TIME_COLUMN_ID_FORMAT, Integer.valueOf( i ) );
            summaryRowValues.put( timeId, AvUtils.avStr( SUMMARY_FIELD_NAME_STR ) );
          }

          EDisplayFormat displayFormat = reportParams.get( i ).displayFormat();
          String id = String.format( MODEL_COLUMN_ID_FORMAT, Integer.valueOf( i ) );
          summaryRowValues.put( id,
              AvUtils.avStr( convertValueToView( aggrFuncs.get( i ).getCurrentResult(), displayFormat ) ) );
        }

        items().add( summaryRowValues );
      }
    }
  }

  /**
   * Implementaiotn of Single data argument.
   *
   * @author max
   */
  static class DtoQueryParamImpl2
      implements IDtoQueryParam {

    private Gwid dataGwid;

    private ITsCombiFilterParams filter;

    private String funcId;

    private IOptionSetEdit funcArgs;

    public DtoQueryParamImpl2( Gwid aGwid, ITsCombiFilterParams aFilter, String aFuncId, IOptionSetEdit aFuncArgs ) {
      dataGwid = aGwid;
      filter = aFilter;
      funcId = aFuncId;
      funcArgs = aFuncArgs;
    }

    @Override
    public Gwid dataGwid() {
      return dataGwid;
    }

    @Override
    public ITsCombiFilterParams filterParams() {
      return filter;
    }

    @Override
    public String funcId() {
      return funcId;
    }

    @Override
    public IOptionSet funcArgs() {
      return funcArgs;
    }

  }

  /**
   * Формирует строковое представление атомарного значения с учётом формата.
   *
   * @param aVal IAtomicValue - атомарное значение.
   * @param aDisplayFormat EDisplayFormat - формат отображения атомарного значения.
   * @return String - строковое представление атомарного значения.
   */
  public static String convertValueToView( IAtomicValue aVal, EDisplayFormat aDisplayFormat ) {
    if( aVal != null && aVal.isAssigned() ) {
      if( aVal.atomicType() == EAtomicType.FLOATING ) {
        return String.format( aDisplayFormat.format(), Double.valueOf( aVal.asDouble() ) );
      }
      return aVal.asString();
    }

    return DEFAULT_EMPTY_VALUE_STR;
  }

  /**
   * Формирует строковое представление времени из значения.
   *
   * @param aVal ITemporalAtomicValue - значение с меткой времени.
   * @return String - строковое представление времени из значения.
   */
  public static String convertTime( ITemporalAtomicValue aVal ) {
    if( aVal.timestamp() > 0 ) {
      DateFormat formatter = new SimpleDateFormat( FMT_TIME_COLUMN_VALUE );
      return formatter.format( new Date( aVal.timestamp() ) );
    }
    return DEFAULT_EMPTY_VALUE_STR;
  }

  /**
   * Формирует набор данных из ответа сервера
   *
   * @param aProcessData ответ сервера
   * @param aQueryParams параметры запроса
   * @return набор данных
   */
  public static IList<ITimedList<?>> createResult( ISkQueryProcessedData aProcessData,
      IStringMap<IDtoQueryParam> aQueryParams ) {

    IListEdit<ITimedList<?>> result = new ElemArrayList<>();
    for( String paramKey : aQueryParams.keys() ) {
      ITimedList<?> data = aProcessData.getArgData( paramKey );
      result.add( data );
    }
    return result;
  }

  /**
   * Создаёт тестовый набор данных.
   *
   * @param aQueryParams IStringMap - параметры, для которых создаются тестовые данные.
   * @return IList - тестовый набор данных.
   */
  @SuppressWarnings( "unused" )
  public static IList<ITimedList<?>> createTestResult( IStringMap<IDtoQueryParam> aQueryParams ) {
    IListEdit<ITimedList<?>> result = new ElemArrayList<>();

    long curTime = System.currentTimeMillis();

    for( IDtoQueryParam p : aQueryParams.values() ) {
      ITimedListEdit<TemporalAtomicValue> timedList = new TimedList<>();
      result.add( timedList );
      for( int i = 2000; i > 0; i-- ) {

        TemporalAtomicValue val1 = new TemporalAtomicValue( curTime - i * 1000L, AvUtils.avInt( i ) );
        timedList.add( val1 );
      }

    }
    return result;
  }

  /**
   * Создает Chart панель для графика одного параметра
   *
   * @param aContext контекст
   * @param aParent родительский компонент
   * @param aParamGwid параметр отображаемый на графике
   * @param aTitle название параметра
   * @param aDescription описание параметра
   * @return панель для графика
   */
  public static ChartPanel popupChart( ITsGuiContext aContext, Composite aParent, Gwid aParamGwid, String aTitle,
      String aDescription ) {
    IVtGraphTemplate selTemplate = createTemplate( aParamGwid, aTitle, aDescription );
    // формируем запрос к одноименному сервису
    IStringMap<IDtoQueryParam> queryParams = ReportTemplateUtilities.formQueryParams( selTemplate );
    ISkConnectionSupplier connSupp = aContext.get( ISkConnectionSupplier.class );

    ISkQueryProcessedData processData =
        connSupp.defConn().coreApi().hqService().createProcessedQuery( IOptionSet.NULL );

    processData.prepare( queryParams );
    TimeInterval popupChartIntvl =
        new TimeInterval( System.currentTimeMillis() - 6 * 60L * 60L * 1000L, System.currentTimeMillis() );
    processData
        .exec( new QueryInterval( EQueryIntervalType.OSOE, popupChartIntvl.startTime(), popupChartIntvl.endTime() ) );

    // асинхронное получение данных
    processData.genericChangeEventer().addListener( aSource -> {
      ISkQueryProcessedData q = (ISkQueryProcessedData)aSource;
      if( q.state() == ESkQueryState.READY ) {
        IList<ITimedList<?>> requestAnswer = createResult( processData, queryParams );
        IList<IG2DataSet> graphData = createG2SelfUploDataSetList( selTemplate, requestAnswer, connSupp.defConn() );
        for( IG2DataSet ds : graphData ) {
          if( ds instanceof G2SelfUploadHistoryDataSetNew ) {
            ((G2SelfUploadHistoryDataSetNew)ds).addListener( aSource1 -> popupChart.refresh() );
          }
        }
        // popupChart.setReportAnswer( graphData, selTemplate, false );
        setReportAnswerToChart( popupChart, graphData, selTemplate, false );
        popupChart.requestLayout();
      }
    } );

    // создаем новую панель
    popupChart = new ChartPanel( aParent, aContext );
    return popupChart;
  }

  /**
   * Устанавливает в график данные, полученные по шаблону.
   *
   * @param aTargetChart ChartPanel - целевой график, в который будуд устанавливаться данные.
   * @param aGraphData IList - данные, полученные по шаблону, предназначенные для установки в график.
   * @param aTemplate IVtGraphTemplate - шаблон, по которому получены данные.
   * @param aFromBegin boolean - показывать данные сначала.
   */
  public static void setReportAnswerToChart( ChartPanel aTargetChart, IList<IG2DataSet> aGraphData,
      IVtGraphTemplate aTemplate, boolean aFromBegin ) {

    IStringMapEdit<YAxisInfo> axisInfoes = new StringMap<>();
    IListEdit<GraphicInfo> graphicInfoes = new ElemArrayList<>();

    for( int i = 0; i < aGraphData.size(); i++ ) {
      IVtGraphParam param = aTemplate.listParams().get( i );
      IList<ITemporalAtomicValue> values = aGraphData.get( i ).getValues( ITimeInterval.NULL );
      Pair<Double, Double> minMax = calcMinMax( values );

      String graphDataSetId = graphDataSetId( param );

      YAxisInfo axisInfo;
      if( axisInfoes.hasKey( param.unitId() ) ) {
        axisInfo = axisInfoes.getByKey( param.unitId() );
      }
      else {
        axisInfo = new YAxisInfo( graphDataSetId, new Pair<>( param.unitId(), param.unitName() ) );
        axisInfoes.put( param.unitId(), axisInfo );
      }

      // chart.dataSets().add( aAnswer.get( i ) );

      IStridable graphStridable = new Stridable( graphDataSetId, param.title(), param.description() );

      GraphicInfo graphInfo =
          new GraphicInfo( graphStridable, axisInfo.id(), graphDataSetId, minMax, param.isLadder() );

      PlotDefTuner plotTuner = new PlotDefTuner( aTargetChart.tsContext() );
      RGB plotColor = param.color().rgb();
      int lineWidth = param.lineWidth();
      EDisplayFormat displayFormat = param.displayFormat();
      plotTuner.setLineInfo( TsLineInfo.ofWidth( lineWidth ) );
      plotTuner.setRGBA( new RGBA( plotColor.red, plotColor.green, plotColor.blue, 255 ) );
      plotTuner.setDisplayFormat( displayFormat );

      plotTuner.setSetPointsList( param.setPoints() );
      graphInfo.createPlotDef( plotTuner );

      graphicInfoes.add( graphInfo );
      axisInfo.putGraphicInfo( graphInfo );
    }

    String chartTitle = aTemplate.title().strip().length() > 0 ? aTemplate.title() : aTemplate.nmName();
    aTargetChart.setReportAnswer( aGraphData, graphicInfoes, axisInfoes, aTemplate.aggrStep(), chartTitle, aFromBegin );
  }

  /**
   * Вычисление min & max диапазона значений
   *
   * @param aValues значения выборки с сервиса данных
   * @return пара значений {@link Pair}
   */
  public static Pair<Double, Double> calcMinMax( IList<ITemporalAtomicValue> aValues ) {

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

  /**
   * Создаёт экземпляр аггрегатора по типу аггрегации.
   *
   * @param aAggrType EAggregationFunc - тип аггреации.
   * @return IAggrigationFunction - экземпляр аггрегатора.
   */
  public static IAggrigationFunction createAggrigationFunction( EAggregationFunc aAggrType ) {
    return switch( aAggrType ) {
      case AVERAGE -> new IAggrigationFunction.AverageAggrFunction();
      case MAX -> new IAggrigationFunction.RangeFunction( false );
      case MIN -> new IAggrigationFunction.RangeFunction( true );
      case SUM, COUNT -> new IAggrigationFunction.SumAggrFunction();
      default -> IAggrigationFunction.EMPTY_AFFR_FUNC;
    };
  }

  /**
   * Создаеет пустой шаблон графика
   *
   * @param aParamGwid {@Gwid }
   * @param aTitle название
   * @param aDescription описание
   * @return пустой шаблон
   */
  public static IVtGraphTemplate createTemplate( Gwid aParamGwid, String aTitle, String aDescription ) {
    IVtGraphTemplate retVal = new IVtGraphTemplate() {

      /**
       * @return { @link ETimeUnit} - time step of aggregation
       */
      @Override
      public ETimeUnit aggrStep() {
        return ETimeUnit.MIN01;
      }

      @Override
      public String nmName() {
        return aTitle;
      }

      @Override
      public String description() {
        return aDescription;
      }

      @Override
      public String strid() {
        return Skid.NONE.strid();
      }

      @Override
      public Skid skid() {
        return Skid.NONE;
      }

      @Override
      public IMappedSkids rivets() {
        return IMappedSkids.EMPTY;
      }

      @Override
      public String readableName() {
        return aTitle;
      }

      @Override
      public String id() {
        return Skid.NONE.strid();
      }

      @Override
      public Skid getSingleLinkSkid( String aLinkId ) {
        return Skid.NONE;
      }

      @Override
      public <T extends ISkObject> T getSingleLinkObj( String aLinkId ) {
        return null;
      }

      @Override
      public ISkidList getRivetRevSkids( String aClassId, String aRivetId ) {
        return ISkidList.EMPTY;
      }

      @Override
      public <T extends ISkObject> IList<T> getRivetRevObjs( String aClassId, String aRivetId ) {
        return IList.EMPTY;
      }

      @Override
      public ISkidList getLinkSkids( String aLinkId ) {
        return null;
      }

      @Override
      public ISkidList getLinkRevSkids( String aClassId, String aLinkId ) {
        return ISkidList.EMPTY;
      }

      @Override
      public <T extends ISkObject> IList<T> getLinkRevObjs( String aClassId, String aLinkId ) {
        return IList.EMPTY;
      }

      @Override
      public <T extends ISkObject> IList<T> getLinkObjs( String aLinkId ) {
        return IList.EMPTY;
      }

      @Override
      public String getClob( String aClobId, String aDefaultValue ) {
        return null;
      }

      @Override
      public ISkCoreApi coreApi() {
        return null;
      }

      @Override
      public String classId() {
        return Skid.NONE.classId();
      }

      @Override
      public IOptionSet attrs() {
        return IOptionSet.NULL;
      }

      @Override
      public IList<IVtGraphParam> listParams() {
        return new ElemArrayList<>( new IVtGraphParam() {

          @Override
          public Gwid gwid() {
            return aParamGwid;
          }

          @Override
          public String title() {
            return aTitle;
          }

          @Override
          public String description() {
            return aDescription;
          }

          @Override
          public EAggregationFunc aggrFunc() {
            return EAggregationFunc.AVERAGE;
          }

          @Override
          public EDisplayFormat displayFormat() {
            return EDisplayFormat.TWO_DIGIT;
          }

          @Override
          public ETsColor color() {
            return ETsColor.BLACK;
          }

          @Override
          public int lineWidth() {
            return 2;
          }

          @Override
          public String unitId() {
            return "Y"; //$NON-NLS-1$
          }

          @Override
          public String unitName() {
            return TsLibUtils.EMPTY_STRING;
          }

          @Override
          public boolean isLadder() {
            return false;
          }

          @Override
          public IStringList setPoints() {
            return IStringList.EMPTY;
          }

        } );
      }

      @Override
      public ISkUser author() {
        return null;
      }

      @Override
      public IAtomicValue readRtdataIfOpen( String aRtdataId ) {
        return null;
      }

      @Override
      public boolean writeRtdataIfOpen( String aRtdataId, IAtomicValue aValue ) {
        return false;
      }
    };
    return retVal;
  }

  /**
   * По шаблону графика и результату запроса к сервису отчетов создает список наборов данных для графической компоненты
   *
   * @param aGraphTemplate {@link IVtGraphTemplate} - шаблон графика
   * @param aReportData - результат запроса к сервису отчетов
   * @param aConnection - соединение с сервером
   * @return - список наборов данных для графика
   */
  public static IList<IG2DataSet> createG2SelfUploDataSetList( IVtGraphTemplate aGraphTemplate,
      IList<ITimedList<?>> aReportData, ISkConnection aConnection ) {
    IListEdit<IG2DataSet> retVal = new ElemArrayList<>();
    IList<IVtGraphParam> graphParams = aGraphTemplate.listParams();
    // создаем нужные наборы данных
    for( int i = 0; i < graphParams.size(); i++ ) {
      IVtGraphParam graphParam = graphParams.get( i );
      String gdsId = ReportTemplateUtilities.graphDataSetId( graphParam );

      G2SelfUploadHistoryDataSetNew dataSet =
          new G2SelfUploadHistoryDataSetNew( aConnection, gdsId, new IDataSetParam() {

            @Override
            public Gwid gwid() {
              return graphParam.gwid();
            }

            @Override
            public String aggrFuncId() {
              return ReportTemplateUtilities.convertFunc( graphParam.aggrFunc() );
            }

            @Override
            public int aggrStep() {
              return (int)aGraphTemplate.aggrStep().timeInMills();
            }

          } );
      retVal.add( dataSet );
      // наполняем его данными
      ITimedList<?> timedList = aReportData.get( i );
      dataSet.setValues( ReportTemplateUtilities.convertList2List( timedList ) );
    }
    return retVal;
  }

  /**
   * Объект нулевого (несуществующего) значения.
   */
  private static ITemporalAtomicValue EMPTY_TEMPORAL_ATOMIC_VALUE = new EmptyTemporalAtomicValue();

  /**
   * Класс нулевого (несуществующего) значения.
   *
   * @author max
   */
  static class EmptyTemporalAtomicValue
      implements ITemporalAtomicValue {

    @Override
    public long timestamp() {
      return 0;
    }

    @Override
    public int compareTo( ITemporalValue<IAtomicValue> aThat ) {
      if( aThat == null ) {
        throw new NullPointerException();
      }
      // объект этого класса один - ITemporalValue.NULL. Равен только самому себе, меньше всех остальных
      return aThat == this ? 0 : -1;
    }

    @Override
    public IAtomicValue value() {
      return IAtomicValue.NULL;
    }

  }

}
