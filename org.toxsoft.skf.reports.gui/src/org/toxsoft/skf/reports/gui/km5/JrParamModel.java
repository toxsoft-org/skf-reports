package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.skf.reports.templates.service.*;

public class JrParamModel
    extends M5Model<String> {

  public final static String MODEL_ID = "jr.param.m5.model";

  /**
   * Attribute {@link IVtSpecReportParam#description() } description of parameter
   */
  public M5AttributeFieldDef<String> NAME = new M5AttributeFieldDef<>( "jr.param.name", EAtomicType.STRING, //
      TSID_NAME, "Идентификатор", //
      TSID_DESCRIPTION, "Идентификатор" //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( String aEntity ) {
      return avStr( aEntity );
    }

  };

  public JrParamModel() {
    super( MODEL_ID, String.class );
    addFieldDefs( NAME );
  }

}
