package org.toxsoft.skf.reports.templates.service.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.reports.templates.service.IVtResources.*;
import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

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
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.impl.*;
//import org.toxsoft.uskat.s5.utils.S5ConnectionUtils;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * {@link IVtGraphTemplateService} implementation.
 *
 * @author dima
 */
public class VtGraphTemplateService
    extends AbstractSkService
    implements IVtGraphTemplateService {

  /**
   * Service creator singleton.
   */
  public static final ISkServiceCreator<AbstractSkService> CREATOR = VtGraphTemplateService::new;

  /**
   * {@link IVtGraphTemplateService#svs()} implementation.
   *
   * @author dima
   */
  static class ValidationSupport
      extends AbstractTsValidationSupport<IVtGraphTemplateServiceValidator>
      implements IVtGraphTemplateServiceValidator {

    @Override
    public IVtGraphTemplateServiceValidator validator() {
      return this;
    }

    @Override
    public ValidationResult canCreateGraphTemplate( IDtoFullObject aGraphTemplateDto ) {
      TsNullArgumentRtException.checkNull( aGraphTemplateDto );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtGraphTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canCreateGraphTemplate( aGraphTemplateDto ) );
      }
      return vr;
    }

    @Override
    public ValidationResult canEditGraphTemplate( IDtoFullObject aGraphTemplateDto,
        IVtGraphTemplate aOldGraphTemplate ) {
      TsNullArgumentRtException.checkNulls( aGraphTemplateDto, aOldGraphTemplate );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtGraphTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canEditGraphTemplate( aGraphTemplateDto, aOldGraphTemplate ) );
      }
      return vr;
    }

    @Override
    public ValidationResult canRemoveGraphTemplate( String aGraphTemplateId ) {
      TsNullArgumentRtException.checkNull( aGraphTemplateId );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( IVtGraphTemplateServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canRemoveGraphTemplate( aGraphTemplateId ) );
      }
      return vr;
    }

  }

  /**
   * {@link IVtGraphTemplateService#eventer()} implementation.
   *
   * @author dima
   */
  class Eventer
      extends AbstractTsEventer<IVtGraphTemplateServiceListener> {

    private boolean isPendingGraphTemplates = false;

    @Override
    protected void doClearPendingEvents() {
      isPendingGraphTemplates = false;
    }

    @Override
    protected void doFirePendingEvents() {
      if( isPendingGraphTemplates ) {
        reallyFireGraphTemplate( ECrudOp.LIST, null );
      }
    }

    @Override
    protected boolean doIsPendingEvents() {
      return isPendingGraphTemplates;
    }

    private void reallyFireGraphTemplate( ECrudOp aOp, String aGraphTemplateId ) {
      for( IVtGraphTemplateServiceListener l : listeners() ) {
        try {
          l.onGraphTemplateChanged( coreApi(), aOp, aGraphTemplateId );
        }
        catch( Exception ex ) {
          LoggerUtils.errorLogger().error( ex );
        }
      }
    }

    void fireGraphTemplateChanged( ECrudOp aOp, String aGraphTemplateId ) {
      if( isFiringPaused() ) {
        isPendingGraphTemplates = true;
        return;
      }
      reallyFireGraphTemplate( aOp, aGraphTemplateId );
    }

  }

  /**
   * Builtin service validator.
   */
  private final IVtGraphTemplateServiceValidator builtinValidator = new IVtGraphTemplateServiceValidator() {

    @Override
    public ValidationResult canCreateGraphTemplate( IDtoFullObject aGraphTemplateDto ) {
      // check precondition
      if( !aGraphTemplateDto.skid().classId().equals( IVtGraphTemplate.CLASS_ID ) ) {
        return ValidationResult.error( FMT_ERR_NOT_REPORT_TEMPLATE_DPU, aGraphTemplateDto.classId(),
            IVtGraphTemplate.CLASS_ID );
      }
      // check if parameters list is empty
      // first, get string from Clob
      String templParamsStr =
          aGraphTemplateDto.clobs().getByKey( IVtTemplateEditorServiceHardConstants.CLBID_TEMPLATE_PARAMS );
      // next, convert string to collection
      ICharInputStream charInputStream = new CharInputStreamString( templParamsStr );
      IStrioReader sr = new StrioReader( charInputStream );
      IList<IVtGraphParam> params = VtGraphParam.KEEPER.readColl( sr );
      if( params == null || params.isEmpty() ) {
        return ValidationResult.error( MSG_ERR_NO_PARAMS );
      }
      return ValidationResult.SUCCESS;
    }

    @Override
    public ValidationResult canEditGraphTemplate( IDtoFullObject aGraphTemplateDto,
        IVtGraphTemplate aOldGraphTemplate ) {
      ValidationResult vr = ValidationResult.SUCCESS;
      // check precondition
      if( !aGraphTemplateDto.skid().classId().equals( IVtGraphTemplate.CLASS_ID ) ) {
        return ValidationResult.error( FMT_ERR_NOT_REPORT_TEMPLATE_DPU, aGraphTemplateDto.classId(),
            IVtGraphTemplate.CLASS_ID );
      }
      // check if parameters list is empty
      // first, get string from Clob
      String templParamsStr =
          aGraphTemplateDto.clobs().getByKey( IVtTemplateEditorServiceHardConstants.CLBID_TEMPLATE_PARAMS );
      // next, convert string to collection
      ICharInputStream charInputStream = new CharInputStreamString( templParamsStr );
      IStrioReader sr = new StrioReader( charInputStream );
      IList<IVtGraphParam> params = VtGraphParam.KEEPER.readColl( sr );
      if( params == null || params.isEmpty() ) {
        return ValidationResult.error( MSG_ERR_NO_PARAMS );
      }
      return vr;
    }

    @Override
    public ValidationResult canRemoveGraphTemplate( String aGraphTemplateId ) {
      // TODO check if current user can right to remove this template
      // ???
      return ValidationResult.SUCCESS;
    }

  };

  private final ValidationSupport          validationSupport = new ValidationSupport();
  private final Eventer                    eventer           = new Eventer();
  private final ClassClaimingCoreValidator claimingValidator = new ClassClaimingCoreValidator();

  /**
   * Constructor.
   *
   * @param aCoreApi {@link IDevCoreApi} - owner core API implementation
   */
  VtGraphTemplateService( IDevCoreApi aCoreApi ) {
    super( SERVICE_ID, aCoreApi );
    validationSupport.addValidator( builtinValidator );
  }

  // ------------------------------------------------------------------------------------
  // AbstractSkCoreService
  //

  @Override
  protected void doInit( ITsContextRo aArgs ) {
    // create class for IVtGraphTemplate
    IDtoClassInfo graphTemplateCinf = internalCreateGraphTemplateClassDto();
    sysdescr().defineClass( graphTemplateCinf );
    objServ().registerObjectCreator( IVtGraphTemplate.CLASS_ID, VtGraphTemplate.CREATOR );
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
      case IVtGraphTemplate.CLASS_ID -> true;
      default -> false;
    };
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  /**
   * Creates DTO of {@link IVtGraphTemplate#CLASS_ID} class.
   *
   * @return {@link IDtoClassInfo} - {@link IVtGraphTemplate#CLASS_ID} class info
   */
  public static IDtoClassInfo internalCreateGraphTemplateClassDto() {
    DtoClassInfo cinf = new DtoClassInfo( IVtGraphTemplate.CLASS_ID, GW_ROOT_CLASS_ID, IOptionSet.NULL );
    // TODO need clarification
    OPDEF_SK_IS_SOURCE_CODE_DEFINED_CLASS.setValue( cinf.params(), AV_TRUE );
    OPDEF_SK_IS_SOURCE_USKAT_CORE_CLASS.setValue( cinf.params(), AV_TRUE );
    cinf.attrInfos().add( ATRINF_TITLE );
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
  // IVtGraphTemplateService
  //

  @Override
  public ITsValidationSupport<IVtGraphTemplateServiceValidator> svs() {
    return validationSupport;
  }

  @Override
  public ITsEventer<IVtGraphTemplateServiceListener> eventer() {
    return eventer;
  }

  @Override
  public IList<IVtGraphTemplate> listGraphTemplates() {
    IList<IVtGraphTemplate> ll = objServ().listObjs( IVtGraphTemplate.CLASS_ID, false );
    return new StridablesList<>( ll );
  }

  @Override
  public IVtGraphTemplate findGraphTemplate( String aTempId ) {
    TsNullArgumentRtException.checkNull( aTempId );
    return coreApi().objService().find( new Skid( IVtGraphTemplate.CLASS_ID, aTempId ) );
  }

  @Override
  public IVtGraphTemplate createGraphTemplate( IDtoFullObject aDtoGraphTemplate ) {
    TsValidationFailedRtException.checkError( validationSupport.canCreateGraphTemplate( aDtoGraphTemplate ) );
    pauseCoreValidation();
    try {
      IVtGraphTemplate retVal = DtoFullObject.defineFullObject( coreApi(), aDtoGraphTemplate );
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
  public IVtGraphTemplate editGraphTemplate( IDtoFullObject aDtoGraphTemplate ) {
    TsNullArgumentRtException.checkNull( aDtoGraphTemplate );
    TsIllegalArgumentRtException.checkFalse( aDtoGraphTemplate.classId().equals( IVtGraphTemplate.CLASS_ID ) );
    IVtGraphTemplate oldGraphTemplate = objServ().find( aDtoGraphTemplate.skid() );
    TsItemNotFoundRtException.checkNull( oldGraphTemplate );
    TsValidationFailedRtException
        .checkError( validationSupport.canEditGraphTemplate( aDtoGraphTemplate, oldGraphTemplate ) );
    pauseCoreValidation();
    try {
      return DtoFullObject.defineFullObject( coreApi(), aDtoGraphTemplate );
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public void removeGraphTemplate( String aGraphTemplateId ) {
    TsValidationFailedRtException.checkError( svs().validator().canRemoveGraphTemplate( aGraphTemplateId ) );
    pauseCoreValidation();
    try {
      coreApi().objService().removeObject( new Skid( IVtGraphTemplate.CLASS_ID, aGraphTemplateId ) );
    }
    finally {
      resumeCoreValidation();
    }

  }

}
