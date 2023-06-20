package org.toxsoft.skf.reports.chart.utils.gui.panels;

import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Y axis params
 *
 * @author max
 */
public class YAxisInfo {

  private final String                      id;
  private final Pair<String, String>        unitInfo;
  private final IStringMapEdit<GraphicInfo> graphicInfoes = new StringMap<>();

  private Double min = null;
  private Double max = null;

  /**
   * Constructor
   *
   * @param aId String - y axis id
   * @param aUnitInfo Pair - unit info (id and name)
   */
  public YAxisInfo( String aId, Pair<String, String> aUnitInfo ) {
    id = aId;
    unitInfo = aUnitInfo;
  }

  /**
   * Returns Y axis id
   *
   * @return String - Y axis id
   */
  public String id() {
    return id;
  }

  /**
   * Returns axis charts.
   *
   * @return IStringMapEdit - axis charts.
   */
  public IStringMapEdit<GraphicInfo> graphicInfoes() {
    return graphicInfoes;
  }

  /**
   * Adds chart to axis
   *
   * @param aGraphInfo GraphicInfo - params of added chart.
   */
  public void putGraphicInfo( GraphicInfo aGraphInfo ) {
    Pair<Double, Double> minMax = aGraphInfo.minMax();
    if( min == null || min.doubleValue() > minMax.left().doubleValue() ) {
      min = minMax.left();
    }
    if( max == null || max.doubleValue() < minMax.right().doubleValue() ) {
      max = minMax.right();
    }
    graphicInfoes.put( aGraphInfo.id(), aGraphInfo );
  }

  /**
   * Returns unit info (id and name)
   *
   * @return Pair - unit info (id and name)
   */
  public Pair<String, String> unitInfo() {
    return unitInfo;
  }
}
