package org.toxsoft.skf.reports.templates.service;

/**
 * Interface to specify template of special (specific) doc report (outer design in the from of jrxml file).
 *
 * @author max
 */
public interface IVtSpecReportTemplate
    extends IVtBaseTemplate<IVtSpecReportParam> {

  /**
   * The {@link IVtSpecReportTemplate} class identifier.
   */
  String CLASS_ID = IVtTemplateEditorServiceHardConstants.CLSID_SPEC_REPORT_TEMPLATE;

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

  /**
   * Return content of jrxml design file
   *
   * @return String - content of jrxml design file
   */
  String design();
}
