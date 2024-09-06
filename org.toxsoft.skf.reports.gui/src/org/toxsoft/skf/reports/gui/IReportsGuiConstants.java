package org.toxsoft.skf.reports.gui;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.ISkResources.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tslib.av.metainfo.*;

/**
 * Plugin constants.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IReportsGuiConstants {

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_"; //$NON-NLS-1$
  // String ICONID_APP_ICON = "app-icon"; //$NON-NLS-1$

  String ICONID_USER            = "user-red";           //$NON-NLS-1$
  String ICONID_RUN             = "run";                //$NON-NLS-1$
  String ICONID_TEMPLATE        = "gdp-shablons";       //$NON-NLS-1$
  String ICONID_REPORT_TEMPLATE = "uipart-events";      //$NON-NLS-1$
  String ICONID_GRAPH_TEMPLATE  = "uipart-ws-profiles"; //$NON-NLS-1$

  String SHOW_APPLY_BUTTON_ID = "show.apply.button"; //$NON-NLS-1$

  /**
   * Параметр, определяющий показывать кнопку "сформировать отчёт".
   */
  IDataDef SHOW_APPLY_BUTTON = create( SHOW_APPLY_BUTTON_ID, BOOLEAN, //
      TSID_NAME, STR_SHOW_APPLY_BUTTON, //
      TSID_DESCRIPTION, STR_SHOW_APPLY_BUTTON_D, //
      TSID_DEFAULT_VALUE, avBool( true ), //
      TSID_IS_MANDATORY, Boolean.FALSE //
  );

  /**
   * Параметр, указывающий на шаблон отчёта
   */
  String JR_TEMPLATE = "jr.template"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IReportsGuiConstants.class, PREFIX_OF_ICON_FIELD_NAME );
    //
  }

}
