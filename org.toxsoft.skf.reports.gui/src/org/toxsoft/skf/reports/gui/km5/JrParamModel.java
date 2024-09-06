package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.km5.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;

/**
 * JR param id model - for editing report param
 *
 * @author max
 */
public class JrParamModel
    extends M5Model<String> {

  /**
   * model id
   */
  public final static String MODEL_ID = "jr.param.m5.model"; //$NON-NLS-1$

  /**
   * id of Attribute jr param name
   */
  private static final String FID_JR_PARAM_NAME = "jr.param.name"; //$NON-NLS-1$

  /**
   * Attribute jr param name
   */
  public M5AttributeFieldDef<String> NAME = new M5AttributeFieldDef<>( FID_JR_PARAM_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_JR_PARAM_ID, //
      TSID_DESCRIPTION, STR_N_PARAM_JR_PARAM_ID ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( String aEntity ) {
      return avStr( aEntity );
    }

  };

  /**
   * Constructor
   */
  public JrParamModel() {
    super( MODEL_ID, String.class );
    addFieldDefs( NAME );
  }

}
