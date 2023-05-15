package ru.toxsoft.skt.vetrol.ws.core.templates.api.impl;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.uskat.core.api.objserv.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

/**
 * {@link IVtReportTemplate} implementation.
 *
 * @author dima
 */
class VtReportTemplate
    extends VtBaseTemplate<IVtReportParam>
    implements IVtReportTemplate {

  static final ISkObjectCreator<VtReportTemplate> CREATOR = VtReportTemplate::new;

  VtReportTemplate( Skid aSkid ) {
    super( aSkid );
  }

  // ------------------------------------------------------------------------------------
  // IVtReportTemplate
  //

  @Override
  public IList<IVtReportParam> listParams() {
    String paramsStr = coreApi().clobService().readClob( parametersGwid() );
    return VtReportParam.KEEPER.str2coll( paramsStr );
  }

}
