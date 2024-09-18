package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Model for enum {@link EGwidKind} - show proper names and list of items (in provider)
 *
 * @author max
 */
public class SpecGwidKindM5Model
    extends M5Model<EGwidKind> {

  /**
   * model id
   */
  public final static String MODEL_ID = "spec.reports.gwid.kind.m5.model"; //$NON-NLS-1$

  /**
   * id of Attribute jr param name
   */
  private static final String FID_GWID_KIND_NAME = "gwid.kind.name"; //$NON-NLS-1$

  /**
   * Attribute name of EGwidKind
   */
  public M5AttributeFieldDef<EGwidKind> GWID_KIND_NAME =
      new M5AttributeFieldDef<>( FID_GWID_KIND_NAME, EAtomicType.STRING, //
          TSID_NAME, TsLibUtils.EMPTY_STRING, //
          TSID_DESCRIPTION, TsLibUtils.EMPTY_STRING ) {

        @Override
        protected IAtomicValue doGetFieldValue( EGwidKind aEntity ) {
          return AvUtils.avStr( aEntity.name() );
        }

      };

  /**
   * Constructor
   */
  public SpecGwidKindM5Model() {
    super( MODEL_ID, EGwidKind.class );
    addFieldDefs( GWID_KIND_NAME );
  }

}
