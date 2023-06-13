package org.toxsoft.skf.reports.gui.km5;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
interface IReportsGuiResources {

  /**
   * {@link ReportParamM5Model}
   */
  String STR_N_PARAM_GWID           = "параметр";
  String STR_D_PARAM_GWID           = "идентификатор параметра";
  String STR_N_PARAM_AGGR_FUNC      = "f(agr)";
  String STR_D_PARAM_AGGR_FUNC      = "функция агрегации";
  String STR_N_PARAM_DISPLAY_FORMAT = "формат";
  String STR_D_PARAM_DISPLAY_FORMAT = "формат отображения";
  String STR_N_PARAM_TITLE          = "название";
  String STR_D_PARAM_TITLE          = "название параметра";
  String STR_N_PARAM_DESCRIPTION    = "описание";
  String STR_D_PARAM_DESCRIPTION    = "описание параметра";
  String STR_N_SELECT_MULTY_PARAMS  = "мульти выбор";
  String STR_D_SELECT_MULTY_PARAMS  = "выбрать несколько параметров сразу";
  String STR_N_COPY_PARAM           = "копировать выбранный";
  String STR_D_COPY_PARAM           = "сделать копию текущего выбранного параметра списка";

  /**
   * {@link GraphParamM5Model}
   */
  String STR_N_PARAM_COLOR              = "цвет";
  String STR_D_PARAM_COLOR              = "цвет линии параметра";
  String STR_N_PARAM_LINE_WIDTH         = "толщина линии";
  String STR_D_PARAM_LINE_WIDTH         = "толщина линии параметра";
  String STR_N_GRAPH_TEMPLATE           = "шаблоны";
  String STR_D_GRAPH_TEMPLATE           = "шаблоны графиков";
  String STR_N_PARAM_UNIT_ID            = "unitId";
  String STR_D_PARAM_UNIT_ID            = "идентификатор единицы измерения параметра";
  String STR_N_PARAM_UNIT_NAME          = "unit";
  String STR_D_PARAM_UNIT_NAME          = "Единица измерения параметра";
  String STR_N_IS_LADDER                = "ступеньками";
  String STR_D_IS_LADDER                = "Рисовать график в ступеньками";
  String STR_N_SET_POINTS               = "линии уставок";
  String STR_D_SET_POINTS               = "Значения для линий уставок";
  String STR_N_PARAM_MAX_EXECUTION_TIME = "время выполнения";
  String STR_D_PARAM_MAX_EXECUTION_TIME = "максимальное время выполнения запроса (мсек)";

  /**
   * {@link ReportTemplateM5Model}
   */
  String STR_N_TEMPLATE_PARAMS  = "параметры";
  String STR_D_TEMPLATE_PARAMS  = "список параметров шаблона";
  String STR_N_REPORT_TEMPLATE  = "шаблоны";
  String STR_D_REPORT_TEMPLATE  = "шаблоны отчетов";
  String STR_N_FDEF_HAS_SUMMARY = "'Итого'";
  String STR_D_FDEF_HAS_SUMMARY = "отображать область 'Итого'";
  String STR_N_FDEF_NAME        = "название";
  String STR_D_FDEF_NAME        = "название шаблона";
  // String STR_FDEF_DESCR = "описание";
  // String STR_FDEF_DESCR_D = "описание шаблона";
  String STR_PARAM_AGGR_STEP   = Messages.getString( "STR_PARAM_AGGR_STEP" );   //$NON-NLS-1$
  String STR_PARAM_AGGR_STEP_D = Messages.getString( "STR_PARAM_AGGR_STEP_D" ); //$NON-NLS-1$

}
