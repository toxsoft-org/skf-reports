package org.toxsoft.skf.reports.chart.utils.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.graphics.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.chart.renderers.*;
import org.toxsoft.core.tsgui.graphics.lines.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;

/**
 * Настройщик графика.
 * <p>
 * Класс специально был назван Tuner, а не Builder, чтобы не раздражать Гогу.<br>
 * <b>Мотивация:</b> Использование IOptionSet в качестве хранилища настроек - делает настройку параметров графика
 * неоправданно трудоемким и исключительно неудобочитаемым. Поэтому был создан публичный класс, который позволяет
 * настраивать параметры графика эффективно, в привычном виде.
 *
 * @author vs
 * @author dima // ts4 conversion
 */
public class PlotDefTuner {

  TsLineInfo            lineInfo      = null;
  EGraphicRenderingKind renderingKind = null;
  RGBA                  rgba          = null;
  EDisplayFormat        displayFormat = null;
  IStringList           setPoints     = new StringArrayList();

  private final ITsGuiContext context;

  /**
   * Конструктор
   *
   * @param aContext контекст приложения
   */
  public PlotDefTuner( ITsGuiContext aContext ) {
    context = aContext;
  }

  /**
   * Creates plot definition.
   *
   * @param aNameable IStridable - id and name.
   * @param aAxisId String - id of Y axis.
   * @param aDataSetId String - id of chart - the same as from aNameable //TODO - FIXIT
   * @param aCanvasId String - id of canvas.
   * @return IPlotDef - plot definition.
   */
  public IPlotDef createPlotDef( IStridable aNameable, String aAxisId, String aDataSetId, String aCanvasId ) {
    IOptionSetEdit opSet = new OptionSet();
    if( lineInfo != null ) {
      IStdG2GraphicRendererOptions.GRAPHIC_LINE_INFO.setValue( opSet, avValobj( lineInfo ) );
    }
    if( rgba != null ) {
      IStdG2GraphicRendererOptions.GRAPHIC_RGBA.setValue( opSet, AvUtils.avValobj( rgba ) );
    }
    if( renderingKind != null ) {
      IStdG2GraphicRendererOptions.RENDERING_KIND.setValue( opSet, AvUtils.avValobj( renderingKind ) );
    }
    if( displayFormat != null ) {
      IStdG2GraphicRendererOptions.VALUES_DISPLAY_FORMAT.setValue( opSet, AvUtils.avValobj( displayFormat ) );
    }

    IStdG2GraphicRendererOptions.СHART_SET_POINTS.setValue( opSet, AvUtils.avValobj( setPoints ) );

    IG2Params g2p = G2ChartUtils.createParams( IStdG2GraphicRendererOptions.CONSUMER_NAME, opSet, context );
    return new PlotDef( aNameable, aAxisId, aDataSetId, aCanvasId, g2p );
  }

  /**
   * Установить параметры отрисовки линии
   *
   * @param aLineInfo оприсание линии
   */
  public void setLineInfo( TsLineInfo aLineInfo ) {
    lineInfo = aLineInfo;
  }

  /**
   * Установить цвет линии
   *
   * @param aRgba оприсание цвета
   */
  public void setRGBA( RGBA aRgba ) {
    rgba = aRgba;
  }

  /**
   * Установить формат отображени
   *
   * @param aDisplayFormat описание формата
   */
  public void setDisplayFormat( EDisplayFormat aDisplayFormat ) {
    displayFormat = aDisplayFormat;
  }

  /**
   * Установить тип отрисовки аналогового графика.
   *
   * @param aRenderingKind тип отрисовки
   */
  public void setRenderingKind( EGraphicRenderingKind aRenderingKind ) {
    renderingKind = aRenderingKind;
  }

  /**
   * Установить набор линий уставки
   *
   * @param aSetPoints набор линий уставки
   */
  public void setSetPointsList( IStringList aSetPoints ) {
    setPoints = aSetPoints;
  }

}
