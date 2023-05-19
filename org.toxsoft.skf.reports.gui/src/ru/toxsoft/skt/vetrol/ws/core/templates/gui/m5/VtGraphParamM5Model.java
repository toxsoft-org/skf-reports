package ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5.IVtResources.*;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.bricks.tstree.impl.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.colors.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.misc.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

import ru.toxsoft.skt.vetrol.ws.core.templates.gui.*;
import ru.toxsoft.skt.vetrol.ws.core.templates.gui.valed.*;

/**
 * M5-model of {@link IVtGraphParam}.
 *
 * @author dima
 */
public class VtGraphParamM5Model
    extends M5Model<IVtGraphParam> {

  // FIXME dima 04.04.23 код просто copy-paste VtReportParamM5Model::ReportParamPaneComponentModown
  private static class GraphParamPaneComponentModown
      extends MultiPaneComponentModown<IVtGraphParam>
      implements ITsSelectionChangeListener<IVtGraphParam> {

    GraphParamPaneComponentModown( ITsGuiContext aContext, IM5Model<IVtGraphParam> aModel,
        IM5ItemsProvider<IVtGraphParam> aItemsProvider, IM5LifecycleManager<IVtGraphParam> aLifecycleManager ) {
      super( aContext, aModel, aItemsProvider, aLifecycleManager );
      addTsSelectionListener( this );

    }

    @Override
    public void onTsSelectionChanged( Object aSource, IVtGraphParam aSelectedItem ) {
      // регулируем активность
      if( aSelectedItem != null ) {
        toolbar().getAction( VtReportParamM5Model.ACTID_COPY_PARAM ).setEnabled( true );
      }
      else {
        toolbar().getAction( VtReportParamM5Model.ACTID_COPY_PARAM ).setEnabled( false );
      }
    }

    @Override
    protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
        IListEdit<ITsActionDef> aActs ) {
      aActs.add( ACDEF_SEPARATOR );
      aActs.add( VtReportParamM5Model.ACDEF_COPY_PARAM );
      aActs.add( ACDEF_SEPARATOR );
      aActs.add( VtReportParamM5Model.ACDEF_SELECT_MULTY_PARAMS );

      ITsToolbar toolbar =

          super.doCreateToolbar( aContext, aName, aIconSize, aActs );

      toolbar.addListener( aActionId -> {
        // nop

      } );

      return toolbar;
    }

    @Override
    protected void doProcessAction( String aActionId ) {
      IVtGraphParam selParam = selectedItem();

      switch( aActionId ) {
        case VtReportParamM5Model.ACTID_COPY_PARAM:
          copyParam( selParam );
          break;
        case VtReportParamM5Model.ACTID_SELECT_MULTY_PARAMS:
          selectParams( selParam );
          break;

        default:
          throw new TsNotAllEnumsUsedRtException( aActionId );
      }
    }

    private void copyParam( IVtGraphParam aSelParam ) {
      IM5Model<IVtGraphParam> model = getModel();
      IM5Bunch<IVtGraphParam> originalBunch = model.valuesOf( aSelParam );
      IM5BunchEdit<IVtGraphParam> copyBunch = new M5BunchEdit<>( model );
      for( IM5FieldDef<IVtGraphParam, ?> fd : originalBunch.model().fieldDefs() ) {
        copyBunch.set( fd.id(), originalBunch.get( fd ) );
      }
      ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
      IVtGraphParam copyParam = M5GuiUtils.askCreate( tsContext(), model, copyBunch, cdi, lifecycleManager() );
      if( copyParam != null ) {
        // создали копию, обновим список
        refresh();
      }
    }

    private IM5Model<IVtGraphParam> getModel() {
      ISkConnection conn = connection();

      IM5Domain m5 = conn.scope().get( IM5Domain.class );
      IM5Model<IVtGraphParam> model =
          m5.getModel( IVtTemplateEditorServiceHardConstants.GRAPH_PARAM_MODEL_ID, IVtGraphParam.class );
      return model;
    }

    private ISkConnection connection() {
      ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
      ISkConnection conn = connSup.defConn();
      return conn;
    }

    private void selectParams( IVtGraphParam aSelParam ) {
      IGwidList gwidList = new GwidList();

      if( aSelParam != null ) {
        gwidList = new GwidList( aSelParam.gwid() );
      }
      gwidList = PanelGwidListSelector.selectGwids( gwidList, tsContext() );

      if( gwidList != null ) {
        IM5Model<IVtGraphParam> model = getModel();
        IM5Bunch<IVtGraphParam> originalBunch =
            aSelParam == null ? lifecycleManager().createNewItemValues() : model.valuesOf( aSelParam );
        for( Gwid gwid : gwidList ) {
          IM5BunchEdit<IVtGraphParam> copyBunch = new M5BunchEdit<>( model );
          for( IM5FieldDef<IVtGraphParam, ?> fd : originalBunch.model().fieldDefs() ) {
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

  }

  /**
   * model id
   */
  // public static final String MODEL_ID = "sk.GraphParam"; //$NON-NLS-1$
  /**
   * id field of Gwid
   */
  public static final String FID_GWID         = "gwid";          //$NON-NLS-1$
  /**
   * title of param
   */
  public static final String FID_TITLE        = "title";         //$NON-NLS-1$
  /**
   * description of param
   */
  public static final String FID_DESCR        = "descr";         //$NON-NLS-1$
  /**
   * unit id of param
   */
  public static final String FID_UNIT_ID      = "unitId";        //$NON-NLS-1$
  /**
   * unit name for
   */
  public static final String FID_UNIT_NAME    = "unitName";      //$NON-NLS-1$
  /**
   * id field of aggregation func
   */
  public static final String FID_AGGR_FUNC    = "aggrFunc";      //$NON-NLS-1$
  /**
   * id field of display format
   */
  public static final String FID_DISPL_FORMAT = "displayFormat"; //$NON-NLS-1$
  /**
   * id field of color
   */
  public static final String FID_COLOR        = "color";         //$NON-NLS-1$
  /**
   * id field of line width
   */
  public static final String FID_LINE_WIDTH   = "lineWidth";     //$NON-NLS-1$
  /**
   * id field of flag "draw ladder"
   */
  public static final String FID_IS_LADDER    = "isLadder";      //$NON-NLS-1$

  /**
   * id field of list set points
   */
  public static final String FID_SET_POINTS = "setPoints"; //$NON-NLS-1$

  /**
   * Attribute {@link IVtGraphParam#gwid() } Green world ID
   */
  public M5AttributeFieldDef<IVtGraphParam> GWID = new M5AttributeFieldDef<>( FID_GWID, VALOBJ, //
      TSID_NAME, STR_N_PARAM_GWID, //
      TSID_DESCRIPTION, STR_D_PARAM_GWID, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvValobjGwidEditor.FACTORY_NAME //
  ) {

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avValobj( aEntity.gwid() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#aggrFunc() } function of aggregation for values
   */
  public M5AttributeFieldDef<IVtGraphParam> AGGR_FUNC = new M5AttributeFieldDef<>( FID_AGGR_FUNC, VALOBJ, //
      TSID_NAME, STR_N_PARAM_AGGR_FUNC, //
      TSID_DESCRIPTION, STR_D_PARAM_AGGR_FUNC, //
      TSID_KEEPER_ID, EAggregationFunc.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EAggregationFunc.AVERAGE ) ) {

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avValobj( aEntity.aggrFunc() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#displayFormat() } values display format
   */
  public M5AttributeFieldDef<IVtGraphParam> DISPL_FORMAT = new M5AttributeFieldDef<>( FID_DISPL_FORMAT, VALOBJ, //
      TSID_NAME, STR_N_PARAM_DISPLAY_FORMAT, //
      TSID_DESCRIPTION, STR_D_PARAM_DISPLAY_FORMAT, //
      TSID_KEEPER_ID, EDisplayFormat.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( EDisplayFormat.TWO_DIGIT ) ) {

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avValobj( aEntity.displayFormat() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#title() } title of parameter
   */
  public M5AttributeFieldDef<IVtGraphParam> TITLE = new M5AttributeFieldDef<>( FID_TITLE, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_TITLE, //
      TSID_DESCRIPTION, STR_D_PARAM_TITLE, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avStr( aEntity.title() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#description() } description of parameter
   */
  public M5AttributeFieldDef<IVtGraphParam> DESCR = new M5AttributeFieldDef<>( FID_DESCR, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_DESCRIPTION, //
      TSID_DESCRIPTION, STR_D_PARAM_DESCRIPTION, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avStr( aEntity.description() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#unitId() } id of unit
   */
  public M5AttributeFieldDef<IVtGraphParam> UNIT_ID = new M5AttributeFieldDef<>( FID_UNIT_ID, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_UNIT_ID, //
      TSID_DESCRIPTION, STR_D_PARAM_UNIT_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, AvUtils.avStr( "T" ) // оставлено для примера //$NON-NLS-1$
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avStr( aEntity.unitId() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#unitName() } name of unit
   */
  public M5AttributeFieldDef<IVtGraphParam> UNIT_NAME = new M5AttributeFieldDef<>( FID_UNIT_NAME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_UNIT_NAME, //
      TSID_DESCRIPTION, STR_D_PARAM_UNIT_NAME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      TSID_DEFAULT_VALUE, AvUtils.avStr( "°С" ) // оставлено для примера //$NON-NLS-1$
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avStr( aEntity.unitName() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#color() } description of parameter
   */
  public M5AttributeFieldDef<IVtGraphParam> COLOR = new M5AttributeFieldDef<>( FID_COLOR, VALOBJ, //
      TSID_NAME, STR_N_PARAM_COLOR, //
      TSID_DESCRIPTION, STR_D_PARAM_COLOR, //
      TSID_KEEPER_ID, ETsColor.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ETsColor.BLACK ) ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avValobj( aEntity.color() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#lineWidth() } description of parameter
   */
  public M5AttributeFieldDef<IVtGraphParam> LINE_WIDTH = new M5AttributeFieldDef<>( FID_LINE_WIDTH, EAtomicType.INTEGER, //
      TSID_NAME, STR_N_PARAM_LINE_WIDTH, //
      TSID_DESCRIPTION, STR_D_PARAM_LINE_WIDTH, //
      OPID_EDITOR_FACTORY_NAME, ValedAvIntegerSpinner.FACTORY_NAME, //
      TSID_DEFAULT_VALUE, avInt( 3 ) //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avInt( aEntity.lineWidth() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#isLadder() } description of parameter
   */
  public M5AttributeFieldDef<IVtGraphParam> IS_LADDER = new M5AttributeFieldDef<>( FID_IS_LADDER, EAtomicType.BOOLEAN, //
      TSID_NAME, STR_N_IS_LADDER, //
      TSID_DESCRIPTION, STR_D_IS_LADDER, //
      TSID_DEFAULT_VALUE, avBool( false ) //

  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IVtGraphParam aEntity ) {
      return avBool( aEntity.isLadder() );
    }

  };

  /**
   * Attribute {@link IVtGraphParam#setPoints() } description of parameter
   */
  public M5MultiModownFieldDef<IVtGraphParam, String> SET_POINTS =
      new M5MultiModownFieldDef<>( FID_SET_POINTS, StringM5Model.MODEL_ID ) //

      {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_SET_POINTS, STR_D_SET_POINTS );
          setFlags( M5FF_DETAIL );
          // панель высотой в 4 строки
          params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 4 ); //
          // строка поиска не нужна
          params().setBool( IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE, false );
          // прячем заголовок таблицы
          params().setBool( TsTreeViewer.OPDEF_IS_HEADER_SHOWN, false );
          // прячем тулбар
          // params().setBool( IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR, false );
        }

        protected IStringList doGetFieldValue( IVtGraphParam aEntity ) {
          return aEntity.setPoints();
        }

      };

  /**
   * Constructor
   */
  public VtGraphParamM5Model() {
    super( IVtTemplateEditorServiceHardConstants.GRAPH_PARAM_MODEL_ID, IVtGraphParam.class );

    addFieldDefs( GWID, TITLE, DESCR, UNIT_ID, UNIT_NAME, AGGR_FUNC, DISPL_FORMAT, COLOR, LINE_WIDTH, IS_LADDER,
        SET_POINTS );

    // panels creator
    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5CollectionPanel<IVtGraphParam> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<IVtGraphParam> aItemsProvider, IM5LifecycleManager<IVtGraphParam> aLifecycleManager ) {
        MultiPaneComponentModown<IVtGraphParam> mpc =
            new GraphParamPaneComponentModown( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );

  }

  @Override
  protected IM5LifecycleManager<IVtGraphParam> doCreateDefaultLifecycleManager() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    // TODO which connection to use?
    ISkConnection conn = cs.defConn();
    return new VtGraphParamM5LifecycleManager( this, conn );
  }

  @Override
  protected IM5LifecycleManager<IVtGraphParam> doCreateLifecycleManager( Object aMaster ) {
    return new VtGraphParamM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

}
