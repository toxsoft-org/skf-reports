package org.toxsoft.skf.reports.skide;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Application common constants.
 *
 * @author Max
 */
@SuppressWarnings( "javadoc" )
public interface ISkidePluginReportsConstants {
  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";             //$NON-NLS-1$
  String ICONID_SKIDE_PLUGIN       = "pattern_otchet_graf"; //$NON-NLS-1$
  String ICONID_GRAPH_TEMPL        = "pattern_otchet_graf"; //$NON-NLS-1$
  String ICONID_REPORT_TEMPL       = "pattern_otchet_txt";  //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, ISkidePluginReportsConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }
}
