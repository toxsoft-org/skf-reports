package org.toxsoft.skf.reports.templates.service;

import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.uskat.core.*;

/**
 * Listener to the {@link IVtReportTemplateService}.
 *
 * @author dima
 */
public interface IVtReportTemplateServiceListener {

  /**
   * Called when any change in templates occur.
   *
   * @param aCoreApi {@link ISkCoreApi} - the event source
   * @param aOp {@link ECrudOp} - the kind of change
   * @param aReportTemplateId String - affected report template or <code>null</code> for batch changes
   *          {@link ECrudOp#LIST}
   */
  void onReportTemplateChanged( ISkCoreApi aCoreApi, ECrudOp aOp, String aReportTemplateId );

}
