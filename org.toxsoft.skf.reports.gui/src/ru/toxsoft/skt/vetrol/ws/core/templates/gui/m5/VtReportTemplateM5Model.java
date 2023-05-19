package ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5.IVtResources.*;

import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * M5-model of {@link IVtReportTemplate}.
 *
 * @author dima
 */
public class VtReportTemplateM5Model
    extends KM5ModelBasic<IVtReportTemplate> {

  /**
   * Структура для описания поля типа списка параметров которые хранятся ВМЕСТЕ с сущностью. Ключевое отличие от связи с
   * объектам в том что по связи объекты хранятся отдельно от сущности.
   */
  public final IM5MultiModownFieldDef<IVtReportTemplate, IVtReportParam> REPORT_PARAMS =
      new M5MultiModownFieldDef<>( CLBID_TEMPLATE_PARAMS, VtReportParamM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_TEMPLATE_PARAMS, STR_D_TEMPLATE_PARAMS );
          setFlags( M5FF_DETAIL );
        }

        protected IList<IVtReportParam> doGetFieldValue( IVtReportTemplate aEntity ) {
          return aEntity.listParams();
        }

      };

  /**
   * Flag existence summary zone
   */
  public final KM5AttributeFieldDef<IVtReportTemplate> HAS_SUMMARY =
      new KM5AttributeFieldDef<>( ATRID_HAS_SUMMARY, IAvMetaConstants.DDEF_TS_BOOL ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_FDEF_HAS_SUMMARY, STR_D_FDEF_HAS_SUMMARY );
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtReportTemplate aEntity ) {
          return AvUtils.avBool( aEntity.hasSummary() );
        }

      };

  /**
   * id field of aggregation step
   */
  public static final String FID_AGGR_STEP = "aggrStep"; //$NON-NLS-1$

  /**
   * Attribute {@link IVtReportTemplate#aggrStep() } step of aggregation for values
   */
  public M5AttributeFieldDef<IVtReportTemplate> AGGR_STEP = new M5AttributeFieldDef<>( FID_AGGR_STEP, VALOBJ, //
      TSID_NAME, STR_N_PARAM_AGGR_STEP, //
      TSID_DESCRIPTION, STR_D_PARAM_AGGR_STEP, //
      TSID_KEEPER_ID, ETimeUnit.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ETimeUnit.MIN01 ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtReportTemplate aEntity ) {
      return avValobj( aEntity.aggrStep() );
    }

  };

  /**
   * Attribute {@link IVtReportTemplate#maxExecutionTime() } query max execution time (msec)
   */
  public M5AttributeFieldDef<IVtReportTemplate> MAX_EXECUTION_TIME =
      new M5AttributeFieldDef<>( ATRID_MAX_EXECUTION_TIME, INTEGER, //
          TSID_NAME, STR_N_PARAM_MAX_EXECUTION_TIME, //
          TSID_DESCRIPTION, STR_D_PARAM_MAX_EXECUTION_TIME, //
          TSID_DEFAULT_VALUE, avInt( 10000 ) ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtReportTemplate aEntity ) {
          return avInt( aEntity.maxExecutionTime() );
        }

      };

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public VtReportTemplateM5Model( ISkConnection aConn ) {
    super( IVtReportTemplate.CLASS_ID, IVtReportTemplate.class, aConn );
    setNameAndDescription( STR_N_REPORT_TEMPLATE, STR_D_REPORT_TEMPLATE );

    // add fields
    addFieldDefs( NAME, DESCRIPTION, AGGR_STEP, MAX_EXECUTION_TIME, HAS_SUMMARY, REPORT_PARAMS );
    // panels creator
    // FIXME 4 debug use default
    // setPanelCreator( new M5DefaultPanelCreator<>() {
    //
    // protected IM5CollectionPanel<IVtReportTemplate> doCreateCollEditPanel( ITsGuiContext aContext,
    // IM5ItemsProvider<IVtReportTemplate> aItemsProvider,
    // IM5LifecycleManager<IVtReportTemplate> aLifecycleManager ) {
    // OPDEF_IS_SUPPORTS_TREE.setValue( aContext.params(), AV_TRUE );
    // OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_TRUE );
    // MultiPaneComponentModown<IVtReportTemplate> mpc =
    // new SkReportTemplateMpc( aContext, model(), aItemsProvider, aLifecycleManager );
    // return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
    // }
    // } );
  }

  @Override
  protected IM5LifecycleManager<IVtReportTemplate> doCreateDefaultLifecycleManager() {
    return new VtReportTemplateM5LifecycleManager( this, skConn() );
  }

  @Override
  protected IM5LifecycleManager<IVtReportTemplate> doCreateLifecycleManager( Object aMaster ) {
    ISkConnection master = ISkConnection.class.cast( aMaster );
    return new VtReportTemplateM5LifecycleManager( this, master );
  }

}
