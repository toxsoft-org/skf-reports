package org.toxsoft.skf.reports.templates.service;

/**
 * Interface to specify one parameter for template of doc spec report.
 *
 * @author dima
 */

public interface IVtSpecReportParam
    extends IVtTemplateParam {

  /**
   * Returns id of param from JR design
   *
   * @return String- id of param from JR design
   */
  String jrParamId();

  /**
   * Returns the preset value of param.
   *
   * @return String - the preset value of param.
   */
  String value();

  /**
   * Flag indicating posibility to be overwritten
   *
   * @return true - can be overwritten
   */
  boolean canBeOverwritten();

  /**
   * Returns data source type of param from JR design
   *
   * @return EJrParamSourceType - data source type of param from JR design;
   */
  EJrParamSourceType jrParamSourceType();
}
