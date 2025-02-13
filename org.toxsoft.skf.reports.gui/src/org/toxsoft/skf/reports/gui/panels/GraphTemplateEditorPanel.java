package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.reports.gui.IReportsGuiConstants.*;
import static org.toxsoft.skf.reports.gui.panels.ISkResources.*;
import static org.toxsoft.skf.reports.templates.service.IVtTemplateEditorServiceHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.text.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.chart.utils.gui.dataset.*;
import org.toxsoft.skf.reports.chart.utils.gui.panels.*;
import org.toxsoft.skf.reports.gui.km5.*;
import org.toxsoft.skf.reports.gui.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.api.users.ability.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.glib.query.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Панель редактора шаблонов графиков ts4.<br>
 *
 * @author dima
 */
public class GraphTemplateEditorPanel
    extends TsPanel {

  final static String ACTID_FORM_GRAPH = SK_ID + ".users.gui.RunGraphForm"; //$NON-NLS-1$

  final static TsActionDef ACDEF_FORM_GRAPH =
      TsActionDef.ofPush2( ACTID_FORM_GRAPH, STR_N_GENERATE_CHART, STR_D_GENERATE_CHART, ICONID_RUN );

  final ISkConnection                  conn;
  IM5CollectionPanel<IVtGraphTemplate> graphTemplatesPanel;

  private CTabFolder tabFolder;

  TimeInterval initValues =
      new TimeInterval( System.currentTimeMillis() - 24L * 60L * 60L * 1000L, System.currentTimeMillis() );

  SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.YY HH:mm:ss" ); //$NON-NLS-1$

  /**
   * лист отчета
   */
  final ITsNodeKind<IVtGraphTemplate> NK_TEMPLATE_LEAF =
      new TsNodeKind<>( "LeafTemplate", IVtGraphTemplate.class, false, ICONID_GRAPH_TEMPLATE ); //$NON-NLS-1$

  /**
   * узел пользователя
   */
  final ITsNodeKind<ISkUser> NK_USER_NODE = new TsNodeKind<>( "NodeUser", ISkUser.class, true, ICONID_USER ); //$NON-NLS-1$

  protected ChartPanel chartPanel;

  static final String TMID_GROUP_BY_USER = "GroupByUser"; //$NON-NLS-1$

  /**
   * Создатель дерева пользователи-отчеты
   *
   * @author dima
   */
  private class User2GraphTemplatesTreeMaker
      implements ITsTreeMaker<IVtGraphTemplate> {

    private final IVtGraphTemplateService service;

    User2GraphTemplatesTreeMaker( IVtGraphTemplateService aService ) {
      service = aService;
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return aNode.kind() == NK_TEMPLATE_LEAF;
    }

    private IStringMapEdit<DefaultTsNode<ISkUser>> makeUser2TemplatesTypesMap( ITsNode aRootNode ) {
      IStringMapEdit<DefaultTsNode<ISkUser>> retVal = new StringMap<>();
      // получаем все шаблоны системы и строим узлы дерева
      IList<IVtGraphTemplate> templates = service.listGraphTemplates();
      for( IVtGraphTemplate template : templates ) {
        if( !retVal.hasKey( template.author().id() ) ) {
          DefaultTsNode<ISkUser> authorNode = new DefaultTsNode<>( NK_USER_NODE, aRootNode, template.author() );
          // присвоим нормальное имя
          authorNode.setName( template.author().attrs().getStr( IM5Constants.FID_NAME ) );
          retVal.put( template.author().id(), authorNode );
        }
      }
      return retVal;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<IVtGraphTemplate> aTemplates ) {
      IStringMapEdit<DefaultTsNode<ISkUser>> roots = makeUser2TemplatesTypesMap( aRootNode );
      for( IVtGraphTemplate template : aTemplates ) {
        DefaultTsNode<ISkUser> userNode = roots.findByKey( template.author().id() );
        DefaultTsNode<IVtGraphTemplate> templateLeaf = new DefaultTsNode<>( NK_TEMPLATE_LEAF, userNode, template );
        // присвоим нормальное имя
        templateLeaf.setName( template.attrs().getStr( IM5Constants.FID_NAME ) );
        userNode.addNode( templateLeaf );
      }
      return (IList)roots.values();
    }

  }

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public GraphTemplateEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtGraphTemplate> model = m5.getModel( IVtGraphTemplate.CLASS_ID, IVtGraphTemplate.class );

    IM5LifecycleManager<IVtGraphTemplate> lm = new GraphTemplateM5LifecycleManager( model, conn );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_TRUE );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    SashForm sf = new SashForm( this, SWT.HORIZONTAL );
    MultiPaneComponentModown<IVtGraphTemplate> componentModown =
        new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

          @Override
          protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
              EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
            aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
            aActs.add( ReportTemplateEditorPanel.ACDEF_COPY_TEMPLATE );

            if( SHOW_APPLY_BUTTON.getValue( aContext.params() ).asBool() ) {
              aActs.add( ACDEF_SEPARATOR );
              aActs.add( ACDEF_FORM_GRAPH );
            }

            ITsToolbar toolbar =

                super.doCreateToolbar( aContext, aName, aIconSize, aActs );

            toolbar.addListener( aActionId -> {
              // TODO Auto-generated method stub

            } );

            return toolbar;
          }

          @Override
          protected void doProcessAction( String aActionId ) {
            IVtGraphTemplate selTemplate = selectedItem();

            switch( aActionId ) {
              case ReportTemplateEditorPanel.ACTID_COPY_TEMPLATE:
                copyTemplate( selTemplate );
                break;

              case ACTID_FORM_GRAPH:
                formGraph( selTemplate );
                break;
              default:
                throw new TsNotAllEnumsUsedRtException( aActionId );
            }
          }

          private void copyTemplate( IVtGraphTemplate aSelTemplate ) {
            IM5Bunch<IVtGraphTemplate> originalBunch = model.valuesOf( aSelTemplate );
            IM5BunchEdit<IVtGraphTemplate> copyBunch = new M5BunchEdit<>( model );
            for( IM5FieldDef<IVtGraphTemplate, ?> fd : originalBunch.model().fieldDefs() ) {
              copyBunch.set( fd.id(), originalBunch.get( fd ) );
            }
            ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
            IVtGraphTemplate copyTemplate =
                M5GuiUtils.askCreate( tsContext(), model, copyBunch, cdi, model.getLifecycleManager( null ) );
            if( copyTemplate != null ) {
              // создали копию, обновим список
              refresh();
            }
          }

        };
    // dima 27.06.23 отключаем дерево, автор шаблона непонятно как испарился
    // User2GraphTemplatesTreeMaker treeMaker =
    // new User2GraphTemplatesTreeMaker( conn.coreApi().getService( IVtGraphTemplateService.SERVICE_ID ) );
    //
    // componentModown.tree().setTreeMaker( treeMaker );
    //
    // componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMID_GROUP_BY_USER,
    // IReportsGuiResources.STR_N_BY_USERS, IReportsGuiResources.STR_D_BY_USERS, null, treeMaker ) );
    // componentModown.treeModeManager().setCurrentMode( TMID_GROUP_BY_USER );

    componentModown.addTsSelectionListener( ( aSource, aSelectedItem ) -> {
      componentModown.toolbar().setActionEnabled( ACTID_FORM_GRAPH, aSelectedItem != null );
    } );

    // check ability to edit and tune M5 panels accordingly
    ISkAbility canEdit = conn.coreApi().userService().abilityManager().findAbility( ABILITY_EDIT_TEMPLATES.id() );
    boolean isViewer = false;
    if( !conn.coreApi().userService().abilityManager().isAbilityAllowed( canEdit.id() ) ) {
      isViewer = true;
    }
    graphTemplatesPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, isViewer );

    graphTemplatesPanel.createControl( sf );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }

  /**
   * По шаблону графика и результату запроса к сервису отчетов создает список наборов данных для графической компоненты
   *
   * @param aGraphTemplate {@link IVtGraphTemplate} - шаблон графика
   * @param aReportData - результат запроса к сервису отчетов
   * @param aConnection - соединение с сервером
   * @return - список наборов данных для графика
   */
  public static IList<IG2DataSet> createG2SelfUploDataSetList( IVtGraphTemplate aGraphTemplate,
      IList<ITimedList<?>> aReportData, ISkConnection aConnection ) {
    IListEdit<IG2DataSet> retVal = new ElemArrayList<>();
    IList<IVtGraphParam> graphParams = aGraphTemplate.listParams();
    // создаем нужные наборы данных
    for( int i = 0; i < graphParams.size(); i++ ) {
      IVtGraphParam graphParam = graphParams.get( i );
      String gdsId = ReportTemplateUtilities.graphDataSetId( graphParam );

      G2SelfUploadHistoryDataSetNew dataSet =
          new G2SelfUploadHistoryDataSetNew( aConnection, gdsId, new IDataSetParam() {

            @Override
            public Gwid gwid() {
              return graphParam.gwid();
            }

            @Override
            public String aggrFuncId() {
              return ReportTemplateUtilities.convertFunc( graphParam.aggrFunc() );
            }

            @Override
            public int aggrStep() {
              return (int)aGraphTemplate.aggrStep().timeInMills();
            }

          } );
      retVal.add( dataSet );
      // наполняем его данными
      ITimedList<?> timedList = aReportData.get( i );
      dataSet.setValues( ReportTemplateUtilities.convertList2List( timedList ) );
    }
    return retVal;
  }

  protected void formGraph( IVtGraphTemplate aSelTemplate ) {
    ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );

    doFormGraph( aSelTemplate, conSupp.defConn() );
  }

  protected void doFormGraph( IVtGraphTemplate aSelTemplate, ISkConnection aReportDataConnection ) {

    Shell shell = tsContext().get( Shell.class );
    // запросим у пользователя интервал времени
    TimeInterval retVal = IntervalSelectionDialogPanel.getParams( shell, initValues, tsContext() );
    if( retVal == null ) {
      return;
    }
    // запомним выбранный интервал
    initValues = new TimeInterval( retVal.startTime(), retVal.endTime() );
    // формируем запрос к одноименному сервису
    IStringMap<IDtoQueryParam> queryParams = ReportTemplateUtilities.formQueryParams( aSelTemplate );
    // ISkConnectionSupplier connSupp = tsContext().get( ISkConnectionSupplier.class );

    // Исполнитель запросов в одном потоке
    ITsThreadExecutor threadExecutor = SkThreadExecutorService.getExecutor( aReportDataConnection.coreApi() );
    // Максимальное время выполнения запроса (мсек)
    long timeout = aSelTemplate.maxExecutionTime();
    // Создание диалога прогресса выполнения запроса
    SkAbstractQueryDialog<ISkQueryProcessedData> dialog =
        new SkAbstractQueryDialog<>( getShell(), STR_EXEC_QUERY_REPORT, timeout, threadExecutor ) {

          @Override
          protected ISkQueryProcessedData doCreateQuery( IOptionSetEdit aOptions ) {
            return aReportDataConnection.coreApi().hqService().createProcessedQuery( aOptions );
          }

          @Override
          protected void doPrepareQuery( ISkQueryProcessedData aQuery ) {
            // Подготовка запроса
            aQuery.prepare( queryParams );
            // Настройка обработки результатов запроса
            aQuery.genericChangeEventer().addListener( aSource -> {
              ISkQueryProcessedData q = (ISkQueryProcessedData)aSource;
              if( q.state() == ESkQueryState.READY ) {
                IList<ITimedList<?>> requestAnswer = ReportTemplateUtilities.createResult( aQuery, queryParams );
                IList<IG2DataSet> graphData =
                    createG2SelfUploDataSetList( aSelTemplate, requestAnswer, aReportDataConnection );
                for( IG2DataSet ds : graphData ) {
                  if( ds instanceof G2SelfUploadHistoryDataSetNew ) {
                    ((G2SelfUploadHistoryDataSetNew)ds).addListener( aSource1 -> chartPanel.refresh() );
                  }
                }
                // создаем новую закладку
                CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
                tabItem.setText( aSelTemplate.nmName() );
                chartPanel = new ChartPanel( tabFolder, tsContext() );

                tabItem.setControl( chartPanel );
                tabFolder.setSelection( tabItem );
                // chartPanel.setReportAnswer( graphData, aSelTemplate, true );
                ReportTemplateUtilities.setReportAnswerToChart( chartPanel, graphData, aSelTemplate, true );
                chartPanel.requestLayout();
              }
              if( q.state() == ESkQueryState.FAILED ) {
                String stateMessage = q.stateMessage();
                TsDialogUtils.error( getShell(), ERR_QUERY_FAILED, stateMessage );
              }
            } );

          }
        };
    // Запуск выполнения запроса
    dialog.executeQuery( new QueryInterval( EQueryIntervalType.OSOE, retVal.startTime(), retVal.endTime() ) );
  }
}
