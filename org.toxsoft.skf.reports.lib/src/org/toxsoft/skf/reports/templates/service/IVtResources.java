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
  String STR_AVERAGE   = Messages.getString( "STR_AVERAGE" );   //$NON-NLS-1$
  String STR_AVERAGE_D = Messages.getString( "STR_AVERAGE_D" ); //$NON-NLS-1$
  String STR_MIN       = Messages.getString( "STR_MIN" );       //$NON-NLS-1$
  String STR_MIN_D     = Messages.getString( "STR_MIN_D" );     //$NON-NLS-1$
  String STR_MAX       = Messages.getString( "STR_MAX" );       //$NON-NLS-1$
  String STR_MAX_D     = Messages.getString( "STR_MAX_D" );     //$NON-NLS-1$
  String STR_SUM       = Messages.getString( "STR_SUM" );       //$NON-NLS-1$
  String STR_SUM_D     = Messages.getString( "STR_SUM_D" );     //$NON-NLS-1$
  String STR_COUNT     = Messages.getString( "STR_COUNT" );     //$NON-NLS-1$
  String STR_COUNT_D   = Messages.getString( "STR_COUNT_D" );   //$NON-NLS-1$

  /**
   * {@link IVtTemplateEditorServiceHardConstants}
   */
  String STR_AGGR_FUNC             = Messages.getString( "STR_AGGR_FUNC" );             //$NON-NLS-1$
  String STR_AGGR_FUNC_D           = Messages.getString( "STR_AGGR_FUNC_D" );           //$NON-NLS-1$
  String STR_DISPLAY_FORMAT        = Messages.getString( "STR_DISPLAY_FORMAT" );        //$NON-NLS-1$
  String STR_DISPLAY_FORMAT_D      = Messages.getString( "STR_DISPLAY_FORMAT_D" );      //$NON-NLS-1$
  String STR_TITLE                 = Messages.getString( "STR_TITLE" );                 //$NON-NLS-1$
  String STR_TITLE_D               = Messages.getString( "STR_TITLE_D" );               //$NON-NLS-1$
  String STR_TEMPLATE_PARAMS       = Messages.getString( "STR_TEMPLATE_PARAMS" );       //$NON-NLS-1$
  String STR_TEMPLATE_PARAMS_D     = Messages.getString( "STR_TEMPLATE_PARAMS_D" );     //$NON-NLS-1$
  String STR_HAS_SUMMARY           = Messages.getString( "STR_HAS_SUMMARY" );           //$NON-NLS-1$
  String STR_HAS_SUMMARY_D         = Messages.getString( "STR_HAS_SUMMARY_D" );         //$NON-NLS-1$
  String STR_AGGR_STEP             = Messages.getString( "STR_AGGR_STEP" );             //$NON-NLS-1$
  String STR_AGGR_STEP_D           = Messages.getString( "STR_AGGR_STEP_D" );           //$NON-NLS-1$
  String STR_CLB_TEMPLATE_PARAMS   = Messages.getString( "STR_CLB_TEMPLATE_PARAMS" );   //$NON-NLS-1$
  String STR_CLB_TEMPLATE_PARAMS_D = Messages.getString( "STR_CLB_TEMPLATE_PARAMS_D" ); //$NON-NLS-1$
  String STR_TEMPLATE_AUTHOR       = Messages.getString( "STR_TEMPLATE_AUTHOR" );       //$NON-NLS-1$
  String STR_TEMPLATE_AUTHOR_D     = Messages.getString( "STR_TEMPLATE_AUTHOR_D" );     //$NON-NLS-1$
  String STR_MAX_EXECUTION_TIME    = Messages.getString( "STR_MAX_EXECUTION_TIME" );    //$NON-NLS-1$
  String STR_MAX_EXECUTION_TIME_D  = Messages.getString( "STR_MAX_EXECUTION_TIME_D" );  //$NON-NLS-1$

  String STR_ABKIND_TEMPLATES         = Messages.getString( "STR_ABKIND_TEMPLATES" );         //$NON-NLS-1$
  String STR_ABKIND_TEMPLATES_D       = Messages.getString( "STR_ABKIND_TEMPLATES_D" );       //$NON-NLS-1$
  String STR_ABILITY_EDIT_TEMPLATES   = Messages.getString( "STR_ABILITY_EDIT_TEMPLATES" );   //$NON-NLS-1$
  String STR_ABILITY_EDIT_TEMPLATES_D = Messages.getString( "STR_ABILITY_EDIT_TEMPLATES_D" ); //$NON-NLS-1$
  String STR_GRAPH_TEMPLATE_CREATED   = Messages.getString( "STR_GRAPH_TEMPLATE_CREATED" );   //$NON-NLS-1$
  String STR_GRAPH_TEMPLATE_CREATED_D = Messages.getString( "STR_GRAPH_TEMPLATE_CREATED_D" ); //$NON-NLS-1$
  String STR_GRAPH_TEMPLATE_REMOVED   = Messages.getString( "STR_GRAPH_TEMPLATE_REMOVED" );   //$NON-NLS-1$
  String STR_GRAPH_TEMPLATE_REMOVED_D = Messages.getString( "STR_GRAPH_TEMPLATE_REMOVED_D" ); //$NON-NLS-1$
  String STR_COMMENT                  = Messages.getString( "STR_COMMENT" );                  //$NON-NLS-1$
  String STR_COMMENT_D                = Messages.getString( "STR_COMMENT_D" );                //$NON-NLS-1$

  String STR_CLBINF_TEMPLATE_DESIGN   = Messages.getString( "STR_CLBINF_TEMPLATE_DESIGN" );   //$NON-NLS-1$
  String STR_CLBINF_TEMPLATE_DESIGN_D = Messages.getString( "STR_CLBINF_TEMPLATE_DESIGN_D" ); //$NON-NLS-1$

  /**
   * {@link EJrParamSourceType}
   */
  String STR_DATA           = Messages.getString( "STR_DATA" );           //$NON-NLS-1$
  String STR_ATTRIBUTES     = Messages.getString( "STR_ATTRIBUTES" );     //$NON-NLS-1$
  String STR_RRI_ATTRIBUTES = Messages.getString( "STR_RRI_ATTRIBUTES" ); //$NON-NLS-1$

}
