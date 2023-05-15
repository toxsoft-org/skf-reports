package ru.toxsoft.skt.vetrol.ws.core.templates.api;

import org.toxsoft.core.tslib.bricks.events.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.*;
import org.toxsoft.uskat.core.api.objserv.*;

/**
 * The report template managment service.
 *
 * @author dima
 */
public interface IVtReportTemplateService
    extends ISkService {

  /**
   * Service identifier.
   */
  String SERVICE_ID = ISkHardConstants.SK_CORE_SERVICE_ID_PREFIX + ".ReportTemplate"; //$NON-NLS-1$

  /**
   * Returns the list of report tenmplates.
   *
   * @return {@link IList}&lt;{@link IVtReportTemplate}&gt; - the list of report templates
   */
  IList<IVtReportTemplate> listReportTemplates();

  /**
   * Finds report template.
   *
   * @param aTempId String - ID of template {@link ISkObject#strid()}
   * @return {@link IVtReportTemplate} - found template or <code>null</code>
   */
  IVtReportTemplate findReportTemplate( String aTempId );

  /**
   * Creates new report template.
   *
   * @param aDtoReportTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException object DTO does not refers to {@link IVtReportTemplate#CLASS_ID}
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  IVtReportTemplate createReportTemplate( IDtoFullObject aDtoReportTemplate );

  /**
   * Edits an existing template.
   * <p>
   *
   * @param aDtoReportTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  IVtReportTemplate editReportTemplate( IDtoFullObject aDtoReportTemplate );

  /**
   * Removes the template.
   *
   * @param aReportTemplateId String - template ID and {@link ISkObject#strid()}
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  void removeReportTemplate( String aReportTemplateId );

  // ------------------------------------------------------------------------------------
  // Service support

  /**
   * Returns the service validator.
   *
   * @return {@link ITsValidationSupport}&lt;{@link IVtReportTemplateServiceValidator}&gt; - the service validator
   */
  ITsValidationSupport<IVtReportTemplateServiceValidator> svs();

  /**
   * Returns the service eventer.
   *
   * @return {@link ITsEventer}&lt;{@link IVtReportTemplateServiceListener}&gt; - the service eventer
   */
  ITsEventer<IVtReportTemplateServiceListener> eventer();

}
