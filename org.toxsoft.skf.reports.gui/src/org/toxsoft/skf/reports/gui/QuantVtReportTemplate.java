package org.toxsoft.skf.reports.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.reports.gui.km5.*;
import org.toxsoft.skf.reports.gui.panels.valed.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skf.reports.templates.service.impl.*;
import org.toxsoft.uskat.core.api.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * The libtary quant.
 *
 * @author hazard157
 */
public class QuantVtReportTemplate
    extends AbstractQuant
    implements ISkCoreExternalHandler {

  /**
   * Constructor.
   */
  public QuantVtReportTemplate() {
    super( QuantVtReportTemplate.class.getSimpleName() );

    SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );
    SkCoreUtils.registerSkServiceCreator( VtSpecReportTemplateService.CREATOR );

    // dima 26.05.23 падает в этом месте если запускать в SkIDE вместе с другими плагинами
    // max 29.05.23 поставил
    TsValobjUtils.registerKeeperIfNone( EAggregationFunc.KEEPER_ID, EAggregationFunc.KEEPER );
    TsValobjUtils.registerKeeperIfNone( EDisplayFormat.KEEPER_ID, EDisplayFormat.KEEPER );
    TsValobjUtils.registerKeeperIfNone( ETimeUnit.KEEPER_ID, ETimeUnit.KEEPER );
    TsValobjUtils.registerKeeperIfNone( VtGraphParamsList.KEEPER_ID, VtGraphParamsList.KEEPER );

    TsValobjUtils.registerKeeperIfNone( EJrParamSourceType.KEEPER_ID, EJrParamSourceType.KEEPER );

    KM5Utils.registerContributorCreator( KM5TemplateContributor.CREATOR );
    // register as initialize listener
    SkCoreUtils.registerCoreApiHandler( this );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {

    // add standart events formatters
    // ISkModJournalEventFormattersRegistry eventFormattersRegistry =
    // aWinContext.containsKey( ISkModJournalEventFormattersRegistry.class )
    // ? aWinContext.get( ISkModJournalEventFormattersRegistry.class )
    // : new DefaultMwsModJournalEventFormattersRegistry();
    //
    // aWinContext.set( ISkModJournalEventFormattersRegistry.class, eventFormattersRegistry );
    //
    // // Обработка событий создания/удаления шаблонов
    // GraphTemplateStdEventFormatter graphTemplateStdEventFormatter = new GraphTemplateStdEventFormatter();
    // Gwid evGwid = Gwid.createEvent( IVtReportTemplate.CLASS_ID,
    // IVtTemplateEditorServiceHardConstants.EVID_GRAPH_TEMPLATE_CREATED );
    // eventFormattersRegistry.registerFomatter( evGwid, graphTemplateStdEventFormatter );
    // evGwid = Gwid.createEvent( IVtReportTemplate.CLASS_ID,
    // IVtTemplateEditorServiceHardConstants.EVID_GRAPH_TEMPLATE_REMOVED );
    // eventFormattersRegistry.registerFomatter( evGwid, graphTemplateStdEventFormatter );

  }

  /**
   * Класс осуществляющий форматирование стандартных событий шаблона графиков
   *
   * @author dima
   */
  // static class GraphTemplateStdEventFormatter
  // extends AbstractJournalEventFormatter {
  //
  // @Override
  // protected String doFormatShortText( SkEvent aEvent, ISkClassInfo aSkClass, IDtoEventInfo aEvInfo,
  // ISkObject aAttrObject ) {
  // boolean isCreated = true;
  // // тут определяем что за событие
  // if( aEvent.paramValues().hasKey( IVtTemplateEditorServiceHardConstants.EVPRMID_COMMENT ) ) {
  // isCreated = false;
  // }
  // // формируем описание события
  // StringBuilder sb = new StringBuilder();
  //
  // if( isCreated ) {
  // sb.append( String.format( "Создан шаблон графика '%s'", aAttrObject.nmName() ) );
  // }
  // else {
  // sb.append( String.format( "Удален шаблон графика '%s'", aAttrObject.nmName() ) );
  // }
  // return sb.toString();
  // }
  //
  // @Override
  // protected String doFormatLongText( SkEvent aEvent, ISkClassInfo aSkClass, IDtoEventInfo aEvInfo,
  // ISkObject aAttrObject ) {
  // boolean isCreated = true;
  // IAtomicValue commentVal = IAtomicValue.NULL;
  // // тут определяем что за событие
  // if( aEvent.paramValues().hasKey( IVtTemplateEditorServiceHardConstants.EVPRMID_COMMENT ) ) {
  // commentVal = aEvent.paramValues().findByKey( IVtTemplateEditorServiceHardConstants.EVPRMID_COMMENT );
  // isCreated = false;
  // }
  // // формируем описание события
  // StringBuilder sb = new StringBuilder();
  //
  // if( isCreated ) {
  // sb.append( String.format( "Создан шаблон графика '%s'", aAttrObject.nmName() ) );
  // }
  // else {
  // sb.append( String.format( "Удален шаблон графика '%s'.\n Комментарий: ", aAttrObject.nmName(),
  // commentVal.asString() ) );
  // }
  // return sb.toString();
  // }
  // }
  //
  // static abstract class AbstractJournalEventFormatter
  // implements ISkModJournalEventFormatter {
  //
  // @Override
  // public String formatShortText( SkEvent aEvent, ITsGuiContext aContext ) {
  // ISkConnectionSupplier conSupp = aContext.get( ISkConnectionSupplier.class );
  // ISkConnection conn = conSupp.defConn();
  //
  // // Получаем его класс
  // ISkClassInfo skClass = conn.coreApi().sysdescr().findClassInfo( aEvent.eventGwid().classId() );
  // // Описание события
  // IDtoEventInfo evInfo = skClass.events().list().findByKey( aEvent.eventGwid().propId() );
  // // Получаем объект источника события
  // ISkObject evSrcObject = conn.coreApi().objService().find( aEvent.eventGwid().skid() );
  //
  // return doFormatShortText( aEvent, skClass, evInfo, evSrcObject );
  // }
  //
  // @Override
  // public String formatLongText( SkEvent aEvent, ITsGuiContext aContext ) {
  // ISkConnectionSupplier conSupp = aContext.get( ISkConnectionSupplier.class );
  // ISkConnection conn = conSupp.defConn();
  //
  // // Получаем его класс
  // ISkClassInfo skClass = conn.coreApi().sysdescr().findClassInfo( aEvent.eventGwid().classId() );
  // // Описание события
  // IDtoEventInfo evInfo = skClass.events().list().findByKey( aEvent.eventGwid().propId() );
  // // Получаем объект источника события
  // ISkObject evSrcObject = conn.coreApi().objService().find( aEvent.eventGwid().skid() );
  //
  // return doFormatLongText( aEvent, skClass, evInfo, evSrcObject );
  // }
  //
  // protected abstract String doFormatShortText( SkEvent aEvent, ISkClassInfo aSkClass, IDtoEventInfo aEvInfo,
  // ISkObject aAttrObject );
  //
  // protected abstract String doFormatLongText( SkEvent aEvent, ISkClassInfo aSkClass, IDtoEventInfo aEvInfo,
  // ISkObject aAttrObject );
  // }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IReportsGuiConstants.init( aWinContext );

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    if( !vcReg.hasFactory( ValedGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedAvValobjGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedSkidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedSkidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedAvValobjSkidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( SpecValedAvValobjGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( SpecValedAvValobjGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( SpecValedGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( SpecValedGwidEditor.FACTORY );
    }
  }

  @Override
  public void processSkCoreInitialization( IDevCoreApi aCoreApi ) {
    // register abilities
    aCoreApi.userService().abilityManager().defineAbility( IReportsGuiConstants.ABILITY_ACCESS_TEMPLATE_EDITOR );
  }

}
