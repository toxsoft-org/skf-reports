package org.toxsoft.skf.reports.templates.service.impl;

import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;

import java.util.*;

import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.objserv.*;

/**
 * {@link IVtSpecReportTemplate} implementation.
 *
 * @author max
 */
public class VtSpecReportTemplate
    extends VtBaseTemplate<IVtSpecReportParam>
    implements IVtSpecReportTemplate {

  static final ISkObjectCreator<VtSpecReportTemplate> CREATOR = VtSpecReportTemplate::new;

  VtSpecReportTemplate( Skid aSkid ) {
    super( aSkid );
  }

  // ------------------------------------------------------------------------------------
  // IVtReportTemplate
  //

  @Override
  public IList<IVtSpecReportParam> listParams() {
    String paramsStr = coreApi().clobService().readClob( parametersGwid() );
    return VtSpecReportParam.KEEPER.str2coll( paramsStr );
  }

  @Override
  public String design() {
    Gwid designGwid = Gwid.createClob( classId(), strid(), CLBID_TEMPLATE_DESIGN );
    String design64 = coreApi().clobService().readClob( designGwid );
    String designStr = new String( Base64.getDecoder().decode( design64.getBytes() ) );
    return designStr;
  }

}
