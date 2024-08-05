package org.toxsoft.skf.reports.templates.service;

import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.*;
import org.toxsoft.uskat.core.api.objserv.*;

/**
 * The special (specific) report template managment service.
 *
 * @author dima
 */
public interface IVtSpecReportTemplateService
    extends ISkService {

  /**
   * Service identifier.
   */
  String SERVICE_ID = ISkHardConstants.SK_CORE_SERVICE_ID_PREFIX + ".SpecReportTemplate"; //$NON-NLS-1$

  /**
   * Returns the list of special report tenmplates.
   *
   * @return {@link IList}&lt;{@link IVtSpecReportTemplate}&gt; - the list of report templates
   */
  IList<IVtSpecReportTemplate> listSpecReportTemplates();

  /**
   * Finds special report template.
   *
   * @param aTempId String - ID of template {@link ISkObject#strid()}
   * @return {@link IVtReportTemplate} - found template or <code>null</code>
   */
  IVtSpecReportTemplate findSpecReportTemplate( String aTempId );

  /**
   * Creates new report template.
   *
   * @param aDtoReportTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException object DTO does not refers to {@link IVtReportTemplate#CLASS_ID}
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  IVtSpecReportTemplate createSpecReportTemplate( IDtoFullObject aDtoReportTemplate );

  /**
   * Edits an existing template.
   * <p>
   *
   * @param aDtoReportTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  IVtSpecReportTemplate editSpecReportTemplate( IDtoFullObject aDtoReportTemplate );

  /**
   * Removes the template.
   *
   * @param aSpecReportTemplateId String - template ID and {@link ISkObject#strid()}
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  void removeSpecReportTemplate( String aSpecReportTemplateId );

  // ------------------------------------------------------------------------------------
  // Service support

  /**
   * Returns the service validator.
   *
   * @return {@link ITsValidationSupport}&lt;{@link IVtReportTemplateServiceValidator}&gt; - the service validator
   */
  // ITsValidationSupport<IVtReportTemplateServiceValidator> svs();

  /**
   * Returns the service eventer.
   *
   * @return {@link ITsEventer}&lt;{@link IVtReportTemplateServiceListener}&gt; - the service eventer
   */
  // ITsEventer<IVtReportTemplateServiceListener> eventer();

}
