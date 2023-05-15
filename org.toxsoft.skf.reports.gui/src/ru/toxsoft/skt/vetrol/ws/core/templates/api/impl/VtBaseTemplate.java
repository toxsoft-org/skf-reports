package ru.toxsoft.skt.vetrol.ws.core.templates.api.impl;

import static ru.toxsoft.skt.vetrol.ws.core.templates.api.IVtTemplateEditorServiceHardConstants.*;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.impl.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

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
