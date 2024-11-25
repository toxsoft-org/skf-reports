package org.toxsoft.skf.reports.chart.utils.gui.panels;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.chart.renderers.*;
import org.toxsoft.core.tsgui.graphics.*;
import org.toxsoft.core.tsgui.graphics.fonts.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Настройщик Y шкалы.
 * <p>
 * Класс специально был назван Tuner, а не Builder, чтобы не раздражать Гогу.<br>
 * <b>Мотивация:</b> Использование IOptionSet в качестве хранилища настроек - делает настройку параметров шкалы
 * неоправданно трудоемким и исключительно неудобочитаемым. Поэтому был создан публичный класс, который позволяет
 * настраивать парметры шкалы эффективно, в привычном виде.
 *
 * @author vs
 * @author dima // ts4 conversion
 */
public class YAxisTuner {

  IOptionSetEdit annoOps;
  IG2Params      annoParams;

  double startVal = 0;
  double endVal   = 100;
  double stepVal  = 10;

  String                      title            = TsLibUtils.EMPTY_STRING;
  ETsOrientation              titleOrientation = ETsOrientation.HORIZONTAL;
  String                      formatStr        = TsLibUtils.EMPTY_STRING;
  IFontInfo                   titleFontInfo    = null;
  private final ITsGuiContext context;

  /**
   * @param aContext контекст приложения
   */
  public YAxisTuner( ITsGuiContext aContext ) {
    context = aContext;
    annoOps = new OptionSet();
    String annoRendererClass = IStdG2AxisAnnotationRendererOptions.CONSUMER_NAME;
    annoParams = G2ChartUtils.createParams( annoRendererClass, annoOps, context );
  }

  /**
   * Creates Y axis definition
   *
   * @param aId String - Y axis id
   * @param aDescription String - description
   * @param aName String - name.
   * @return IYAxisDef - Y axis definition
   */
  public IYAxisDef createAxisDef( String aId, String aDescription, String aName ) {

    String annoRendererClass = IStdG2AxisAnnotationRendererOptions.CONSUMER_NAME;
    // dima 22.11.24 переходим на системный фон
    // String bkgRendererClass = IGradientBackgroundRendererOptions.CONSUMER_NAME;
    String bkgRendererClass = SystemBackgroundRenderer.class.getName();

    IStdG2AxisAnnotationRendererOptions.ANNOTATION_FORMAT.setValue( annoOps, AvUtils.avStr( formatStr ) );
    IStdG2AxisAnnotationRendererOptions.TITLE.setValue( annoOps, AvUtils.avStr( title ) );
    IStdG2AxisAnnotationRendererOptions.TITLE_ORIENTATION.setValue( annoOps, AvUtils.avValobj( titleOrientation ) );
    if( titleFontInfo != null ) {
      IStdG2AxisAnnotationRendererOptions.TITLE_FONT_INFO.setValue( annoOps, AvUtils.avValobj( titleFontInfo ) );
    }
    annoParams = G2ChartUtils.createParams( annoRendererClass, annoOps, context );

    IOptionSetEdit rendererOps = new OptionSet();
    IOptionSetEdit bkgRendererOps = new OptionSet();
    IStdG2AxisRendererOptions.BACKGROUND_RENDERER_CLASS.setValue( rendererOps, AvUtils.avStr( bkgRendererClass ) );
    IGradientBackgroundRendererOptions.HORIZONTAL.setValue( bkgRendererOps, AvUtils.avBool( true ) );
    IStdG2AxisRendererOptions.BACKGROUND_RENDERER_OPS.setValue( rendererOps, AvUtils.avValobj( bkgRendererOps ) );

    IStdG2AxisRendererOptions.ANNOTATION_RENDERER_CLASS.setValue( rendererOps, AvUtils.avStr( annoRendererClass ) );
    IStdG2AxisRendererOptions.ANNOTATION_RENDERER_OPS.setValue( rendererOps, AvUtils.avValobj( annoParams.params() ) );

    IG2Params rendererParams =
        G2ChartUtils.createParams( IStdG2AxisRendererOptions.CONSUMER_NAME, rendererOps, context );
    return G2ChartUtils.createYAxisDef( aId, aDescription, aName, rendererParams, startVal, endVal, stepVal );
  }

  /**
   * Sets start value.
   *
   * @param aValue double - start value.
   */
  public void setStartValue( double aValue ) {
    startVal = aValue;
  }

  /**
   * Sets end value.
   *
   * @param aValue double - end value.
   */
  public void setEndValue( double aValue ) {
    endVal = aValue;
  }

  /**
   * Sets step.
   *
   * @param aValue double - value of step.
   */
  public void setStepValue( double aValue ) {
    stepVal = aValue;
  }

  /**
   * Sets title.
   *
   * @param aTitle String - title.
   */
  public void setTitle( String aTitle ) {
    title = aTitle;
  }

  /**
   * Sets title orientation.
   *
   * @param aOrientation ETsOrientation - title orientation.
   */
  public void setTitleOrientation( ETsOrientation aOrientation ) {
    titleOrientation = aOrientation;
  }

  /**
   * Sets title font.
   *
   * @param aFontInfo IFontInfo - title font.
   */
  public void setTitleFont( IFontInfo aFontInfo ) {
    titleFontInfo = aFontInfo;
  }

  /**
   * Sets y axis values format.
   *
   * @param aFormatStr String - values format.
   */
  public void setFormatString( String aFormatStr ) {
    formatStr = aFormatStr;
  }
}
