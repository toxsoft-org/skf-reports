package ru.toxsoft.skt.vetrol.ws.core.templates.api;

/**
 * Interface to specify template of doc report.
 *
 * @author dima
 */

public interface IVtReportTemplate
    extends IVtBaseTemplate<IVtReportParam> {

  /**
   * The {@link IVtReportTemplate} class identifier.
   */
  String CLASS_ID = IVtTemplateEditorServiceHardConstants.CLSID_REPORT_TEMPLATE;

  /**
   * Determines if report has summary.
   *
   * @return boolean - has report summary<br>
   *         <code>true</code> report has summary<br>
   *         <code>false</code> report has no summary
   */
  default boolean hasSummary() {
    return attrs().getBool( IVtTemplateEditorServiceHardConstants.ATRID_HAS_SUMMARY );
  }

}
