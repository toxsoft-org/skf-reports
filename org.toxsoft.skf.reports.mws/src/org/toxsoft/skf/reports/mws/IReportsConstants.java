package org.toxsoft.skf.reports.mws;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Plugin constants.
 *
 * @author max
 */
@SuppressWarnings( "javadoc" )
public interface IReportsConstants {

  // ------------------------------------------------------------------------------------
  // E4

  String PERSPID_REPORTS = "org.toxsoft.skf.reports.mws.perps.reports"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_"; //$NON-NLS-1$
  // String ICONID_USER = "user-red"; //$NON-NLS-1$
  // String ICONID_RUN = "run"; //$NON-NLS-1$
  // String ICONID_TEMPLATE = "gdp-shablons"; //$NON-NLS-1$
  // String ICONID_REPORT_TEMPLATE = "uipart-events"; //$NON-NLS-1$
  // String ICONID_GRAPH_TEMPLATE = "uipart-ws-profiles"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IReportsConstants.class, PREFIX_OF_ICON_FIELD_NAME );
  }

}
