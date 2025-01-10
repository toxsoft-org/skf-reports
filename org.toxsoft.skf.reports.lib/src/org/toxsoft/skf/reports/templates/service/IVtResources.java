package org.toxsoft.skf.reports.templates.service;

import org.toxsoft.skf.reports.templates.service.impl.*;

/**
 * Localizable resources.
 *
 * @author dima
 */
@SuppressWarnings( value = { "javadoc" } )
public interface IVtResources {

  /**
   * {@link VtReportTemplateService}
   */
  String FMT_ERR_NOT_REPORT_TEMPLATE_DPU = Messages.getString( "FMT_ERR_NOT_REPORT_TEMPLATE_DPU" ); //$NON-NLS-1$
  String MSG_ERR_NO_PARAMS               = Messages.getString( "MSG_ERR_NO_PARAMS" );               //$NON-NLS-1$

  /**
   * {@link EAggregationFunc}
   */
  String STR_N_AVERAGE = Messages.getString( "STR_N_AVERAGE" ); //$NON-NLS-1$
  String STR_D_AVERAGE = Messages.getString( "STR_D_AVERAGE" ); //$NON-NLS-1$
  String STR_N_MIN     = Messages.getString( "STR_N_MIN" );     //$NON-NLS-1$
  String STR_D_MIN     = Messages.getString( "STR_D_MIN" );     //$NON-NLS-1$
  String STR_N_MAX     = Messages.getString( "STR_N_MAX" );     //$NON-NLS-1$
  String STR_D_MAX     = Messages.getString( "STR_D_MAX" );     //$NON-NLS-1$
  String STR_N_SUM     = Messages.getString( "STR_N_SUM" );     //$NON-NLS-1$
  String STR_D_SUM     = Messages.getString( "STR_D_SUM" );     //$NON-NLS-1$
  String STR_N_COUNT   = Messages.getString( "STR_N_COUNT" );   //$NON-NLS-1$
  String STR_D_COUNT   = Messages.getString( "STR_D_COUNT" );   //$NON-NLS-1$

  /**
   * {@link IVtTemplateEditorServiceHardConstants}
   */
  String STR_N_AGGR_FUNC           = Messages.getString( "STR_N_AGGR_FUNC" );           //$NON-NLS-1$
  String STR_D_AGGR_FUNC           = Messages.getString( "STR_D_AGGR_FUNC" );           //$NON-NLS-1$
  String STR_N_DISPLAY_FORMAT      = Messages.getString( "STR_N_DISPLAY_FORMAT" );      //$NON-NLS-1$
  String STR_D_DISPLAY_FORMAT      = Messages.getString( "STR_D_DISPLAY_FORMAT" );      //$NON-NLS-1$
  String STR_N_TITLE               = Messages.getString( "STR_N_TITLE" );               //$NON-NLS-1$
  String STR_D_TITLE               = Messages.getString( "STR_D_TITLE" );               //$NON-NLS-1$
  String STR_N_TEMPLATE_PARAMS     = Messages.getString( "STR_N_TEMPLATE_PARAMS" );     //$NON-NLS-1$
  String STR_D_TEMPLATE_PARAMS     = Messages.getString( "STR_D_TEMPLATE_PARAMS" );     //$NON-NLS-1$
  String STR_N_HAS_SUMMARY         = Messages.getString( "STR_N_HAS_SUMMARY" );         //$NON-NLS-1$
  String STR_D_HAS_SUMMARY         = Messages.getString( "STR_D_HAS_SUMMARY" );         //$NON-NLS-1$
  String STR_N_AGGR_STEP           = Messages.getString( "STR_N_AGGR_STEP" );           //$NON-NLS-1$
  String STR_D_AGGR_STEP           = Messages.getString( "STR_D_AGGR_STEP" );           //$NON-NLS-1$
  String STR_N_CLB_TEMPLATE_PARAMS = Messages.getString( "STR_N_CLB_TEMPLATE_PARAMS" ); //$NON-NLS-1$
  String STR_D_CLB_TEMPLATE_PARAMS = Messages.getString( "STR_D_CLB_TEMPLATE_PARAMS" ); //$NON-NLS-1$
  String STR_N_TEMPLATE_AUTHOR     = Messages.getString( "STR_N_TEMPLATE_AUTHOR" );     //$NON-NLS-1$
  String STR_D_TEMPLATE_AUTHOR     = Messages.getString( "STR_D_TEMPLATE_AUTHOR" );     //$NON-NLS-1$
  String STR_N_MAX_EXECUTION_TIME  = Messages.getString( "STR_N_MAX_EXECUTION_TIME" );  //$NON-NLS-1$
  String STR_D_MAX_EXECUTION_TIME  = Messages.getString( "STR_D_MAX_EXECUTION_TIME" );  //$NON-NLS-1$

  String STR_ABKIND_TEMPLATES         = Messages.getString( "STR_ABKIND_TEMPLATES" );         //$NON-NLS-1$
  String STR_ABKIND_TEMPLATES_D       = Messages.getString( "STR_ABKIND_TEMPLATES_D" );       //$NON-NLS-1$
  String STR_ABILITY_EDIT_TEMPLATES   = Messages.getString( "STR_ABILITY_EDIT_TEMPLATES" );   //$NON-NLS-1$
  String STR_ABILITY_EDIT_TEMPLATES_D = Messages.getString( "STR_ABILITY_EDIT_TEMPLATES_D" ); //$NON-NLS-1$

  /**
   * {@link EJrParamSourceType}
   */
  String STR_N_DATA           = Messages.getString( "STR_N_DATA" );           //$NON-NLS-1$
  String STR_N_ATTRIBUTES     = Messages.getString( "STR_N_ATTRIBUTES" );     //$NON-NLS-1$
  String STR_N_RRI_ATTRIBUTES = Messages.getString( "STR_N_RRI_ATTRIBUTES" ); //$NON-NLS-1$

}
