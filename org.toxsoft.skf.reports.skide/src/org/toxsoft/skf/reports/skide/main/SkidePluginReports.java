package org.toxsoft.skf.reports.skide.main;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skide.core.api.*;

/**
 * SkIDE plugin: reports templates management.
 *
 * @author Max
 */
public class SkidePluginReports
    extends AbstractSkidePlugin {

  /**
   * The plugin ID.
   */
  public static final String SKIDE_PLUGIN_ID = SKIDE_FULL_ID + ".plugin.skide.reports"; //$NON-NLS-1$

  /**
   * The singleton instance.
   */
  public static final AbstractSkidePlugin INSTANCE = new SkidePluginReports();

  SkidePluginReports() {
    super( SKIDE_PLUGIN_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_REPORTS_MANAGEMENT, //
        TSID_DESCRIPTION, STR_SKIDE_REPORTS_MANAGEMENT_D, //
        TSID_ICON_ID, ICONID_SKIDE_PLUGIN //
    ) );
  }

  @Override
  protected void doCreateUnits( ITsGuiContext aContext, IStridablesListEdit<ISkideUnit> aUnitsList ) {
    aUnitsList.add( new SkideUnitReports( aContext, this ) );
  }

}
