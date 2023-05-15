package ru.toxsoft.skt.vetrol.ws.core.templates.api.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.api.IVtTemplateEditorServiceHardConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.api.impl.IVtResources.*;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.events.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.bricks.strio.chario.*;
import org.toxsoft.core.tslib.bricks.strio.chario.impl.*;
import org.toxsoft.core.tslib.bricks.strio.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.reports.gui.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.impl.*;
//import org.toxsoft.uskat.s5.utils.S5ConnectionUtils;
import org.toxsoft.uskat.core.impl.dto.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

/**
 * {@link IVtReportTemplateService} implementation.
 *
 * @author dima
 */
public class VtReportTemplateService
    extends AbstractSkService
    implements IVtReportTemplateService {

  /**
   * Service creator singleton.
   */
  public static final ISkServiceCreator<AbstractSkService> CREATOR = VtReportTemplateService::new;

  /**
   * {@link IVtReportTemplateService#svs()} implementation.
   *
   * @author dima
   */
  static class ValidationSupport
      extends AbstractTsValidationSupport<IVtReportTemplateServiceValidator>
      implements IVtReportTemplateServiceValidator {

    @Override
    public IVtReportTemplateServiceValidator validator() {
      return this;
    }

    @Override
    public ValidationResult canCreateReportTemplate( IDtoFullObject aReportTemplateDto ) {
      TsNullArgumentRtException.checkNull( aReportTemplateDto );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtReportTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canCreateReportTemplate( aReportTemplateDto ) );
      }
      return vr;
    }

    @Override
    public ValidationResult canEditReportTemplate( IDtoFullObject aReportTemplateDto,
        IVtReportTemplate aOldReportTemplate ) {
      TsNullArgumentRtException.checkNulls( aReportTemplateDto, aOldReportTemplate );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtReportTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canEditReportTemplate( aReportTemplateDto, aOldReportTemplate ) );
      }
      return vr;
    }

    @Override
    public ValidationResult canRemoveReportTemplate( String aReportTemplateId ) {
      TsNullArgumentRtException.checkNull( aReportTemplateId );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtReportTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canRemoveReportTemplate( aReportTemplateId ) );
      }
      return vr;
    }

  }

  /**
   * {@link IVtReportTemplateService#eventer()} implementation.
   *
   * @author dima
   */
  class Eventer
      extends AbstractTsEventer<IVtReportTemplateServiceListener> {

    private boolean isPendingReportTemplates = false;

    @Override
    protected void doClearPendingEvents() {
      isPendingReportTemplates = false;
    }

    @Override
    protected void doFirePendingEvents() {
      if( isPendingReportTemplates ) {
        reallyFireReportTemplate( ECrudOp.LIST, null );
      }
    }

    @Override
    protected boolean doIsPendingEvents() {
      return isPendingReportTemplates;
    }

    private void reallyFireReportTemplate( ECrudOp aOp, String aReportTemplateId ) {
      for( IVtReportTemplateServiceListener l : listeners() ) {
        try {
          l.onReportTemplateChanged( coreApi(), aOp, aReportTemplateId );
        }
        catch( Exception ex ) {
          LoggerUtils.errorLogger().error( ex );
        }
      }
    }

    void fireReportTemplateChanged( ECrudOp aOp, String aReportTemplateId ) {
      if( isFiringPaused() ) {
        isPendingReportTemplates = true;
        return;
      }
      reallyFireReportTemplate( aOp, aReportTemplateId );
    }

  }

  /**
   * Builtin service validator.
   */
  private final IVtReportTemplateServiceValidator builtinValidator = new IVtReportTemplateServiceValidator() {

    @Override
    public ValidationResult canCreateReportTemplate( IDtoFullObject aReportTemplateDto ) {
      // check precondition
      if( !aReportTemplateDto.skid().classId().equals( IVtReportTemplate.CLASS_ID ) ) {
        return ValidationResult.error( FMT_ERR_NOT_REPORT_TEMPLATE_DPU, aReportTemplateDto.classId(),
            IVtReportTemplate.CLASS_ID );
      }
      // check if parameters list is empty
      // first, get string from Clob
      String templParamsStr =
          aReportTemplateDto.clobs().getByKey( IVtTemplateEditorServiceHardConstants.CLBID_TEMPLATE_PARAMS );
      // next, convert string to collection
      ICharInputStream charInputStream = new CharInputStreamString( templParamsStr );
      IStrioReader sr = new StrioReader( charInputStream );
      IList<IVtReportParam> params = VtReportParam.KEEPER.readColl( sr );
      if( params == null || params.isEmpty() ) {
        return ValidationResult.error( MSG_ERR_NO_PARAMS );
      }
      return ValidationResult.SUCCESS;
    }

    @Override
    public ValidationResult canEditReportTemplate( IDtoFullObject aReportTemplateDto,
        IVtReportTemplate aOldReportTemplate ) {
      ValidationResult vr = ValidationResult.SUCCESS;
      // check precondition
      if( !aReportTemplateDto.skid().classId().equals( IVtReportTemplate.CLASS_ID ) ) {
        return ValidationResult.error( FMT_ERR_NOT_REPORT_TEMPLATE_DPU, aReportTemplateDto.classId(),
            IVtReportTemplate.CLASS_ID );
      }
      // check if parameters list is empty
      // first, get string from Clob
      String templParamsStr =
          aReportTemplateDto.clobs().getByKey( IVtTemplateEditorServiceHardConstants.CLBID_TEMPLATE_PARAMS );
      // next, convert string to collection
      ICharInputStream charInputStream = new CharInputStreamString( templParamsStr );
      IStrioReader sr = new StrioReader( charInputStream );
      IList<IVtReportParam> params = VtReportParam.KEEPER.readColl( sr );
      if( params == null || params.isEmpty() ) {
        return ValidationResult.error( MSG_ERR_NO_PARAMS );
      }
      return vr;
    }

    @Override
    public ValidationResult canRemoveReportTemplate( String aReportTemplateId ) {
      // TODO check if current user can right to remove this template
      // ???
      return ValidationResult.SUCCESS;
    }

  };

  private final ValidationSupport          validationSupport = new ValidationSupport();
  private final Eventer                    eventer           = new Eventer();
  private final ClassClaimingCoreValidator claimingValidator = new ClassClaimingCoreValidator();
  private ISkConnection                    connection        = null;

  /**
   * Constructor.
   *
   * @param aCoreApi {@link IDevCoreApi} - owner core API implementation
   */
  VtReportTemplateService( IDevCoreApi aCoreApi ) {
    super( SERVICE_ID, aCoreApi );
    validationSupport.addValidator( builtinValidator );
  }

  // ------------------------------------------------------------------------------------
  // AbstractSkCoreService
  //

  @Override
  protected void doInit( ITsContextRo aArgs ) {
    // create class for IVtReportTemplate
    IDtoClassInfo reportTemplateCinf = internalCreateReportTemplateClassDto();
    sysdescr().defineClass( reportTemplateCinf );
    objServ().registerObjectCreator( IVtReportTemplate.CLASS_ID, VtReportTemplate.CREATOR );
    //
    sysdescr().svs().addValidator( claimingValidator );
    objServ().svs().addValidator( claimingValidator );
    linkService().svs().addValidator( claimingValidator );
    clobService().svs().addValidator( claimingValidator );
  }

  @Override
  protected void doClose() {
    // nop
  }

  @Override
  protected boolean doIsClassClaimedByService( String aClassId ) {
    return switch( aClassId ) {
      case IVtReportTemplate.CLASS_ID -> true;
      default -> false;
    };
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  /**
   * Creates DTO of {@link IVtReportTemplate#CLASS_ID} class.
   *
   * @return {@link IDtoClassInfo} - {@link IVtReportTemplate#CLASS_ID} class info
   */
  public static IDtoClassInfo internalCreateReportTemplateClassDto() {
    DtoClassInfo cinf = new DtoClassInfo( IVtReportTemplate.CLASS_ID, GW_ROOT_CLASS_ID, IOptionSet.NULL );
    // TODO need clarification
    OPDEF_SK_IS_SOURCE_CODE_DEFINED_CLASS.setValue( cinf.params(), AV_TRUE );
    OPDEF_SK_IS_SOURCE_USKAT_CORE_CLASS.setValue( cinf.params(), AV_TRUE );
    cinf.attrInfos().add( ATRINF_TITLE );
    cinf.attrInfos().add( ATRINF_HAS_SUMMARY );
    cinf.attrInfos().add( ATRINF_AGGR_STEP );
    cinf.attrInfos().add( ATRINF_MAX_EXECUTION_TIME );
    cinf.clobInfos().add( CLBINF_TEMPLATE_PARAMS );
    cinf.linkInfos().add( LNKINF_TEMPLATE_AUTHOR );
    return cinf;
  }

  private void pauseCoreValidation() {
    sysdescr().svs().pauseValidator( claimingValidator );
    objServ().svs().pauseValidator( claimingValidator );
    linkService().svs().pauseValidator( claimingValidator );
    clobService().svs().pauseValidator( claimingValidator );
  }

  private void resumeCoreValidation() {
    sysdescr().svs().resumeValidator( claimingValidator );
    objServ().svs().resumeValidator( claimingValidator );
    linkService().svs().resumeValidator( claimingValidator );
    clobService().svs().resumeValidator( claimingValidator );
  }

  // ------------------------------------------------------------------------------------
  // IVtReportTemplateService
  //

  @Override
  public ITsValidationSupport<IVtReportTemplateServiceValidator> svs() {
    return validationSupport;
  }

  @Override
  public ITsEventer<IVtReportTemplateServiceListener> eventer() {
    return eventer;
  }

  @Override
  public IList<IVtReportTemplate> listReportTemplates() {
    IList<IVtReportTemplate> ll = objServ().listObjs( IVtReportTemplate.CLASS_ID, false );
    return new StridablesList<>( ll );
  }

  @Override
  public IVtReportTemplate findReportTemplate( String aTempId ) {
    TsNullArgumentRtException.checkNull( aTempId );
    return coreApi().objService().find( new Skid( IVtReportTemplate.CLASS_ID, aTempId ) );
  }

  @Override
  public IVtReportTemplate createReportTemplate( IDtoFullObject aDtoReportTemplate ) {
    TsValidationFailedRtException.checkError( validationSupport.canCreateReportTemplate( aDtoReportTemplate ) );
    pauseCoreValidation();
    try {
      IVtReportTemplate retVal = DtoFullObject.defineFullObject( coreApi(), aDtoReportTemplate );
      // установим ему автора
      if( connection != null ) {
        ISkUser currUser = ConnectionUtiles.getConnectedUser( coreApi() );
        coreApi().linkService().defineLink( retVal.skid(), LNKID_TEMPLATE_AUTHOR, null,
            new SkidList( currUser.skid() ) );
      }
      return retVal;
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public IVtReportTemplate editReportTemplate( IDtoFullObject aDtoReportTemplate ) {
    TsNullArgumentRtException.checkNull( aDtoReportTemplate );
    TsIllegalArgumentRtException.checkFalse( aDtoReportTemplate.classId().equals( IVtReportTemplate.CLASS_ID ) );
    IVtReportTemplate oldReportTemplate = objServ().find( aDtoReportTemplate.skid() );
    TsItemNotFoundRtException.checkNull( oldReportTemplate );
    TsValidationFailedRtException
        .checkError( validationSupport.canEditReportTemplate( aDtoReportTemplate, oldReportTemplate ) );
    pauseCoreValidation();
    try {
      return DtoFullObject.defineFullObject( coreApi(), aDtoReportTemplate );
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public void removeReportTemplate( String aReportTemplateId ) {
    TsValidationFailedRtException.checkError( svs().validator().canRemoveReportTemplate( aReportTemplateId ) );
    pauseCoreValidation();
    try {
      coreApi().objService().removeObject( new Skid( IVtReportTemplate.CLASS_ID, aReportTemplateId ) );
    }
    finally {
      resumeCoreValidation();
    }

  }

}
