package org.toxsoft.skf.reports.templates.service;

import org.toxsoft.skf.reports.templates.service.impl.*;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( value = { "nls", "javadoc" } )
public interface IVtResources {

  /**
   * {@link VtReportTemplateService}
   */
  String FMT_ERR_NOT_REPORT_TEMPLATE_DPU = Messages.getString( "FMT_ERR_NOT_REPORT_TEMPLATE_DPU" );
  String MSG_ERR_NO_PARAMS               = Messages.getString( "MSG_ERR_NO_PARAMS" );

  /**
   * {@link EAggregationFunc}
   */
  String STR_N_AVERAGE = Messages.getString( "STR_N_AVERAGE" );
  String STR_D_AVERAGE = Messages.getString( "STR_D_AVERAGE" );
  String STR_N_MIN     = Messages.getString( "STR_N_MIN" );
  String STR_D_MIN     = Messages.getString( "STR_D_MIN" );
  String STR_N_MAX     = Messages.getString( "STR_N_MAX" );
  String STR_D_MAX     = Messages.getString( "STR_D_MAX" );
  String STR_N_SUM     = Messages.getString( "STR_N_SUM" );
  String STR_D_SUM     = Messages.getString( "STR_D_SUM" );
  String STR_N_COUNT   = Messages.getString( "STR_N_COUNT" );
  String STR_D_COUNT   = Messages.getString( "STR_D_COUNT" );

  /**
   * {@link IVtTemplateEditorServiceHardConstants}
   */
  String STR_N_AGGR_FUNC           = Messages.getString( "STR_N_AGGR_FUNC" );
  String STR_D_AGGR_FUNC           = Messages.getString( "STR_D_AGGR_FUNC" );
  String STR_N_DISPLAY_FORMAT      = Messages.getString( "STR_N_DISPLAY_FORMAT" );
  String STR_D_DISPLAY_FORMAT      = Messages.getString( "STR_D_DISPLAY_FORMAT" );
  String STR_N_TITLE               = Messages.getString( "STR_N_TITLE" );
  String STR_D_TITLE               = Messages.getString( "STR_D_TITLE" );
  String STR_N_TEMPLATE_PARAMS     = Messages.getString( "STR_N_TEMPLATE_PARAMS" );
  String STR_D_TEMPLATE_PARAMS     = Messages.getString( "STR_D_TEMPLATE_PARAMS" );
  String STR_N_HAS_SUMMARY         = Messages.getString( "STR_N_HAS_SUMMARY" );
  String STR_D_HAS_SUMMARY         = Messages.getString( "STR_D_HAS_SUMMARY" );
  String STR_N_AGGR_STEP           = Messages.getString( "STR_N_AGGR_STEP" );
  String STR_D_AGGR_STEP           = Messages.getString( "STR_D_AGGR_STEP" );
  String STR_N_CLB_TEMPLATE_PARAMS = Messages.getString( "STR_N_CLB_TEMPLATE_PARAMS" );
  String STR_D_CLB_TEMPLATE_PARAMS = Messages.getString( "STR_D_CLB_TEMPLATE_PARAMS" );
  String STR_N_TEMPLATE_AUTHOR     = Messages.getString( "STR_N_TEMPLATE_AUTHOR" );
  String STR_D_TEMPLATE_AUTHOR     = Messages.getString( "STR_D_TEMPLATE_AUTHOR" );
  String STR_N_MAX_EXECUTION_TIME  = Messages.getString( "STR_N_MAX_EXECUTION_TIME" );
  String STR_D_MAX_EXECUTION_TIME  = Messages.getString( "STR_D_MAX_EXECUTION_TIME" );

}
