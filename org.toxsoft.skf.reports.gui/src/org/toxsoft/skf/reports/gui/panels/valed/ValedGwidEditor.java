package org.toxsoft.skf.reports.gui.panels.valed;

import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.reports.gui.panels.valed.IReportsGuiResources.*;

import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.valed.api.IValedControl;
import org.toxsoft.core.tsgui.valed.controls.helpers.AbstractValedTextAndButton;
import org.toxsoft.core.tsgui.valed.impl.AbstractValedControl;
import org.toxsoft.core.tsgui.valed.impl.AbstractValedControlFactory;
import org.toxsoft.core.tslib.bricks.validator.ValidationResult;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.utils.TsLibUtils;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.uskat.core.api.objserv.ISkObjectService;

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

  }

  @Override
  protected boolean doProcessButtonPress() {
    // create and dispaly Gwid selector
    Gwid gwid = PanelGwidSelector.selectGwid( canGetValue().isOk() ? getValue() : null, tsContext() );

    if( gwid != null ) {
      doSetUnvalidatedValue( gwid );
      return true;
    }
    return false;
  }

  @Override
  public ValidationResult canGetValue() {
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
