package org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.reports.gui.rtchart.data_aliases.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.valed.gwid.*;

/**
 * M5-model of {@link IDataNameAlias}.
 *
 * @author dima
 */
public class DataAliasM5Model
    extends M5Model<IDataNameAlias> {

  /**
   * model id
   */
  public static final String MODEL_ID  = "ci.DataAliasM5Model"; //$NON-NLS-1$
  /**
   * id field of Gwid
   */
  public static final String FID_GWID  = "gwid";                //$NON-NLS-1$
  /**
   * title of param
   */
  public static final String FID_TITLE = "title";               //$NON-NLS-1$
  /**
   * description of param
   */
  public static final String FID_DESCR = "descr";               //$NON-NLS-1$

  /**
   * Attribute {@link IDataNameAlias#gwid() } Green world ID
   */
  public M5AttributeFieldDef<IDataNameAlias> GWID = new M5AttributeFieldDef<>( FID_GWID, VALOBJ, //
      TSID_NAME, STR_PARAM_GWID, //
      TSID_DESCRIPTION, STR_PARAM_GWID_D, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvValobjConcreteGwidEditor.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDataNameAlias aEntity ) {
      return avValobj( aEntity.gwid() );
    }

  };

  /**
   * Attribute {@link IDataNameAlias#title() } title of parameter
   */
  public M5AttributeFieldDef<IDataNameAlias> TITLE = new M5AttributeFieldDef<>( FID_TITLE, EAtomicType.STRING, //
      TSID_NAME, STR_PARAM_TITLE, //
      TSID_DESCRIPTION, STR_PARAM_TITLE_D, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDataNameAlias aEntity ) {
      return avStr( aEntity.title() );
    }

  };

  /**
   * Attribute {@link IDataNameAlias#description() } description of parameter
   */
  public M5AttributeFieldDef<IDataNameAlias> DESCR = new M5AttributeFieldDef<>( FID_DESCR, EAtomicType.STRING, //
      TSID_NAME, STR_PARAM_DESCRIPTION, //
      TSID_DESCRIPTION, STR_PARAM_DESCRIPTION_D, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IDataNameAlias aEntity ) {
      return avStr( aEntity.description() );
    }

  };

  /**
   * Constructor
   */
  public DataAliasM5Model() {
    super( MODEL_ID, IDataNameAlias.class );

    addFieldDefs( GWID, TITLE, DESCR );

  }

  @Override
  protected IM5LifecycleManager<IDataNameAlias> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = cs.defConn();
    return new DataAliasM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IDataNameAlias> doCreateLifecycleManager( Object aMaster ) {
    return new DataAliasM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

}
