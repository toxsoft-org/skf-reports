package org.toxsoft.skf.reports.chart.utils.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Plugin constants.
 *
 * @author max
 */
@SuppressWarnings( "javadoc" )
public interface IReportsChartUtilsGuiConstants {

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_"; //$NON-NLS-1$

  String ICONID_VISIBLE_OFF         = "visible_off";       //$NON-NLS-1$
  String ICONID_VISIBLE_ON          = "visible_on";        //$NON-NLS-1$
  String ICONID_SHIFT_DISPLAY_LEFT  = "sdvig_ekran_left";  //$NON-NLS-1$
  String ICONID_SHIFT_STEP_LEFT     = "sdvig_shag_left";   //$NON-NLS-1$
  String ICONID_SHIFT_STEP_RIGHT    = "sdvig_shag_right";  //$NON-NLS-1$
  String ICONID_SHIFT_DISPLAY_RIGHT = "sdvig_ekran_right"; //$NON-NLS-1$
  String ICONID_GRAPHIC_LIST        = "grafic_list";       //$NON-NLS-1$
  String ICONID_VIZIR               = "vizir";             //$NON-NLS-1$
  String ICONID_LEGENDA_ON          = "legenda_on";        //$NON-NLS-1$
  String ICONID_MANAGE_PULT         = "manage_pult";       //$NON-NLS-1$
  String ICONID_DOCUMENT_PRINT      = "document-print";    //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, IReportsChartUtilsGuiConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }
}
