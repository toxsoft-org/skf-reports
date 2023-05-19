package org.toxsoft.skf.reports.templates.service.impl;

import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * {@link IVtReportTemplate} implementation.
 *
 * @author dima
 * @param <T> тип параметров шаблона
 */
abstract class VtBaseTemplate<T extends IVtTemplateParam>
    extends SkObject
    implements IVtBaseTemplate<T> {

  private transient Gwid parametersGwid = null;

  VtBaseTemplate( Skid aSkid ) {
    super( aSkid );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  protected Gwid parametersGwid() {
    if( parametersGwid == null ) {
      parametersGwid = Gwid.createClob( classId(), strid(), CLBID_TEMPLATE_PARAMS );
    }
    return parametersGwid;
  }

  // ------------------------------------------------------------------------------------
  // IVtReportTemplate
  //

  @Override
  abstract public IList<T> listParams();

  @Override
  public ISkUser author() {
    return (ISkUser)getLinkObjs( LNKID_TEMPLATE_AUTHOR ).first();
  }

}
