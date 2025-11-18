package org.toxsoft.skf.reports.gui.utils;

import org.toxsoft.skf.reports.gui.*;

/**
 * Localizable resources.
 *
 * @author dima
 */
interface ISkResources {

  /**
   * {@link IntervalSelectionDialogPanel}
   */
  String START_STR             = Messages.getString( "START_STR" );             //$NON-NLS-1$
  String END_STR               = Messages.getString( "END_STR" );               //$NON-NLS-1$
  String DLG_TITLE_PERIOD_SEL  = Messages.getString( "DLG_TITLE_PERIOD_SEL" );  //$NON-NLS-1$
  String DLG_HEADER_PERIOD_SEL = Messages.getString( "DLG_HEADER_PERIOD_SEL" ); //$NON-NLS-1$

  /**
   * {@link ReportTemplateUtilities}
   */

  String FMT_N_TIME_COLUMN       = Messages.getString( "FMT_N_TIME_COLUMN" );       //$NON-NLS-1$
  String STR_N_TIME_COLUMN       = Messages.getString( "STR_N_TIME_COLUMN" );       //$NON-NLS-1$
  String SUMMARY_FIELD_NAME_STR  = Messages.getString( "SUMMARY_FIELD_NAME_STR" );  //$NON-NLS-1$
  String DEFAULT_EMPTY_VALUE_STR = Messages.getString( "DEFAULT_EMPTY_VALUE_STR" ); //$NON-NLS-1$

  /**
   * {@link IntervalSelectionExtandedDialogPanel}
   */
  String STR_N_INTERVAL_FROM_REFBOOK               = Messages.getString( "STR_N_INTERVAL_FROM_REFBOOK" );               //$NON-NLS-1$
  String STR_ADDITIONAL_TITLE_CORRECT_FROM_REFBOOK = Messages.getString( "STR_ADDITIONAL_TITLE_CORRECT_FROM_REFBOOK" ); //$NON-NLS-1$

  /**
   * {@link YScaleRefbookGenerator}
   */
  String STR_Y_SCALE_ID          = "id шкалы";
  String STR_Y_SCALE_ID_D        = "Идентификатор шкалы";
  String STR_Y_SCALE_UNIT_NAME   = "Ед.изм.";
  String STR_Y_SCALE_UNIT_NAME_D = "Название единиц измерения";
  String STR_Y_SCALE_FORMAT      = "формат";
  String STR_Y_SCALE_FORMAT_D    = "Формат отображения значений";
  String STR_Y_SCALE_MIN         = "min";
  String STR_Y_SCALE_MIN_D       = "Нижнее значение шкалы";
  String STR_Y_SCALE_MAX         = "max";
  String STR_Y_SCALE_MAX_D       = "Верхнее значение шкалы";

}
