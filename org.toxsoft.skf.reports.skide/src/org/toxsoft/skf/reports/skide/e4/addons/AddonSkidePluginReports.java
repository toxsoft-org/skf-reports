package org.toxsoft.skf.reports.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.reports.skide.*;
import org.toxsoft.skf.reports.skide.main.*;
import org.toxsoft.skide.core.api.*;

/**
 * Plugin addon.
 *
 * @author Max
 */
public class AddonSkidePluginReports
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkidePluginReports() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginReports.INSTANCE );
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.reports.chart.utils.gui.QuantReportsChartUtilsGui() );
    aQuantRegistrator.registerQuant( new org.toxsoft.skf.reports.gui.QuantVtReportTemplate() );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginReportsConstants.init( aWinContext );
  }

}
