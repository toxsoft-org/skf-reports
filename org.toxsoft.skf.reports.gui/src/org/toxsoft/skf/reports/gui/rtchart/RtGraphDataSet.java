package org.toxsoft.skf.reports.gui.rtchart;

import static org.toxsoft.uskat.core.api.hqserv.ISkHistoryQueryServiceConstants.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.chart.api.IG2DataSet;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.temporal.*;
import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.utils.ReportTemplateUtilities;
import org.toxsoft.skf.reports.templates.service.IVtGraphParam;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Набор данных для графика реального времени.
 * <p>
 * Представляет собой список с фиксированным максимальнм значением элементов. Если при добавлении нового данного размер
 * списка превышает максимальный, то самый старый элемент удаляется.
 *
 * @author dima
 */
public class RtGraphDataSet
    extends Stridable
    implements IG2DataSet {

  String QUERY_PARAM_ID = "rt_graph_query_param_id"; //$NON-NLS-1$

  private final IListEdit<ITemporalAtomicValue> values   = new ElemArrayList<>();
  private final ISkReadCurrDataChannel          rtDataChannel;
  int                                           maxCount = 600 + 2;
  // 2023-08-18 mvk ---
  // private final Timer timer = new Timer();
  private final IVtGraphParam graphParam;

  /**
   * @return graphParam
   */
  public IVtGraphParam getGraphParam() {
    return graphParam;
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса IG2DataSet
  //
  /**
   * Создание набора данных реального времени
   *
   * @param aDisplay дисплей
   * @param aGraphParam описание rt data
   * @param aServerApi доступ к серверу
   * @param aHistoryData исторические данные этого параметра
   */

  public RtGraphDataSet( Display aDisplay, IVtGraphParam aGraphParam, ISkCoreApi aServerApi,
      ITimedList<?> aHistoryData ) {
    super( ReportTemplateUtilities.graphDataSetId( aGraphParam ) );
    TsNullArgumentRtException.checkNull( aDisplay );
    // получим название и описание параметра
    ISkObject myselfObj = aServerApi.objService().find( aGraphParam.gwid().skid() );
    setName( myselfObj.nmName() );
    setDescription( myselfObj.description() );
    graphParam = aGraphParam;

    // запросим историю параметра за последние 10 минут
    // ISkQueryProcessedData processData = aServerApi.hqService().createProcessedQuery( IOptionSet.NULL );
    // IStringMap<IDtoQueryParam> queryParams = queryParams( aGraphParam.gwid() );
    // processData.prepare( queryParams );
    // TimeInterval queryInterval =
    // new TimeInterval( System.currentTimeMillis() - 10 * 60 * 1000, System.currentTimeMillis() );
    //
    // values = new ElemArrayList<>();
    //
    // processData.genericChangeEventer().addListener( aSource -> {
    // ISkQueryProcessedData q = (ISkQueryProcessedData)aSource;
    // if( q.state() == ESkQueryState.READY ) {
    // IList<ITimedList<?>> requestAnswer = ReportTemplateUtilities.createResult( processData, queryParams );
    // ITimedList<?> historyData = requestAnswer.getOnly();
    // for( Object value : historyData ) {
    // if( value instanceof TemporalAtomicValue tav ) {
    // values.add( tav );
    // }
    // }
    // // только после поучения данных запускаем таймер
    // aRtChartPanel.init();
    // aRtChartPanel.start();
    // TimerTask repeatedTask = new TimerTask() {
    //
    // @Override
    // public void run() {
    // // обновим текущее значение
    // addCurrValue( rtDataChannel.getValue() );
    // }
    // };
    // timer.scheduleAtFixedRate( repeatedTask, 1000L, 1000L );
    // }
    // } );
    //
    // processData
    // .exec( new QueryInterval( EQueryIntervalType.OSOE, queryInterval.startTime(), queryInterval.endTime() ) );

    for( Object value : aHistoryData ) {
      if( value instanceof TemporalAtomicValue tav ) {
        values.add( tav );
      }
    }

    // создаем канал текущих данных
    IMap<Gwid, ISkReadCurrDataChannel> channelMap =
        aServerApi.rtdService().createReadCurrDataChannels( new GwidList( aGraphParam.gwid() ) );
    rtDataChannel = channelMap.getByKey( aGraphParam.gwid() );
    // запускаем таймер
    // запускаем таймер
    // 2023-08-18 mvk ---+++
    // TimerTask repeatedTask = new TimerTask() {
    //
    // @Override
    // public void run() {
    // // обновим текущее значение
    // addCurrValue( rtDataChannel.getValue() );
    // }
    // };
    // timer.scheduleAtFixedRate( repeatedTask, 1000L, 1000L );
    aDisplay.asyncExec( new Runnable() {

      @Override
      public void run() {
        if( !rtDataChannel.isOk() ) {
          // Канал завершил работу
          return;
        }
        // обновим текущее значение
        addCurrValue( rtDataChannel.getValue() );
        aDisplay.timerExec( 1000, this );
      }
    } );
  }

  private synchronized void addCurrValue( IAtomicValue aValue ) {
    long timestamp = System.currentTimeMillis();
    if( values.size() >= maxCount ) {
      values.removeByIndex( 0 );
    }
    values.add( new TemporalAtomicValue( timestamp, aValue ) );
  }

  private IStringMap<IDtoQueryParam> queryParams( Gwid aGwid ) {
    IStringMapEdit<IDtoQueryParam> result = new StringMap<>();
    ITsCombiFilterParams filter = ITsCombiFilterParams.ALL;
    IOptionSetEdit funcArgs = new OptionSet();
    // задаем интервал агрегации
    funcArgs.setInt( HQFUNC_ARG_AGGREGAION_INTERVAL, 1000 );
    IDtoQueryParam qParam = DtoQueryParam.create( aGwid, filter, HQFUNC_ID_AVERAGE, funcArgs );
    result.put( QUERY_PARAM_ID, qParam );
    return result;
  }

  @Override
  public void close() {
    // закрываем канал текущих данных
    rtDataChannel.close();
  }

  @Override
  public synchronized IList<ITemporalAtomicValue> getValues( ITimeInterval aInterval ) {
    if( aInterval.equals( ITimeInterval.NULL ) || aInterval.equals( ITimeInterval.WHOLE ) ) {
      IListEdit<ITemporalAtomicValue> retVal = new ElemArrayList<>( values );
      return retVal;
    }
    IListEdit<ITemporalAtomicValue> retVal = new ElemArrayList<>();
    for( ITemporalAtomicValue val : values ) {
      if( aInterval.startTime() <= val.timestamp() && val.timestamp() <= aInterval.endTime() ) {
        retVal.add( val );
      }
    }
    return retVal;
  }

  @Override
  public void prepare( ITimeInterval aInterval ) {
    maxCount = (int)(aInterval.duration() / 100) + 2;
  }

  @Override
  public synchronized Pair<ITemporalAtomicValue, ITemporalAtomicValue> locate( long aTimeStamp ) {
    ITemporalAtomicValue leftVal = ITemporalAtomicValue.NULL;
    ITemporalAtomicValue rightVal = ITemporalAtomicValue.NULL;
    for( ITemporalAtomicValue value : values ) {
      if( value.timestamp() <= aTimeStamp ) {
        leftVal = value;
      }
      else {
        rightVal = value;
        break;
      }
    }
    return new Pair<>( leftVal, rightVal );
  }

}
