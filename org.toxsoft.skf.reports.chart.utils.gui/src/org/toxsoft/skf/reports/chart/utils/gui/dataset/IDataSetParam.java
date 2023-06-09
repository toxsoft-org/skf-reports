package org.toxsoft.skf.reports.chart.utils.gui.dataset;

import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Interface to specify one parameter for data set
 *
 * @author dima
 */

public interface IDataSetParam {

  /**
   * @return green world id
   */
  Gwid gwid();

  /**
   * @return id
   */
  String aggrFuncId();

  /**
   * @return name of unit for Y scale
   */
  int aggrStep();

}
