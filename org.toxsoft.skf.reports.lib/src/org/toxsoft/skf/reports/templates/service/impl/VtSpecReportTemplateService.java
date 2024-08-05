package org.toxsoft.skf.reports.templates.service.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.templates.service.*;
//import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * {@link IVtSpecReportTemplateService} implementation.
 *
 * @author max
 */
public class VtSpecReportTemplateService
    extends AbstractSkService
    implements IVtSpecReportTemplateService {

  /**
   * Service creator singleton.
   */
  public static final ISkServiceCreator<AbstractSkService> CREATOR = VtSpecReportTemplateService::new;

  // private final ValidationSupport validationSupport = new ValidationSupport();
  // private final Eventer eventer = new Eventer();
  // private final ClassClaimingCoreValidator claimingValidator = new ClassClaimingCoreValidator();
  // private ISkConnection connection = null;

  /**
   * Constructor.
   *
   * @param aCoreApi {@link IDevCoreApi} - owner core API implementation
   */
  VtSpecReportTemplateService( IDevCoreApi aCoreApi ) {
    super( SERVICE_ID, aCoreApi );
    // validationSupport.addValidator( builtinValidator );
  }

  // ------------------------------------------------------------------------------------
  // AbstractSkCoreService
  //

  @Override
  protected void doInit( ITsContextRo aArgs ) {
    // create class for IVtSpecReportTemplate
    IDtoClassInfo reportTemplateCinf = internalCreateSpecReportTemplateClassDto();
    sysdescr().defineClass( reportTemplateCinf );
    objServ().registerObjectCreator( IVtSpecReportTemplate.CLASS_ID, VtSpecReportTemplate.CREATOR );
    //
    // sysdescr().svs().addValidator( claimingValidator );
    // objServ().svs().addValidator( claimingValidator );
    // linkService().svs().addValidator( claimingValidator );
    // clobService().svs().addValidator( claimingValidator );
  }

  @Override
  protected void doClose() {
    // nop
  }

  @Override
  protected boolean doIsClassClaimedByService( String aClassId ) {
    return switch( aClassId ) {
      case IVtSpecReportTemplate.CLASS_ID -> true;
      default -> false;
    };
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  /**
   * Creates DTO of {@link IVtSpecReportTemplate#CLASS_ID} class.
   *
   * @return {@link IDtoClassInfo} - {@link IVtSpecReportTemplate#CLASS_ID} class info
   */
  public static IDtoClassInfo internalCreateSpecReportTemplateClassDto() {
    DtoClassInfo cinf = new DtoClassInfo( IVtSpecReportTemplate.CLASS_ID, GW_ROOT_CLASS_ID, IOptionSet.NULL );
    // TODO need clarification
    OPDEF_SK_IS_SOURCE_CODE_DEFINED_CLASS.setValue( cinf.params(), AV_TRUE );
    OPDEF_SK_IS_SOURCE_USKAT_CORE_CLASS.setValue( cinf.params(), AV_TRUE );
    cinf.attrInfos().add( ATRINF_TITLE );
    cinf.attrInfos().add( ATRINF_HAS_SUMMARY );
    cinf.attrInfos().add( ATRINF_AGGR_STEP );
    cinf.attrInfos().add( ATRINF_MAX_EXECUTION_TIME );
    cinf.clobInfos().add( CLBINF_TEMPLATE_PARAMS );
    cinf.clobInfos().add( CLBINF_TEMPLATE_DESIGN );
    cinf.linkInfos().add( LNKINF_TEMPLATE_AUTHOR );
    return cinf;
  }

  private void pauseCoreValidation() {
    // sysdescr().svs().pauseValidator( claimingValidator );
    // objServ().svs().pauseValidator( claimingValidator );
    // linkService().svs().pauseValidator( claimingValidator );
    // clobService().svs().pauseValidator( claimingValidator );
  }

  private void resumeCoreValidation() {
    // sysdescr().svs().resumeValidator( claimingValidator );
    // objServ().svs().resumeValidator( claimingValidator );
    // linkService().svs().resumeValidator( claimingValidator );
    // clobService().svs().resumeValidator( claimingValidator );
  }

  // ------------------------------------------------------------------------------------
  // IVtSpecReportTemplateService
  //

  // @Override
  // public ITsValidationSupport<IVtSpecReportTemplateServiceValidator> svs() {
  // return validationSupport;
  // }
  //
  // @Override
  // public ITsEventer<IVtSpecReportTemplateServiceListener> eventer() {
  // return eventer;
  // }

  @Override
  public IList<IVtSpecReportTemplate> listSpecReportTemplates() {
    IList<IVtSpecReportTemplate> ll = objServ().listObjs( IVtSpecReportTemplate.CLASS_ID, false );
    return new StridablesList<>( ll );
  }

  @Override
  public IVtSpecReportTemplate findSpecReportTemplate( String aTempId ) {
    TsNullArgumentRtException.checkNull( aTempId );
    return coreApi().objService().find( new Skid( IVtSpecReportTemplate.CLASS_ID, aTempId ) );
  }

  @Override
  public IVtSpecReportTemplate createSpecReportTemplate( IDtoFullObject aDtoReportTemplate ) {
    // TsValidationFailedRtException.checkError( validationSupport.canCreateReportTemplate( aDtoReportTemplate ) );
    pauseCoreValidation();
    try {
      IVtSpecReportTemplate retVal = DtoFullObject.defineFullObject( coreApi(), aDtoReportTemplate );
      // установим ему автора
      Skid currUser = coreApi().getCurrentUserInfo().userSkid();
      coreApi().linkService().defineLink( retVal.skid(), LNKID_TEMPLATE_AUTHOR, null, new SkidList( currUser ) );

      return retVal;
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public IVtSpecReportTemplate editSpecReportTemplate( IDtoFullObject aDtoReportTemplate ) {
    TsNullArgumentRtException.checkNull( aDtoReportTemplate );
    TsIllegalArgumentRtException.checkFalse( aDtoReportTemplate.classId().equals( IVtSpecReportTemplate.CLASS_ID ) );
    IVtSpecReportTemplate oldReportTemplate = objServ().find( aDtoReportTemplate.skid() );
    TsItemNotFoundRtException.checkNull( oldReportTemplate );
    // TsValidationFailedRtException
    // .checkError( validationSupport.canEditReportTemplate( aDtoReportTemplate, oldReportTemplate ) );
    pauseCoreValidation();
    try {
      return DtoFullObject.defineFullObject( coreApi(), aDtoReportTemplate );
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public void removeSpecReportTemplate( String aReportTemplateId ) {
    // TsValidationFailedRtException.checkError( svs().validator().canRemoveReportTemplate( aReportTemplateId ) );
    pauseCoreValidation();
    try {
      coreApi().objService().removeObject( new Skid( IVtSpecReportTemplate.CLASS_ID, aReportTemplateId ) );
    }
    finally {
      resumeCoreValidation();
    }

  }

}
