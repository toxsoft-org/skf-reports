package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.km5.ISkResources.*;
import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.valed.controls.basic.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * M5-model of {@link IVtSpecReportTemplate}.
 *
 * @author dima
 */
public class SpecReportTemplateM5Model
    extends KM5ModelBasic<IVtSpecReportTemplate> {

  /**
   * Структура для описания поля типа списка параметров которые хранятся ВМЕСТЕ с сущностью. Ключевое отличие от связи с
   * объектам в том что по связи объекты хранятся отдельно от сущности.
   */
  public final IM5MultiModownFieldDef<IVtSpecReportTemplate, IVtSpecReportParam> REPORT_PARAMS =
      new M5MultiModownFieldDef<>( CLBID_TEMPLATE_PARAMS, SpecReportParamM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_TEMPLATE_PARAMS, STR_D_TEMPLATE_PARAMS );
          setFlags( M5FF_DETAIL );
        }

        protected IList<IVtSpecReportParam> doGetFieldValue( IVtSpecReportTemplate aEntity ) {
          return aEntity.listParams();
        }

      };

  /**
   * Строковое представление (jrxml) шаблона JR
   */
  public final M5AttributeFieldDef<IVtSpecReportTemplate> REPORT_DESIGN =
      new M5AttributeFieldDef<>( CLBID_TEMPLATE_DESIGN, EAtomicType.STRING, //
          TSID_NAME, STR_N_PARAM_DESIGN, TSID_DESCRIPTION, STR_D_PARAM_DESIGN ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_DETAIL | M5FF_READ_ONLY );
          ValedStringText.OPDEF_IS_MULTI_LINE.setValue( params(), AV_TRUE );
          params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 5 ); // fucking Windows remove 1 row
          setValedEditor( ValedAvStringText.FACTORY.factoryName() );
        }

        protected IAtomicValue doGetFieldValue( IVtSpecReportTemplate aEntity ) {
          String design = aEntity.design();

          return avStr( design );
        }

      };

  /**
   * id field of aggregation step
   */
  public static final String FID_AGGR_STEP = "aggrStep"; //$NON-NLS-1$

  /**
   * Attribute {@link IVtReportTemplate#aggrStep() } step of aggregation for values
   */
  public M5AttributeFieldDef<IVtSpecReportTemplate> AGGR_STEP = new M5AttributeFieldDef<>( FID_AGGR_STEP, VALOBJ, //
      TSID_NAME, STR_PARAM_AGGR_STEP, //
      TSID_DESCRIPTION, STR_PARAM_AGGR_STEP_D, //
      TSID_KEEPER_ID, ETimeUnit.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ETimeUnit.MIN01 ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportTemplate aEntity ) {
      return avValobj( aEntity.aggrStep() );
    }

  };

  /**
   * Flag existence summary zone
   */
  public final KM5AttributeFieldDef<IVtSpecReportTemplate> HAS_SUMMARY =
      new KM5AttributeFieldDef<>( ATRID_HAS_SUMMARY, IAvMetaConstants.DDEF_TS_BOOL ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_FDEF_HAS_SUMMARY, STR_D_FDEF_HAS_SUMMARY );
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtSpecReportTemplate aEntity ) {
          return AvUtils.avBool( aEntity.hasSummary() );
        }

      };

  /**
   * Attribute {@link IVtReportTemplate#maxExecutionTime() } query max execution time (msec)
   */
  public M5AttributeFieldDef<IVtSpecReportTemplate> MAX_EXECUTION_TIME =
      new M5AttributeFieldDef<>( ATRID_MAX_EXECUTION_TIME, INTEGER, //
          TSID_NAME, STR_N_PARAM_MAX_EXECUTION_TIME, //
          TSID_DESCRIPTION, STR_D_PARAM_MAX_EXECUTION_TIME, //
          TSID_DEFAULT_VALUE, avInt( 10000 ) ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtSpecReportTemplate aEntity ) {
          return avInt( aEntity.maxExecutionTime() );
        }

      };

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public SpecReportTemplateM5Model( ISkConnection aConn ) {
    super( IVtSpecReportTemplate.CLASS_ID, IVtSpecReportTemplate.class, aConn );
    setNameAndDescription( STR_N_REPORT_TEMPLATE, STR_D_REPORT_TEMPLATE );

    // add fields
    addFieldDefs( NAME, DESCRIPTION, AGGR_STEP, MAX_EXECUTION_TIME, HAS_SUMMARY, REPORT_PARAMS, REPORT_DESIGN );
  }

  @Override
  protected IM5LifecycleManager<IVtSpecReportTemplate> doCreateDefaultLifecycleManager() {
    return new SpecReportTemplateM5LifecycleManager( this, tsContext() );
  }

  @Override
  protected IM5LifecycleManager<IVtSpecReportTemplate> doCreateLifecycleManager( Object aMaster ) {
    ITsGuiContext master = ITsGuiContext.class.cast( aMaster );
    return new SpecReportTemplateM5LifecycleManager( this, master );
  }

}
