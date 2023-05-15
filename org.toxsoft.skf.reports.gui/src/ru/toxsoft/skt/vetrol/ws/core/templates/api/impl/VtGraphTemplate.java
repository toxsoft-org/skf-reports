package ru.toxsoft.skt.vetrol.ws.core.templates.api.impl;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.uskat.core.api.objserv.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

/**
 * {@link IVtGraphTemplate} implementation.
 *
 * @author dima
 */
class VtGraphTemplate
    extends VtBaseTemplate<IVtGraphParam>
    implements IVtGraphTemplate {

  static final ISkObjectCreator<VtGraphTemplate> CREATOR = VtGraphTemplate::new;

  VtGraphTemplate( Skid aSkid ) {
    super( aSkid );
  }

  // ------------------------------------------------------------------------------------
  // IVtGraphTemplate
  //

  @Override
  public IList<IVtGraphParam> listParams() {
    String paramsStr = coreApi().clobService().readClob( parametersGwid() );
    return VtGraphParam.KEEPER.str2coll( paramsStr );
  }

}
