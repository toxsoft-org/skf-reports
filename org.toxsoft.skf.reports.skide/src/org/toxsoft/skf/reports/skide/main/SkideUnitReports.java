package org.toxsoft.skf.reports.skide.main;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsConstants.*;
//import static org.toxsoft.skf.reports.gui.ISkUsersGuiConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.core.api.ucateg.ISkideUnitCategoryConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.reports.skide.utils.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.skide.core.api.tasks.*;

/**
 * SkiDE unit: USkat reports management.
 *
 * @author hazard157
 */
public class SkideUnitReports
    extends AbstractSkideUnit {

  /**
   * The plugin ID.
   */
  public static final String UNIT_ID = SKIDE_FULL_ID + ".unit.reports"; //$NON-NLS-1$

  SkideUnitReports( ITsGuiContext aContext, AbstractSkidePlugin aCreator ) {
    super( UNIT_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_REPORTS_MANAGEMENT, //
        TSID_DESCRIPTION, STR_SKIDE_REPORTS_MANAGEMENT_D, //
        OPDEF_SKIDE_UNIT_CATEGORY, UCATEGID_ADMINISTRATION, //
        TSID_ICON_ID, ICONID_TEMPLATE_EDITOR //
    ), aContext, aCreator );
    unitActions().add( ACDEF_ABOUT );
  }

  @Override
  protected AbstractSkideUnitPanel doCreateUnitPanel( ITsGuiContext aContext ) {
    return new SkideUnitPanelReports( aContext, this );
  }

  @Override
  protected void doFillTasks( IStringMapEdit<AbstractSkideUnitTask> aTaskRunnersMap ) {
    AbstractSkideUnitTask task = new TaskTemplatesUpload( this );
    aTaskRunnersMap.put( task.taskInfo().id(), task );
  }

}
