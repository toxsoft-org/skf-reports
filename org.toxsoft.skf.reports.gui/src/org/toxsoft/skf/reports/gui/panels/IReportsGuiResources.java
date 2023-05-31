package org.toxsoft.skf.reports.gui.panels;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
interface IReportsGuiResources {

  /**
   * {@link PanelGwidSelector}
   */
  String DLG_T_GWID_SEL         = "Выбор данного (Gwid)";
  String STR_MSG_GWID_SELECTION = "Выберите класс, объект и его данное";
  String STR_MSG_SELECT_OBJ     = "не указан объект";
  String STR_MSG_SELECT_DATA    = "не указано данное";

  /**
   * {@link PanelGwidListSelector}
   */
  String DLG_T_GWID_LIST_SEL         = "Выбор списка данных (IGwidList)";
  String STR_MSG_GWID_LIST_SELECTION = "Выберите класс, пометьте объекты и данные";

  /**
   * {@link RtDataInfoViewerPanel}
   */
  String STR_N_HIDE_ASYNC = "Спрятать асинхронные";
  String STR_D_HIDE_ASYNC = "Не показывать асинхронные данные";

  /**
   * {@link GraphTemplateEditorPanel}
   */
  String STR_N_BY_USERS           = "по пользователям";
  String STR_D_BY_USERS           = "Дерево пользователи-шаблоны отчетов";
  String STR_N_GENERATE_CHART     = "Сформировать график";
  String STR_D_GENERATE_CHART     = "Загрузка данных из БД и формирование графика";
  String STR_EXEC_QUERY_REPORT    = "Запрос данных для отчета";
  String STR_EXEC_QUERY_FOR_GRAPH = "Запрос данных для графика";
  String ERR_QUERY_FAILED         = "Ошибка выполнения запроса данных: %s";

  /**
   * {@link ReportTemplateEditorPanel}
   */
  String STR_N_GENERATE_REPORT = "Сформировать отчёт";
  String STR_D_GENERATE_REPORT = "Загрузка данных из БД и формирование отчёта";
  String STR_N_COPY_TEMPLATE   = "Копия шаблона";
  String STR_D_COPY_TEMPLATE   = "Сделать копию шаблона";
  String AUTHOR_STR            = "Автор: ";
  String DATE_STR              = "Дата печати: ";

}
