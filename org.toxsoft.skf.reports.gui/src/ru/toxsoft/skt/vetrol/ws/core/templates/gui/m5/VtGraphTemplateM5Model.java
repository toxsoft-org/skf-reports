package ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.api.IVtTemplateEditorServiceHardConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5.IVtResources.*;

import org.toxsoft.core.tsgui.chart.api.ETimeUnit;
import org.toxsoft.core.tsgui.m5.model.IM5LifecycleManager;
import org.toxsoft.core.tsgui.m5.model.IM5MultiModownFieldDef;
import org.toxsoft.core.tsgui.m5.model.impl.M5AttributeFieldDef;
import org.toxsoft.core.tsgui.m5.model.impl.M5MultiModownFieldDef;
import org.toxsoft.core.tslib.av.IAtomicValue;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.km5.KM5ModelBasic;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

/**
 * M5-model of {@link IVtGraphTemplate}.
 *
 * @author dima
 */
public class VtGraphTemplateM5Model
    extends KM5ModelBasic<IVtGraphTemplate> {

  /**
   * Структура для описания поля типа списка параметров которые хранятся ВМЕСТЕ с сущностью. Ключевое отличие от связи с
   * объектам в том что по связи объекты хранятся отдельно от сущности.
   */
  public final IM5MultiModownFieldDef<IVtGraphTemplate, IVtGraphParam> REPORT_PARAMS =
      new M5MultiModownFieldDef<>( CLBID_TEMPLATE_PARAMS, IVtTemplateEditorServiceHardConstants.GRAPH_PARAM_MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_TEMPLATE_PARAMS, STR_D_TEMPLATE_PARAMS );
          setFlags( M5FF_DETAIL );
        }

        protected IList<IVtGraphParam> doGetFieldValue( IVtGraphTemplate aEntity ) {
          return aEntity.listParams();
        }

      };

  /**
   * Attribute {@link IVtGraphTemplate#aggrStep() } step of aggregation for values
   */
  public M5AttributeFieldDef<IVtGraphTemplate> AGGR_STEP = new M5AttributeFieldDef<>( ATRID_AGGR_STEP, VALOBJ, //
      TSID_NAME, STR_N_PARAM_AGGR_STEP, //
      TSID_DESCRIPTION, STR_D_PARAM_AGGR_STEP, //
      TSID_KEEPER_ID, ETimeUnit.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ETimeUnit.MIN01 ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphTemplate aEntity ) {
      return avValobj( aEntity.aggrStep() );
    }

  };

  /**
   * Attribute {@link IVtGraphTemplate#maxExecutionTime() } query max execution time (msec)
   */
  public M5AttributeFieldDef<IVtGraphTemplate> MAX_EXECUTION_TIME =
      new M5AttributeFieldDef<>( ATRID_MAX_EXECUTION_TIME, INTEGER, //
          TSID_NAME, STR_N_PARAM_MAX_EXECUTION_TIME, //
          TSID_DESCRIPTION, STR_D_PARAM_MAX_EXECUTION_TIME, //
          TSID_DEFAULT_VALUE, avInt( 10000 ) ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtGraphTemplate aEntity ) {
          return avInt( aEntity.maxExecutionTime() );
        }

      };

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public VtGraphTemplateM5Model( ISkConnection aConn ) {
    super( IVtGraphTemplate.CLASS_ID, IVtGraphTemplate.class, aConn );
    setNameAndDescription( STR_N_GRAPH_TEMPLATE, STR_D_GRAPH_TEMPLATE );

    // add fields
    addFieldDefs( NAME, DESCRIPTION, AGGR_STEP, MAX_EXECUTION_TIME, REPORT_PARAMS );
  }

  @Override
  protected IM5LifecycleManager<IVtGraphTemplate> doCreateDefaultLifecycleManager() {
    return new VtGraphTemplateM5LifecycleManager( this, skConn() );
  }

  @Override
  protected IM5LifecycleManager<IVtGraphTemplate> doCreateLifecycleManager( Object aMaster ) {
    ISkConnection master = ISkConnection.class.cast( aMaster );
    return new VtGraphTemplateM5LifecycleManager( this, master );
  }

}
