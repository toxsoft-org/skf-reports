package org.toxsoft.skf.reports.chart.utils.gui.panels;

import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Contains data of one chart
 *
 * @author vs
 * @author dima // ts4 conversion
 */
public class GraphicInfo {

  private static final String MAIN_CANVAS_ID = "mainCanvas"; //$NON-NLS-1$

  private final IStridable           nameable;
  private final String               axisId;
  private final String               dataSetId;
  private final Pair<Double, Double> minMax;
  private final boolean              ladder;
  private IPlotDef                   plotDef = null;
  private boolean                    visible = true;

  /**
   * Constructor.
   *
   * @param aNameable IStridable - id and name.
   * @param aAxisId String - id of Y axis.
   * @param aDataSetId String - id of chart - the same as from aNameable //TODO - FIXIT
   * @param aMinMax Pair - min and max values of chart data
   * @param aIsLadder boolean - true - if chart is ladder, false - else.
   */
  public GraphicInfo( IStridable aNameable, String aAxisId, String aDataSetId, Pair<Double, Double> aMinMax,
      boolean aIsLadder ) {
    nameable = aNameable;
    axisId = aAxisId;
    dataSetId = aDataSetId;
    minMax = aMinMax;
    ladder = aIsLadder;
  }

  /**
   * Returns id of chart.
   *
   * @return String - id of chart.
   */
  public String id() {
    return nameable.id();
  }

  IPlotDef plotDef() {
    return plotDef;
  }

  /**
   * Returns min and max values of chart data.
   *
   * @return Pair - min and max values of chart data.
   */
  public Pair<Double, Double> minMax() {
    return minMax;
  }

  boolean isVisibe() {
    return visible;
  }

  void setVisible( boolean aVisible ) {
    visible = aVisible;
  }

  /**
   * Creates chart plot definition.
   *
   * @param aPlotTuner PlotDefTuner - tuner of plot definition
   * @return IPlotDef - chart plot definition.
   */
  public IPlotDef createPlotDef( PlotDefTuner aPlotTuner ) {
    if( ladder ) {
      aPlotTuner.setRenderingKind( EGraphicRenderingKind.LADDER );
    }
    plotDef = aPlotTuner.createPlotDef( nameable, axisId, dataSetId, MAIN_CANVAS_ID );
    return plotDef;
  }

  /**
   * Returns id of chart.
   *
   * @return String - id of chart.
   */
  public String getDataSetId() {
    return dataSetId;
  }

}
