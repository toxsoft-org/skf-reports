package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.bricks.strid.idgen.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skf.reports.templates.service.impl.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Lifecycle manager for {@link GraphTemplateM5Model}.
 *
 * @author dima
 */
public class GraphTemplateM5LifecycleManager
    extends KM5LifecycleManagerBasic<IVtGraphTemplate, ISkConnection> {

  private static SimpleStridGenerator stridGenerator = new SimpleStridGenerator();

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster &lt;M&gt; - master object, may be <code>null</code>
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public GraphTemplateM5LifecycleManager( IM5Model<IVtGraphTemplate> aModel, ISkConnection aMaster ) {
    super( aModel, true, true, true, true, aMaster );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private IVtGraphTemplateService graphTemplateService() {
    return master().coreApi().getService( IVtGraphTemplateService.SERVICE_ID );
  }

  // ------------------------------------------------------------------------------------
  // M5LifecycleManager
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IVtGraphTemplate> aValues ) {
    IDtoFullObject dtoGraphTemplate = makeGraphTemplateDto( aValues, master() );
    return graphTemplateService().svs().validator().canCreateGraphTemplate( dtoGraphTemplate );
  }

  @Override
  protected IVtGraphTemplate doCreate( IM5Bunch<IVtGraphTemplate> aValues ) {
    IDtoFullObject dtoGraphTemplate = makeGraphTemplateDto( aValues, master() );
    IVtGraphTemplate retVal = graphTemplateService().createGraphTemplate( dtoGraphTemplate );
    return retVal;
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<IVtGraphTemplate> aValues ) {
    IDtoFullObject dtoGraphTemplate = makeGraphTemplateDto( aValues, master() );
    return graphTemplateService().svs().validator().canEditGraphTemplate( dtoGraphTemplate, aValues.originalEntity() );
  }

  @Override
  protected IVtGraphTemplate doEdit( IM5Bunch<IVtGraphTemplate> aValues ) {
    IDtoFullObject dtoGraphTemplate = makeGraphTemplateDto( aValues, master() );
    return graphTemplateService().editGraphTemplate( dtoGraphTemplate );
  }

  @Override
  protected ValidationResult doBeforeRemove( IVtGraphTemplate aEntity ) {
    return graphTemplateService().svs().validator().canRemoveGraphTemplate( aEntity.id() );
  }

  @Override
  protected void doRemove( IVtGraphTemplate aEntity ) {
    graphTemplateService().removeGraphTemplate( aEntity.id() );
  }

  @Override
  protected IList<IVtGraphTemplate> doListEntities() {
    return graphTemplateService().listGraphTemplates();
  }

  // ------------------------------------------------------------------------------------
  // Package API
  //

  static IDtoFullObject makeGraphTemplateDto( IM5Bunch<IVtGraphTemplate> aValues, ISkConnection aConnection ) {

    Skid skid = (aValues.originalEntity() == null) ? new Skid( IVtGraphTemplate.CLASS_ID, stridGenerator.nextId() )
        : aValues.originalEntity().skid();

    DtoFullObject dtoGraphTemplate = DtoFullObject.createDtoFullObject( skid, aConnection.coreApi() );
    dtoGraphTemplate.attrs().setValue( AID_NAME, aValues.getAsAv( AID_NAME ) );
    dtoGraphTemplate.attrs().setValue( AID_DESCRIPTION, aValues.getAsAv( AID_DESCRIPTION ) );
    dtoGraphTemplate.attrs().setValue( ATRID_AGGR_STEP, aValues.getAsAv( ATRID_AGGR_STEP ) );
    dtoGraphTemplate.attrs().setValue( ATRID_MAX_EXECUTION_TIME, aValues.getAsAv( ATRID_MAX_EXECUTION_TIME ) );

    IList<IVtGraphParam> paramsList = aValues.getAs( CLBID_TEMPLATE_PARAMS, IList.class );
    String paramsStr = VtGraphParam.KEEPER.coll2str( paramsList );
    dtoGraphTemplate.clobs().put( CLBID_TEMPLATE_PARAMS, paramsStr );
    // установим ему автора
    ISkUser currUser = ConnectionUtiles.getConnectedUser( aConnection.coreApi() );
    dtoGraphTemplate.links().map().put( LNKID_TEMPLATE_AUTHOR, new SkidList( currUser.skid() ) );

    return dtoGraphTemplate;
  }

}
