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
 * The graph template managment service.
 *
 * @author dima
 */
public interface IVtGraphTemplateService
    extends ISkService {

  /**
   * Service identifier.
   */
  String SERVICE_ID = ISkHardConstants.SK_CORE_SERVICE_ID_PREFIX + ".GraphTemplate"; //$NON-NLS-1$

  /**
   * Returns the list of graph templates.
   *
   * @return {@link IList}&lt;{@link IVtGraphTemplate}&gt; - the list of graph templates
   */
  IList<IVtGraphTemplate> listGraphTemplates();

  /**
   * Finds graph template.
   *
   * @param aTempId String - ID of template {@link ISkObject#strid()}
   * @return {@link IVtReportTemplate} - found template or <code>null</code>
   */
  IVtGraphTemplate findGraphTemplate( String aTempId );

  /**
   * Creates new graph template.
   *
   * @param aDtoGraphTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException object DTO does not refers to {@link IVtGraphTemplate#CLASS_ID}
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  IVtGraphTemplate createGraphTemplate( IDtoFullObject aDtoGraphTemplate );

  /**
   * Edits an existing template.
   * <p>
   *
   * @param aDtoGraphTemplate {@link IDtoFullObject} - the template data
   * @return {@link IVtReportTemplate} - created/update template object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException object DTO does not refers to {@link IVtGraphTemplate#CLASS_ID}
   * @throws TsValidationFailedRtException failed check of {@link IVtGraphTemplateServiceValidator}
   */
  IVtGraphTemplate editGraphTemplate( IDtoFullObject aDtoGraphTemplate );

  /**
   * Removes the template.
   *
   * @param aGraphTemplateId String - template ID and {@link ISkObject#strid()}
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsValidationFailedRtException failed check of {@link IVtReportTemplateServiceValidator}
   */
  void removeGraphTemplate( String aGraphTemplateId );

  // ------------------------------------------------------------------------------------
  // Service support

  /**
   * Returns the service validator.
   *
   * @return {@link ITsValidationSupport}&lt;{@link IVtGraphTemplateServiceValidator}&gt; - the service validator
   */
  ITsValidationSupport<IVtGraphTemplateServiceValidator> svs();

  /**
   * Returns the service eventer.
   *
   * @return {@link ITsEventer}&lt;{@link IVtGraphTemplateServiceListener}&gt; - the service eventer
   */
  ITsEventer<IVtGraphTemplateServiceListener> eventer();

}
