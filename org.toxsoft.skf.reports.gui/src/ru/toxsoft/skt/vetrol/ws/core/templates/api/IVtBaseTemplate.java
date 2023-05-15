package ru.toxsoft.skt.vetrol.ws.core.templates.api;

import org.toxsoft.core.tsgui.chart.api.ETimeUnit;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.uskat.core.api.objserv.ISkObject;
import org.toxsoft.uskat.core.api.users.ISkUser;

/**
 * Interface to specify template of doc report.
 *
 * @author dima
 * @param <T> тип параметра отчета
 */

public interface IVtBaseTemplate<T extends IVtTemplateParam>
    extends ISkObject {

  /**
   * Returns the parameters of template.
   *
   * @return {@link IList}&lt;{@link IVtTemplateParam}&gt; - the parameters of template
   */
  IList<T> listParams();

  /**
   * Returns the author (user) of template.
   *
   * @return {@link ISkUser} - author of template
   */
  ISkUser author();

  /**
   * Determines title of report.
   *
   * @return String - title of report
   */
  default String title() {
    return attrs().getStr( IVtTemplateEditorServiceHardConstants.ATRID_TITLE );
  }

  /**
   * @return { @link ETimeUnit} - time step of aggregation
   */
  default ETimeUnit aggrStep() {
    return attrs().getValobj( IVtTemplateEditorServiceHardConstants.ATRID_AGGR_STEP );
  }

  /**
   * @return long - query max execution time (msec)
   */
  default long maxExecutionTime() {
    return attrs().getLong( IVtTemplateEditorServiceHardConstants.ATRID_MAX_EXECUTION_TIME, 50000 );
  }
}
