package org.toxsoft.skf.reports.mws.e4.addons;

import static org.toxsoft.skf.reports.gui.IReportsGuiConstants.*;
import static org.toxsoft.skf.reports.mws.IReportsConstants.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.reports.mws.*;
import org.toxsoft.uskat.core.gui.utils.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Plugin adoon.
 *
 * @author hazard157
 */
public class AddonVtWsReports
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonVtWsReports() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    IReportsConstants.init( aWinContext );
    // implement access rights
    GuiE4ElementsToAbilitiesBinder binder = new GuiE4ElementsToAbilitiesBinder( new TsGuiContext( aWinContext ) );
    binder.bindPerspective( ABILITYID_TEMPLATE_EDITOR_PERSP, E4_VISUAL_ELEM_ID_PERSP_REPORTS );
    binder.bindMenuElement( ABILITYID_TEMPLATE_EDITOR_PERSP, E4_VISUAL_ELEM_ID_MENU_ITEM_REPORTS );
    binder.bindToolItem( ABILITYID_TEMPLATE_EDITOR_PERSP, E4_VISUAL_ELEM_ID_TOOL_ITEM_REPORTS );
    SkCoreUtils.registerCoreApiHandler( binder );
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.reports.chart.utils.gui.QuantReportsChartUtilsGui() );
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.reports.gui.QuantVtReportTemplate() );
  }
}
