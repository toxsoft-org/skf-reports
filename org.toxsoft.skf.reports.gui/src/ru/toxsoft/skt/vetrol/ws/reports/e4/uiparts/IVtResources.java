package ru.toxsoft.skt.vetrol.ws.reports.e4.uiparts;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
interface IVtResources {

  /**
   * {@link VtGraphTemplateEditorPanel}
   */
  String STR_N_BY_USERS           = "по пользователям";
  String STR_D_BY_USERS           = "Дерево пользователи-шаблоны отчетов";
  String STR_N_GENERATE_CHART     = "Сформировать график";
  String STR_D_GENERATE_CHART     = "Загрузка данных из БД и формирование графика";
  String STR_EXEC_QUERY_REPORT    = "Запрос данных для отчета";
  String STR_EXEC_QUERY_FOR_GRAPH = "Запрос данных для графика";
  String ERR_QUERY_FAILED         = "Ошибка выполнения запроса данных: %s";

  /**
   * {@link VtReportTemplateEditorPanel}
   */
  String STR_N_GENERATE_REPORT = "Сформировать отчёт";
  String STR_D_GENERATE_REPORT = "Загрузка данных из БД и формирование отчёта";
  String STR_N_COPY_TEMPLATE   = "Копия шаблона";
  String STR_D_COPY_TEMPLATE   = "Сделать копию шаблона";
  String AUTHOR_STR            = "Автор: ";
  String DATE_STR              = "Дата печати: ";

}
