package org.toxsoft.skf.reports.gui.rtchart.data_aliases;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.reports.gui.*;
import org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * The libtary quant.
 *
 * @author dima
 */
public class QuantDataAliases
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantDataAliases() {
    super( QuantDataAliases.class.getSimpleName() );
    TsValobjUtils.registerKeeper( DataNameAliasesList.KEEPER_ID, DataNameAliasesList.KEEPER );
    KM5Utils.registerContributorCreator( KM5DataAliasesContributor.CREATOR );
  }

  @Override
  protected void doInitApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IReportsGuiConstants.init( aWinContext );
  }

}
