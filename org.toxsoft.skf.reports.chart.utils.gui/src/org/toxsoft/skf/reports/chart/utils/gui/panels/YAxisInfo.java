package org.toxsoft.skf.reports.chart.utils.gui.panels;

import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.*;

public class YAxisInfo {

  private final String                      id;
  private final Pair<String, String>        unitInfo;
  private final IStringMapEdit<GraphicInfo> graphicInfoes = new StringMap<>();

  private Double min = null;
  private Double max = null;

  public YAxisInfo( String aId, Pair<String, String> aUnitInfo ) {
    id = aId;
    unitInfo = aUnitInfo;
  }

  public String id() {
    return id;
  }

  public IStringMapEdit<GraphicInfo> graphicInfoes() {
    return graphicInfoes;
  }

  public void putGraphicInfo( GraphicInfo aGraphInfo ) {
    Pair<Double, Double> minMax = aGraphInfo.minMax();
    if( min == null || min > minMax.left() ) {
      min = minMax.left();
    }
    if( max == null || max < minMax.right() ) {
      max = minMax.right();
    }
    graphicInfoes.put( aGraphInfo.id(), aGraphInfo );
  }

  public Pair<String, String> unitInfo() {
    return unitInfo;
  }
}
