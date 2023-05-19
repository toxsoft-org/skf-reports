package org.toxsoft.skf.reports.templates.service.impl;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.objserv.*;

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
