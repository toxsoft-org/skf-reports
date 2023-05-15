package ru.toxsoft.skt.vetrol.ws.core;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.skf.reports.gui.*;

/**
 * Plugin constants.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface IVtWsCoreConstants {

  String CI_ID = "ci"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // E4

  String PERSPID_CI_MAIN = "ru.toxsoft.ci.persp.ci_main"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";  //$NON-NLS-1$
  String ICONID_APP_ICON           = "app-icon"; //$NON-NLS-1$

  String ICONID_USER            = "user-red";           //$NON-NLS-1$
  String ICONID_RUN             = "run";                //$NON-NLS-1$
  String ICONID_TEMPLATE        = "gdp-shablons";       //$NON-NLS-1$
  String ICONID_REPORT_TEMPLATE = "uipart-events";      //$NON-NLS-1$
  String ICONID_GRAPH_TEMPLATE  = "uipart-ws-profiles"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IVtWsCoreConstants.class, PREFIX_OF_ICON_FIELD_NAME );
    //
  }

}
