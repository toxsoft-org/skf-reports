package org.toxsoft.skf.reports.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.km5.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.gui.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.skf.reports.gui.panels.valed.*;
import org.toxsoft.skf.reports.gui.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

import net.sf.jasperreports.engine.*;

/**
 * M5-model of {@link IVtSpecReportParam}.
 *
 * @author max
 */
public class SpecReportParamM5Model
    extends M5Model<IVtSpecReportParam> {

  private static class ReportParamPaneComponentModown
      extends MultiPaneComponentModown<IVtSpecReportParam>
      implements ITsSelectionChangeListener<IVtSpecReportParam> {

    ReportParamPaneComponentModown( ITsGuiContext aContext, IM5Model<IVtSpecReportParam> aModel,
        IM5ItemsProvider<IVtSpecReportParam> aItemsProvider,
        IM5LifecycleManager<IVtSpecReportParam> aLifecycleManager ) {
      super( aContext, aModel, aItemsProvider, aLifecycleManager );
      addTsSelectionListener( this );

    }

    @Override
    protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
        IListEdit<ITsActionDef> aActs ) {
      aActs.add( ACDEF_SEPARATOR );
      aActs.add( ACDEF_COPY_PARAM );
      aActs.add( ACDEF_SEPARATOR );
      aActs.add( ACDEF_SELECT_MULTY_PARAMS );

      ITsToolbar toolbar =

          super.doCreateToolbar( aContext, aName, aIconSize, aActs );

      toolbar.addListener( aActionId -> {
        // nop

      } );

      return toolbar;
    }

    @Override
    protected void doProcessAction( String aActionId ) {
      IVtSpecReportParam selParam = selectedItem();

      switch( aActionId ) {
        case ACTID_COPY_PARAM:
          copyParam( selParam );
          break;
        case ACTID_SELECT_MULTY_PARAMS:
          selectParams( selParam );
          break;

        default:
          throw new TsNotAllEnumsUsedRtException( aActionId );
      }
    }

    private void copyParam( IVtSpecReportParam aSelParam ) {
      IM5Model<IVtSpecReportParam> model = getModel();
      IM5Bunch<IVtSpecReportParam> originalBunch = model.valuesOf( aSelParam );
      IM5BunchEdit<IVtSpecReportParam> copyBunch = new M5BunchEdit<>( model );
      for( IM5FieldDef<IVtSpecReportParam, ?> fd : originalBunch.model().fieldDefs() ) {
        copyBunch.set( fd.id(), originalBunch.get( fd ) );
      }
      ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
      IVtSpecReportParam copyParam = M5GuiUtils.askCreate( tsContext(), model, copyBunch, cdi, lifecycleManager() );
      if( copyParam != null ) {
        // создали копию, обновим список
        refresh();
      }
    }

    private IM5Model<IVtSpecReportParam> getModel() {
      ISkConnection conn = connection();

      IM5Domain m5 = conn.scope().get( IM5Domain.class );
      IM5Model<IVtSpecReportParam> model = m5.getModel( MODEL_ID, IVtSpecReportParam.class );
      return model;
    }

    private ISkConnection connection() {
      ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
      ISkConnection conn = connSup.defConn();
      return conn;
    }

    private void selectParams( IVtSpecReportParam aSelParam ) {
      IGwidList gwidList = new GwidList();

      if( aSelParam != null ) {
        gwidList = new GwidList( aSelParam.gwid() );
      }
      gwidList = PanelGwidListSelector.selectGwids( gwidList, tsContext() );

      if( gwidList != null ) {
        IM5Model<IVtSpecReportParam> model = getModel();
        IM5Bunch<IVtSpecReportParam> originalBunch =
            aSelParam == null ? lifecycleManager().createNewItemValues() : model.valuesOf( aSelParam );
        for( Gwid gwid : gwidList ) {
          IM5BunchEdit<IVtSpecReportParam> copyBunch = new M5BunchEdit<>( model );
          for( IM5FieldDef<IVtSpecReportParam, ?> fd : originalBunch.model().fieldDefs() ) {
            if( fd.id().equals( FID_GWID ) ) {
              copyBunch.set( fd.id(), AvUtils.avValobj( gwid ) );
            }
            else {
              if( fd.id().equals( FID_TITLE ) ) {
                ISkObject skObj = connection().coreApi().objService().find( new Skid( gwid.classId(), gwid.strid() ) );
                copyBunch.set( fd.id(), AvUtils.avStr( skObj.nmName() ) );
              }
              else {
                copyBunch.set( fd.id(), originalBunch.get( fd ) );
              }
            }
          }
          lifecycleManager().create( copyBunch );
        }
        // добавили новые параметры, обновим список
        refresh();
      }
    }

    @Override
    public void onTsSelectionChanged( Object aSource, IVtSpecReportParam aSelectedItem ) {
      // регулируем активность
      if( aSelectedItem != null ) {
        toolbar().getAction( ACTID_COPY_PARAM ).setEnabled( true );
      }
      else {
        toolbar().getAction( ACTID_COPY_PARAM ).setEnabled( false );
      }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected IVtSpecReportParam doAddItem() {
      ((M5SingleLookupFieldDef<IVtSpecReportParam, String>)model().fieldDefs().findByKey( FID_JR_PARAM ))
          .setLookupProvider( new JrParamIdsM5LookupProvider( tsContext() ) );

      return super.doAddItem();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected IVtSpecReportParam doEditItem( IVtSpecReportParam aItem ) {
      ((M5SingleLookupFieldDef<IVtSpecReportParam, String>)model().fieldDefs().findByKey( FID_JR_PARAM ))
          .setLookupProvider( new JrParamIdsM5LookupProvider( tsContext() ) );

      if( aItem.gwid() != null ) {
        ValedGwidEditor.OPDEF_GWID_KIND.setValue( tsContext().params(), AvUtils.avValobj( aItem.gwid().kind() ) );
      }
      return super.doEditItem( aItem );
    }

  }

  /**
   * model id
   */
  public static final String MODEL_ID                = "sk.SpecReportParam"; //$NON-NLS-1$
  /**
   * id field of Gwid kind
   */
  public static final String FID_GWID_KIND           = "gwidKind";           //$NON-NLS-1$
  /**
   * id field of Gwid
   */
  public static final String FID_GWID                = "gwid";               //$NON-NLS-1$
  /**
   * title of param
   */
  public static final String FID_TITLE               = "title";              //$NON-NLS-1$
  /**
   * description of param
   */
  public static final String FID_DESCR               = "descr";              //$NON-NLS-1$
  /**
   * id field of aggregation func
   */
  public static final String FID_AGGR_FUNC           = "aggrFunc";           //$NON-NLS-1$
  /**
   * id field of display format
   */
  public static final String FID_DISPL_FORMAT        = "displayFormat";      //$NON-NLS-1$
  /**
   * id field of jr param id
   */
  public static final String FID_JR_PARAM            = "jrParam";            //$NON-NLS-1$
  /**
   * field of preset value of param
   */
  public static final String FID_PRESET_VALUE        = "presetValue";        //$NON-NLS-1$
  /**
   * field of flag for overide value of param
   */
  public static final String FID_FLAG_OVERRIDE_VALUE = "overrideValue";      //$NON-NLS-1$

  final static String ACTID_SELECT_MULTY_PARAMS = SK_ID + ".reports.SelectParams"; //$NON-NLS-1$

  final static TsActionDef ACDEF_SELECT_MULTY_PARAMS = TsActionDef.ofPush2( ACTID_SELECT_MULTY_PARAMS,
      STR_N_SELECT_MULTY_PARAMS, STR_D_SELECT_MULTY_PARAMS, ITsStdIconIds.ICONID_ITEMS_CHECK_GROUP );

  final static String ACTID_COPY_PARAM = SK_ID + ".reports.CopyParam"; //$NON-NLS-1$

  final static TsActionDef ACDEF_COPY_PARAM =
      TsActionDef.ofPush2( ACTID_COPY_PARAM, STR_N_COPY_PARAM, STR_D_COPY_PARAM, ITsStdIconIds.ICONID_EDIT_COPY );

  /**
   * Attribute {@link IVtSpecReportParam#jrParamSourceType() }
   */

  public M5AttributeFieldDef<IVtSpecReportParam> GWID_KIND = new M5AttributeFieldDef<>( FID_GWID_KIND, VALOBJ, //
      TSID_NAME, STR_N_PARAM_GWID_TYPE, //
      TSID_DESCRIPTION, STR_D_PARAM_GWID_TYPE, //
      TSID_KEEPER_ID, EJrParamSourceType.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EJrParamSourceType.RTDATA ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avValobj( aEntity.jrParamSourceType() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#gwid() } Green world ID
   */
  public M5AttributeFieldDef<IVtSpecReportParam> GWID = new M5AttributeFieldDef<>( FID_GWID, VALOBJ, //
      TSID_NAME, STR_N_PARAM_GWID, //
      TSID_DESCRIPTION, STR_D_PARAM_GWID, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, SpecValedAvValobjGwidEditor.FACTORY_NAME, //
      ValedGwidEditor.OPID_IS_EMPTY_GWID_VALID, AV_TRUE, //
      TSID_IS_MANDATORY, AV_FALSE ) {

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avValobj( aEntity.gwid() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#aggrFunc() } function of aggregation for values
   */
  public M5AttributeFieldDef<IVtSpecReportParam> AGGR_FUNC = new M5AttributeFieldDef<>( FID_AGGR_FUNC, VALOBJ, //
      TSID_NAME, STR_N_PARAM_AGGR_FUNC, //
      TSID_DESCRIPTION, STR_D_PARAM_AGGR_FUNC, //
      TSID_KEEPER_ID, EAggregationFunc.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EAggregationFunc.AVERAGE ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avValobj( aEntity.aggrFunc() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#displayFormat() } values display format
   */
  public M5AttributeFieldDef<IVtSpecReportParam> DISPL_FORMAT = new M5AttributeFieldDef<>( FID_DISPL_FORMAT, VALOBJ, //
      TSID_NAME, STR_N_PARAM_DISPLAY_FORMAT, //
      TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_FORMAT, //
      TSID_KEEPER_ID, EDisplayFormat.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EDisplayFormat.TWO_DIGIT ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avValobj( aEntity.displayFormat() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#title() } title of parameter
   */
  public M5AttributeFieldDef<IVtSpecReportParam> TITLE = new M5AttributeFieldDef<>( FID_TITLE, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_TITLE, //
      TSID_DESCRIPTION, STR_D_PARAM_TITLE, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avStr( aEntity.title() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#description() } description of parameter
   */
  public M5AttributeFieldDef<IVtSpecReportParam> DESCR = new M5AttributeFieldDef<>( FID_DESCR, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_DESCRIPTION, //
      TSID_DESCRIPTION, STR_D_PARAM_DESCRIPTION, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
      return avStr( aEntity.description() );
    }

  };

  /**
   * Attribute {@link IVtSpecReportParam#jrParamId() } preset value of param
   */
  public M5AttributeFieldDef<IVtSpecReportParam> PRESET_VALUE =
      new M5AttributeFieldDef<>( FID_PRESET_VALUE, EAtomicType.STRING, //
          TSID_NAME, STR_N_PARAM_JR_PARAM_VALUE, //
          TSID_DESCRIPTION, STR_D_PARAM_JR_PARAM_VALUE, //
          OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
          return avStr( aEntity.value() );
        }

      };

  /**
   * Attribute {@link IVtSpecReportParam#jrParamId() } jr param from design form
   */
  public M5SingleLookupFieldDef<IVtSpecReportParam, String> JR_PARAM =
      new M5SingleLookupFieldDef<>( FID_JR_PARAM, JrParamModel.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_PARAM_JR_PARAM, STR_D_PARAM_JR_PARAM );
          setFlags( M5FF_COLUMN );
        }

        protected String doGetFieldValue( IVtSpecReportParam aEntity ) {
          return aEntity.jrParamId();
        }

      };

  /**
   * Attribute {@link IVtSpecReportParam#jrParamId() } preset value of param
   */
  public M5AttributeFieldDef<IVtSpecReportParam> FLAG_OVERRIDE_VALUE =
      new M5AttributeFieldDef<>( FID_FLAG_OVERRIDE_VALUE, EAtomicType.BOOLEAN, //
          TSID_NAME, STR_N_PARAM_JR_PARAM_RESET, //
          TSID_DESCRIPTION, STR_D_PARAM_JR_PARAM_RESET //
      ) {

        @Override
        protected void doInit() {
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( IVtSpecReportParam aEntity ) {
          return avBool( aEntity.canBeOverwritten() );
        }

      };

  /**
   * Constructor
   */
  public SpecReportParamM5Model() {
    super( MODEL_ID, IVtSpecReportParam.class );

    addFieldDefs( GWID_KIND, GWID, TITLE, DESCR, AGGR_FUNC, DISPL_FORMAT, JR_PARAM, PRESET_VALUE, FLAG_OVERRIDE_VALUE );
    // panels creator
    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<IVtSpecReportParam> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<IVtSpecReportParam> aItemsProvider,
          IM5LifecycleManager<IVtSpecReportParam> aLifecycleManager ) {
        MultiPaneComponentModown<IVtSpecReportParam> mpc =
            new ReportParamPaneComponentModown( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }

      @Override
      protected IM5EntityPanel<IVtSpecReportParam> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<IVtSpecReportParam> aLifecycleManager ) {
        ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
        ISkConnection conn = connSup.defConn();

        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, new Controller( conn ) );
      }

    } );
  }

  class Controller
      extends M5EntityPanelWithValedsController<IVtSpecReportParam> {

    private final ISkConnection conn;

    public Controller( ISkConnection aConn ) {
      super();
      conn = aConn;
    }

    @Override
    public boolean doProcessEditorValueChange( IValedControl<?> aEditor, IM5FieldDef<IVtSpecReportParam, ?> aFieldDef,
        boolean aEditFinished ) {
      switch( aFieldDef.id() ) {
        case FID_GWID_KIND:
          // when changing the Gwid Kind then autochange gwid select dialog context
          IAtomicValue avv = (IAtomicValue)editors().getByKey( FID_GWID_KIND ).getValue();

          SpecValedGwidEditor.OPDEF_GWID_KIND_BY_JR.setValue( tsContext().params(), avv );

          break;
        case FID_GWID:
          // when changing the Gwid then autocomplete name and description
          IAtomicValue av = (IAtomicValue)editors().getByKey( FID_GWID ).getValue();
          if( av == null || !av.isAssigned() ) {
            break;
          }
          Gwid paramGwid = av.asValobj();
          ISkClassInfo ci = conn.coreApi().sysdescr().findClassInfo( paramGwid.classId() );
          ISkObject editObj = conn.coreApi().objService().find( paramGwid.skid() );
          if( ci != null && editObj != null ) {
            // IDtoRtdataInfo rtDataInfo = ci.rtdata().list().findByKey( paramGwid.propId() );
            // работаем только в том случае если поле пустое
            av = (IAtomicValue)editors().getByKey( FID_TITLE ).getValue();
            if( av.asString().isBlank() ) {
              ValedAvStringText valedTitle = getEditor( FID_TITLE, ValedAvStringText.class );
              // dima 27.07.23 по указанию Синько подставляем имя и описание объекта
              // valedTitle.setValue( AvUtils.avStr( rtDataInfo.nmName() ) );
              valedTitle.setValue( AvUtils.avStr( editObj.nmName() ) );
            }
            av = (IAtomicValue)editors().getByKey( FID_DESCR ).getValue();
            if( av.asString().isBlank() ) {
              ValedAvStringText valedDescr = getEditor( FID_DESCR, ValedAvStringText.class );
              // valedDescr.setValue( AvUtils
              // .avStr( !(rtDataInfo.description().isBlank()) ? rtDataInfo.description() : rtDataInfo.nmName() ) );
              valedDescr.setValue( AvUtils.avStr( editObj.description() ) );
            }
          }
          break;
        default:
          break;
      }
      return true;
    }

  }

  @Override
  protected IM5LifecycleManager<IVtSpecReportParam> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new SpecReportParamM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IVtSpecReportParam> doCreateLifecycleManager( Object aMaster ) {
    return new SpecReportParamM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

  /**
   * Provider of jr params and fields identifiers
   *
   * @author max
   */
  private static class JrParamIdsM5LookupProvider
      implements IM5LookupProvider<String> {

    private ITsGuiContext context;

    /**
     * Constructor by context
     *
     * @param aContext - context
     */
    JrParamIdsM5LookupProvider( ITsGuiContext aContext ) {
      context = aContext;
    }

    @Override
    public IList<String> listItems() {
      JasperReport jasperReport = (JasperReport)context.find( IReportsGuiConstants.JR_TEMPLATE );

      IListEdit<String> result = new ElemArrayList<>();
      if( jasperReport != null ) {
        JRParameter[] params = jasperReport.getParameters();
        if( params != null ) {
          for( JRParameter p : params ) {
            if( !p.isSystemDefined() ) {
              result.add( String.format( ReportTemplateUtilities.JR_PARAM_PARAM_FORMAT, p.getName() ) );
            }
          }
        }
        JRField[] fields = jasperReport.getFields();
        if( fields != null ) {
          for( JRField f : fields ) {
            result.add( String.format( ReportTemplateUtilities.JR_PARAM_FIELD_FORMAT, f.getName() ) );
          }
        }
      }
      return result;
    }

  }

}
