package org.toxsoft.skf.reports.gui.panels.valed;

import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.panels.valed.IReportsGuiResources.*;
import static org.toxsoft.skf.reports.gui.panels.valed.ValedGwidEditor.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.helpers.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skf.rri.lib.ugwi.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.gui.valed.ugwi.*;

/**
 * Allows to select {@link Gwid} by accessing {@link ISkObjectService}.
 *
 * @author max
 */
public class SpecValedGwidEditor
    extends AbstractValedTextAndButton<Gwid> {

  /**
   * The factory name.
   */
  public static final String FACTORY_NAME = VALED_EDNAME_PREFIX + ".SpecGwidEditor"; //$NON-NLS-1$

  /**
   * Id for gwid kind option
   */
  public static final String OPID_GWID_KIND_BY_JR = "gwidKindByJr"; //$NON-NLS-1$

  /**
   * The gwid kind will be returned.
   */
  public static final IDataDef OPDEF_GWID_KIND_BY_JR = DataDef.create( OPID_GWID_KIND_BY_JR, VALOBJ, //
      TSID_NAME, STR_GWID_KIND, //
      TSID_DESCRIPTION, STR_GWID_KIND_D, //
      TSID_KEEPER_ID, EJrParamSourceType.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EJrParamSourceType.RTDATA ) //
  );

  /**
   * The factory class.
   *
   * @author hazard157
   */
  static class Factory
      extends AbstractValedControlFactory {

    protected Factory() {
      super( FACTORY_NAME );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected IValedControl<Gwid> doCreateEditor( ITsGuiContext aContext ) {
      AbstractValedControl<Gwid, ?> e = new SpecValedGwidEditor( aContext );
      return e;
    }

  }

  /**
   * The factory singleton.
   */
  public static final AbstractValedControlFactory FACTORY = new Factory();

  /**
   * Constructor for subclasses.
   *
   * @param aContext {@link ITsGuiContext} - the valed context
   * @throws TsNullArgumentRtException аргумент = null
   */
  public SpecValedGwidEditor( ITsGuiContext aContext ) {
    super( aContext );
    setParamIfNull( OPDEF_IS_WIDTH_FIXED, AV_FALSE );
    setParamIfNull( OPDEF_IS_HEIGHT_FIXED, AV_TRUE );
    setParamIfNull( OPDEF_VERTICAL_SPAN, AV_1 );
    setParamIfNull( OPDEF_IS_EMPTY_GWID_VALID, OPDEF_IS_EMPTY_GWID_VALID.defaultValue() );
  }

  @Override
  protected boolean doProcessButtonPress() {
    // TODO - conver ugwi to gwid and back
    EJrParamSourceType gwidKind = params().getValobj( OPDEF_GWID_KIND_BY_JR );

    // create and dispaly Gwid selector
    Gwid gwid = selectGwid( gwidKind );

    if( gwid != null ) {
      doSetUnvalidatedValue( gwid );
      return true;
    }
    return false;
  }

  @Override
  public ValidationResult canGetValue() {
    if( params().getBool( OPDEF_IS_EMPTY_GWID_VALID ) && getTextControl().getText().trim().length() == 0 ) {
      return ValidationResult.SUCCESS;
    }
    try {
      Gwid.of( getTextControl().getText() );
      return ValidationResult.SUCCESS;
    }
    catch( @SuppressWarnings( "unused" ) Exception ex ) {
      return ValidationResult.error( MSG_ERR_INV_GWID_FORMAT );
    }
  }

  @Override
  protected void doUpdateTextControl() {
    // nop
  }

  @Override
  protected Gwid doGetUnvalidatedValue() {
    if( params().getBool( OPDEF_IS_EMPTY_GWID_VALID ) && getTextControl().getText().trim().length() == 0 ) {
      return null;
    }
    return Gwid.of( getTextControl().getText() );
  }

  @Override
  protected void doDoSetUnvalidatedValue( Gwid aValue ) {
    String txt = TsLibUtils.EMPTY_STRING;
    if( aValue != null ) {
      txt = aValue.toString();
    }
    getTextControl().setText( txt );
  }

  private Gwid selectGwid( EJrParamSourceType aGwidKind ) {
    Gwid retVal = null;

    switch( aGwidKind ) {

      case RTDATA:
        retVal = PanelGwidSelector.selectGwid( canGetValue().isOk() ? getValue() : null, tsContext(),
            ESkClassPropKind.RTDATA, null );
        break;
      case ATTRIBURES:
        retVal = PanelGwidSelector.selectGwid( canGetValue().isOk() ? getValue() : null, tsContext(),
            ESkClassPropKind.ATTR, null );
        break;
      case RRI_ATTRIBUTES:
        Gwid init = canGetValue().isOk() ? getValue() : null;
        Ugwi currItem = null;
        // try {
        // currItem = init == null ? null : Ugwi.of( UgwiKindRriAttr.KIND_ID, init.canonicalString() );
        // }
        // catch( Exception e ) {
        // // nop
        // e.printStackTrace();
        // }
        Ugwi selUgwi = PanelUgwiSelector.selectUgwiSingleKind( tsContext(), currItem, UgwiKindRriAttr.KIND_ID );
        if( selUgwi == null ) {
          return null;
        }
        // dima 03/01/26 patch for build error
        // retVal = Gwid.createAttr( UgwiKindRriAttr.getClassId( selUgwi ), UgwiKindRriAttr.getObjStrid( selUgwi ),
        // UgwiKindRriAttr.getAttrId( selUgwi ) );
        retVal = Gwid.createAttr( getClassId( selUgwi ), getObjStrid( selUgwi ), getAttrId( selUgwi ) );
        break;
      default:
        break;
    }
    return retVal;
  }

  String getClassId( Ugwi aUgwi ) {
    IdChain chain = IdChain.of( aUgwi.essence() );
    return chain.get( 1 );
  }

  String getObjStrid( Ugwi aUgwi ) {
    IdChain chain = IdChain.of( aUgwi.essence() );
    return chain.get( 2 );
  }

  String getAttrId( Ugwi aUgwi ) {
    IdChain chain = IdChain.of( aUgwi.essence() );
    return chain.get( 3 );
  }

}
