package ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5;

import static org.toxsoft.uskat.core.ISkHardConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.api.IVtTemplateEditorServiceHardConstants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.bricks.strid.idgen.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;
//import org.toxsoft.uskat.s5.utils.S5ConnectionUtils;
import org.toxsoft.uskat.core.impl.dto.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;
import ru.toxsoft.skt.vetrol.ws.core.templates.api.impl.*;

/**
 * Lifecycle manager for {@link VtReportTemplateM5Model}.
 *
 * @author dima
 */
public class VtReportTemplateM5LifecycleManager
    extends KM5LifecycleManagerBasic<IVtReportTemplate, ISkConnection> {

  private static SimpleStridGenaretor stridGenerator = new SimpleStridGenaretor();

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster &lt;M&gt; - master object, may be <code>null</code>
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public VtReportTemplateM5LifecycleManager( IM5Model<IVtReportTemplate> aModel, ISkConnection aMaster ) {
    super( aModel, true, true, true, true, aMaster );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private IVtReportTemplateService reportTemplateService() {
    return master().coreApi().getService( IVtReportTemplateService.SERVICE_ID );
  }

  // ------------------------------------------------------------------------------------
  // M5LifecycleManager
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IVtReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, master() );
    return reportTemplateService().svs().validator().canCreateReportTemplate( dtoReportTemplate );
  }

  @Override
  protected IVtReportTemplate doCreate( IM5Bunch<IVtReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, master() );
    return reportTemplateService().createReportTemplate( dtoReportTemplate );
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<IVtReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, master() );
    return reportTemplateService().svs().validator().canEditReportTemplate( dtoReportTemplate,
        aValues.originalEntity() );
  }

  @Override
  protected IVtReportTemplate doEdit( IM5Bunch<IVtReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, master() );
    return reportTemplateService().editReportTemplate( dtoReportTemplate );
  }

  @Override
  protected ValidationResult doBeforeRemove( IVtReportTemplate aEntity ) {
    return reportTemplateService().svs().validator().canRemoveReportTemplate( aEntity.id() );
  }

  @Override
  protected void doRemove( IVtReportTemplate aEntity ) {
    reportTemplateService().removeReportTemplate( aEntity.id() );
  }

  @Override
  protected IList<IVtReportTemplate> doListEntities() {
    return reportTemplateService().listReportTemplates();
  }

  // ------------------------------------------------------------------------------------
  // Package API
  //

  static IDtoFullObject makeReportTemplateDto( IM5Bunch<IVtReportTemplate> aValues, ISkConnection aConnection ) {
    Skid skid = new Skid( IVtReportTemplate.CLASS_ID,
        aValues.originalEntity() == null ? stridGenerator.nextId() : aValues.originalEntity().id() );
    DtoFullObject dtoReportTemplate = DtoFullObject.createDtoFullObject( skid, aConnection.coreApi() );
    dtoReportTemplate.attrs().setValue( AID_NAME, aValues.getAsAv( AID_NAME ) );
    dtoReportTemplate.attrs().setValue( AID_DESCRIPTION, aValues.getAsAv( AID_DESCRIPTION ) );
    dtoReportTemplate.attrs().setValue( ATRID_HAS_SUMMARY, aValues.getAsAv( ATRID_HAS_SUMMARY ) );
    dtoReportTemplate.attrs().setValue( ATRID_AGGR_STEP, aValues.getAsAv( ATRID_AGGR_STEP ) );
    dtoReportTemplate.attrs().setValue( ATRID_MAX_EXECUTION_TIME, aValues.getAsAv( ATRID_MAX_EXECUTION_TIME ) );

    IList<IVtReportParam> paramsList = aValues.getAs( CLBID_TEMPLATE_PARAMS, IList.class );
    String paramsStr = VtReportParam.KEEPER.coll2str( paramsList );
    dtoReportTemplate.clobs().put( CLBID_TEMPLATE_PARAMS, paramsStr );
    // установим ему автора
    ISkUser currUser = ConnectionUtiles.getConnectedUser( aConnection.coreApi() );
    dtoReportTemplate.links().map().put( LNKID_TEMPLATE_AUTHOR, new SkidList( currUser.skid() ) );

    return dtoReportTemplate;
  }

}
