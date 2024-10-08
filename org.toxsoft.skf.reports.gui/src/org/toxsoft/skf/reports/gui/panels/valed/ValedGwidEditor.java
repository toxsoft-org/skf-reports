package org.toxsoft.skf.reports.gui.panels.valed;

import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.panels.valed.IReportsGuiResources.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.helpers.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;

/**
 * Allows to select {@link Gwid} by accessing {@link ISkObjectService}.
 *
 * @author hazard157
 * @author dima
 */
public class ValedGwidEditor
    extends AbstractValedTextAndButton<Gwid> {

  /**
   * The factory name.
   */
  public static final String FACTORY_NAME = VALED_EDNAME_PREFIX + ".GwidEditor"; //$NON-NLS-1$

  /**
   * Id for gwid kind option
   */
  public static final String OPID_GWID_KIND = "gwidKind"; //$NON-NLS-1$

  /**
   * The gwid kind will be returned.
   */
  public static final IDataDef OPDEF_GWID_KIND = DataDef.create( OPID_GWID_KIND, VALOBJ, //
      TSID_NAME, STR_GWID_KIND, //
      TSID_DESCRIPTION, STR_GWID_KIND_D, //
      TSID_KEEPER_ID, EGwidKind.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EGwidKind.GW_RTDATA ) //
  );

  /**
   * ID of option {@link #OPDEF_IS_EMPTY_GWID_VALID}.
   */
  public static final String OPID_IS_EMPTY_GWID_VALID = "IsEmptyGwidValid"; //$NON-NLS-1$

  public static final IDataDef OPDEF_IS_EMPTY_GWID_VALID = DataDef.create( OPID_IS_EMPTY_GWID_VALID, BOOLEAN, //
      TSID_NAME, "Is Empty GWID valid", //
      TSID_DESCRIPTION, "Is Empty value of GWID valid", //
      TSID_DEFAULT_VALUE, AV_FALSE //
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
      AbstractValedControl<Gwid, ?> e = new ValedGwidEditor( aContext );
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
  public ValedGwidEditor( ITsGuiContext aContext ) {
    super( aContext );
    setParamIfNull( OPDEF_IS_WIDTH_FIXED, AV_FALSE );
    setParamIfNull( OPDEF_IS_HEIGHT_FIXED, AV_TRUE );
    setParamIfNull( OPDEF_VERTICAL_SPAN, AV_1 );
    setParamIfNull( OPDEF_IS_EMPTY_GWID_VALID, OPDEF_IS_EMPTY_GWID_VALID.defaultValue() );
  }

  @Override
  protected boolean doProcessButtonPress() {
    // TODO - conver ugwi to gwid and back
    EGwidKind gwidKind = params().getValobj( OPDEF_GWID_KIND );
    ESkClassPropKind propKind = ESkClassPropKind.getById( gwidKind.id() );
    // create and dispaly Gwid selector
    Gwid gwid = PanelGwidSelector.selectGwid( canGetValue().isOk() ? getValue() : null, tsContext(), propKind, null );

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

}
