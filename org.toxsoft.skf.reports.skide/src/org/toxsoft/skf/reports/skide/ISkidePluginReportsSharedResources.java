package org.toxsoft.skf.reports.skide;

import org.toxsoft.skf.reports.skide.main.*;
import org.toxsoft.skf.reports.skide.utils.*;

/**
 * Localizable resources.
 *
 * @author Max
 */
@SuppressWarnings( value = { "javadoc" } )
public interface ISkidePluginReportsSharedResources {

  /**
   * {@link SkidePluginReports}
   */
  String STR_SKIDE_REPORTS_MANAGEMENT   = Messages.getString( "STR_SKIDE_REPORTS_MANAGEMENT" );   //$NON-NLS-1$
  String STR_SKIDE_REPORTS_MANAGEMENT_D = Messages.getString( "STR_SKIDE_REPORTS_MANAGEMENT_D" ); //$NON-NLS-1$

  /**
   * {@link SkideUnitPanelReports}
   */
  String STR_TAB_REPORTS        = Messages.getString( "STR_TAB_REPORTS" );        //$NON-NLS-1$
  String STR_TAB_REPORTS_D      = Messages.getString( "STR_TAB_REPORTS_D" );      //$NON-NLS-1$
  String STR_TAB_GRAPHS         = Messages.getString( "STR_TAB_GRAPHS" );         //$NON-NLS-1$
  String STR_TAB_GRAPHS_D       = Messages.getString( "STR_TAB_GRAPHS_D" );       //$NON-NLS-1$
  String STR_BUTTON_EXPORT_D    = Messages.getString( "STR_BUTTON_EXPORT_D" );    //$NON-NLS-1$
  String STR_TAB_SPEC_REPORTS_D = Messages.getString( "STR_TAB_SPEC_REPORTS_D" ); //$NON-NLS-1$
  String STR_TAB_SPEC_REPORTS   = Messages.getString( "STR_TAB_SPEC_REPORTS" );   //$NON-NLS-1$

  /**
   * {@link ReportsTemplatesMigrationUtiles}
   */
  String STR_MODEL_GRAPH              = Messages.getString( "STR_MODEL_GRAPH" );              //$NON-NLS-1$
  String STR_MODEL_GRAPH_D            = Messages.getString( "STR_MODEL_GRAPH_D" );            //$NON-NLS-1$
  String STR_MODEL_REPORT             = Messages.getString( "STR_MODEL_REPORT" );             //$NON-NLS-1$
  String STR_MODEL_REPORT_D           = Messages.getString( "STR_MODEL_REPORT_D" );           //$NON-NLS-1$
  String STR_EXPORT_TEMPLATE_DIALOG   = Messages.getString( "STR_EXPORT_TEMPLATE_DIALOG" );   //$NON-NLS-1$
  String STR_EXPORT_TEMPLATE_DIALOG_D = Messages.getString( "STR_EXPORT_TEMPLATE_DIALOG_D" ); //$NON-NLS-1$
  String STR_EXPORT_COMPLETE_DIALOG   = Messages.getString( "STR_EXPORT_COMPLETE_DIALOG" );   //$NON-NLS-1$

  /**
   * {@link TaskTemplatesUpload}
   */
  String STR_TEMPLATES_DOWNLOAD   = "выгрузка шаблонов";
  String STR_TEMPLATES_DOWNLOAD_D = "Загрузка/выгрузка шаблонов отчетов и графиков";
  String MSG_TEMPLATES_DOWNLOAD   = "Идет выгрузка шаблонов с удаленного сервера";
  String MSG_TEMPLATES_UPLOAD     = "Идет загрузка шаблонов на удаленный сервер";
  String FMT_TEMPLATES_DOWNLOADED = "Выгружено с удаленного сервера %d шаблонов отчетов и %d шаблонов графиков";
  String FMT_TEMPLATES_UPLOADED   = "Загружено на удаленный сервер %d шаблонов отчетов и %d шаблонов графиков";

}
