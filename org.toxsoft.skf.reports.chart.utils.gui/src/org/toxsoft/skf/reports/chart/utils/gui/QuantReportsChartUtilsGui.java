package org.toxsoft.skf.reports.chart.utils.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;

/**
 * Quant chart utils gui
 *
 * @author max
 */
public class QuantReportsChartUtilsGui
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantReportsChartUtilsGui() {
    super( QuantReportsChartUtilsGui.class.getSimpleName() );

  }

  @Override
  protected void doInitApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IReportsChartUtilsGuiConstants.init( aWinContext );
  }
}
