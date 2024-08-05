package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.impl.*;
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
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Lifecycle manager for {@link SpecReportTemplateM5Model}.
 *
 * @author max
 */
public class SpecReportTemplateM5LifecycleManager
    extends M5LifecycleManager<IVtSpecReportTemplate, ITsGuiContext> {

  private static String PREFIX_TEMPLATE = "template"; //$NON-NLS-1$

  private ISkConnection conn;

  private static IStridGenerator stridGenerator =
      new UuidStridGenerator( UuidStridGenerator.createState( PREFIX_TEMPLATE ) );

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster &lt;M&gt; - master object, may be <code>null</code>
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public SpecReportTemplateM5LifecycleManager( IM5Model<IVtSpecReportTemplate> aModel, ITsGuiContext aMaster ) {
    super( aModel, true, true, true, true, aMaster );

    ISkConnectionSupplier connSup = aMaster.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private IVtSpecReportTemplateService reportTemplateService() {
    return conn.coreApi().getService( IVtSpecReportTemplateService.SERVICE_ID );
  }

  // ------------------------------------------------------------------------------------
  // M5LifecycleManager
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IVtSpecReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, conn );
    return ValidationResult.SUCCESS;// reportTemplateService().svs().validator().canCreateReportTemplate(
                                    // dtoReportTemplate );
  }

  @Override
  protected void doSetupNewItemValues( IM5BunchEdit<IVtSpecReportTemplate> aValues ) {
    FileDialog d = new FileDialog( master().get( Shell.class ), SWT.OPEN );

    String file = d.open();
    try {
      FileInputStream fr = new FileInputStream( file );
      byte[] result = fr.readAllBytes();

      aValues.set( CLBID_TEMPLATE_DESIGN, AvUtils.avStr( new String( result ) ) );

      master().put( "jr.test", new String( result ) );
    }
    catch( IOException e ) {
      e.printStackTrace();
    }
  }

  @Override
  protected IVtSpecReportTemplate doCreate( IM5Bunch<IVtSpecReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, conn );
    return reportTemplateService().createSpecReportTemplate( dtoReportTemplate );
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<IVtSpecReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, conn );
    return ValidationResult.SUCCESS;// reportTemplateService().svs().validator().canEditReportTemplate(
                                    // dtoReportTemplate,
    // aValues.originalEntity() );
  }

  @Override
  protected IVtSpecReportTemplate doEdit( IM5Bunch<IVtSpecReportTemplate> aValues ) {
    IDtoFullObject dtoReportTemplate = makeReportTemplateDto( aValues, conn );
    return reportTemplateService().editSpecReportTemplate( dtoReportTemplate );
  }

  @Override
  protected ValidationResult doBeforeRemove( IVtSpecReportTemplate aEntity ) {
    return ValidationResult.SUCCESS;// reportTemplateService().svs().validator().canRemoveReportTemplate( aEntity.id()
                                    // );
  }

  @Override
  protected void doRemove( IVtSpecReportTemplate aEntity ) {
    reportTemplateService().removeSpecReportTemplate( aEntity.id() );
  }

  @Override
  protected IList<IVtSpecReportTemplate> doListEntities() {
    return reportTemplateService().listSpecReportTemplates();
  }

  // ------------------------------------------------------------------------------------
  // Package API
  //

  static IDtoFullObject makeReportTemplateDto( IM5Bunch<IVtSpecReportTemplate> aValues, ISkConnection aConnection ) {
    Skid skid = new Skid( IVtSpecReportTemplate.CLASS_ID,
        aValues.originalEntity() == null ? stridGenerator.nextId() : aValues.originalEntity().id() );
    DtoFullObject dtoReportTemplate = DtoFullObject.createDtoFullObject( skid, aConnection.coreApi() );
    dtoReportTemplate.attrs().setValue( AID_NAME, aValues.getAsAv( AID_NAME ) );
    dtoReportTemplate.attrs().setValue( AID_DESCRIPTION, aValues.getAsAv( AID_DESCRIPTION ) );
    dtoReportTemplate.attrs().setValue( ATRID_HAS_SUMMARY, aValues.getAsAv( ATRID_HAS_SUMMARY ) );
    dtoReportTemplate.attrs().setValue( ATRID_AGGR_STEP, aValues.getAsAv( ATRID_AGGR_STEP ) );
    dtoReportTemplate.attrs().setValue( ATRID_MAX_EXECUTION_TIME, aValues.getAsAv( ATRID_MAX_EXECUTION_TIME ) );

    IList<IVtSpecReportParam> paramsList = aValues.getAs( CLBID_TEMPLATE_PARAMS, IList.class );
    String paramsStr = VtSpecReportParam.KEEPER.coll2str( paramsList );
    dtoReportTemplate.clobs().put( CLBID_TEMPLATE_PARAMS, paramsStr );

    String designStr = aValues.getAsAv( CLBID_TEMPLATE_DESIGN ).asString();

    System.out.println( "Template after edit: " + designStr );

    String design64 = Base64.getEncoder().encodeToString( designStr.getBytes() );

    dtoReportTemplate.clobs().put( CLBID_TEMPLATE_DESIGN, design64 );

    // установим ему автора
    ISkUser currUser = ConnectionUtiles.getConnectedUser( aConnection.coreApi() );
    dtoReportTemplate.links().map().put( LNKID_TEMPLATE_AUTHOR, new SkidList( currUser.skid() ) );

    return dtoReportTemplate;
  }

}
