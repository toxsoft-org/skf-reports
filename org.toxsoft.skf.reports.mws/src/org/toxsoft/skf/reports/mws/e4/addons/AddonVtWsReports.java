package org.toxsoft.skf.reports.mws.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.reports.chart.utils.gui.QuantReportsChartUtilsGui;
import org.toxsoft.skf.reports.mws.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.*;

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
    // описываем здесь свои возможности
    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    // ISkOneWsService ows = (ISkOneWsService)conn.coreApi().getService( ISkOneWsService.SERVICE_ID );
    // for( IStridableParameterized abilityKind : ReportsAbilities.listAbilityKinds() ) {
    // ows.defineAbilityKind( abilityKind );
    // }
    // for( IOneWsAbility ability : ReportsAbilities.listAbilities() ) {
    // ows.defineAbility( ability );
    // }

  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantReportsChartUtilsGui());
    aQuantRegistrator.registerQuant( new QuantVtReportTemplate() );
  }
}
