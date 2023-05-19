package org.toxsoft.skf.reports.templates.service.impl;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
interface IVtResources {

  /**
   * {@link VtReportTemplateService}
   */
  String FMT_ERR_NOT_REPORT_TEMPLATE_DPU = "Внутренняя ошибка: DPU пользователя имеет класс '%s' вместо '%s'";
  String MSG_ERR_NO_PARAMS               = "Шаблон отчета не может быть с пустым список параметров";
}
