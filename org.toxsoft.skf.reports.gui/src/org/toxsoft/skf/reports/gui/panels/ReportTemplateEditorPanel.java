package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.reports.gui.IReportsGuiConstants.*;
import static org.toxsoft.skf.reports.gui.panels.IReportsGuiResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.jasperreports.gui.main.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
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
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.reports.gui.km5.*;
import org.toxsoft.skf.reports.gui.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.glib.query.*;

/**
 * Панель редактора шаблонов отчетов ts4.<br>
 *
 * @author dima
 */
public class ReportTemplateEditorPanel
    extends TsPanel {

  private static class ReportTemplatePaneComponentModown
      extends MultiPaneComponentModown<IVtReportTemplate> {

    private static final String timestampFormatString  = "dd.MM.yy HH:mm:ss"; //$NON-NLS-1$
    private static final String timestampFormat4TabStr = "dd.MM HH:mm";       //$NON-NLS-1$

    private static final DateFormat timestampFormat     = new SimpleDateFormat( timestampFormatString );
    private static final DateFormat timestampFormat4Tab = new SimpleDateFormat( timestampFormat4TabStr );

    private JasperReportViewer reportV;

    ReportTemplatePaneComponentModown( ITsGuiContext aContext, IM5Model<IVtReportTemplate> aModel,
        IM5ItemsProvider<IVtReportTemplate> aItemsProvider, IM5LifecycleManager<IVtReportTemplate> aLifecycleManager ) {
      super( aContext, aModel, aItemsProvider, aLifecycleManager );

    }

    @Override
    protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
        IListEdit<ITsActionDef> aActs ) {
      aActs.add( ACDEF_SEPARATOR );
      aActs.add( ACDEF_COPY_TEMPLATE );

      if( SHOW_APPLY_BUTTON.getValue( aContext.params() ).asBool() ) {
        aActs.add( ACDEF_SEPARATOR );
        aActs.add( ACDEF_FORM_REPORT );
      }

      ITsToolbar toolbar =

          super.doCreateToolbar( aContext, aName, aIconSize, aActs );

      toolbar.addListener( aActionId -> {
        // nop

      } );

      return toolbar;
    }

    @Override
    protected void doProcessAction( String aActionId ) {
      IVtReportTemplate selTemplate = selectedItem();

      switch( aActionId ) {
        case ACTID_COPY_TEMPLATE:
          copyTemplate( selTemplate );
          break;
        case ACTID_FORM_REPORT:
          formReport( selTemplate );
          break;

        default:
          throw new TsNotAllEnumsUsedRtException( aActionId );
      }
    }

    private void copyTemplate( IVtReportTemplate aSelTemplate ) {
      ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
      ISkConnection conn = connSup.defConn();

      IM5Domain m5 = conn.scope().get( IM5Domain.class );
      IM5Model<IVtReportTemplate> model = m5.getModel( IVtReportTemplate.CLASS_ID, IVtReportTemplate.class );
      IM5Bunch<IVtReportTemplate> originalBunch = model.valuesOf( aSelTemplate );
      IM5BunchEdit<IVtReportTemplate> copyBunch = new M5BunchEdit<>( model );
      for( IM5FieldDef<IVtReportTemplate, ?> fd : originalBunch.model().fieldDefs() ) {
        copyBunch.set( fd.id(), originalBunch.get( fd ) );
      }
      ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
      IVtReportTemplate copyTemplate =
          M5GuiUtils.askCreate( tsContext(), model, copyBunch, cdi, model.getLifecycleManager( null ) );
      if( copyTemplate != null ) {
        // создали копию, обновим список
        refresh();
      }
    }

    private void formReport( IVtReportTemplate aSelTemplate ) {
      Shell shell = tsContext().get( Shell.class );
      // запросим у пользователя интервал времени
      TimeInterval retVal = IntervalSelectionDialogPanel.getParams( shell, initValues, tsContext() );
      if( retVal == null ) {
        return;
      }
      // запомним выбранный интервал
      initValues = new TimeInterval( retVal.startTime(), retVal.endTime() );

      IStringMap<IDtoQueryParam> queryParams = ReportTemplateUtilities.formQueryParams( aSelTemplate );
      ISkConnectionSupplier connSupp = tsContext().get( ISkConnectionSupplier.class );

      // Максимальное время выполнения запроса (мсек)
      long timeout = aSelTemplate.maxExecutionTime();
      try {
        // Создание диалога прогресса выполнения запроса
        SkQueryDialog progressDialog = new SkQueryDialog( getShell(), STR_EXEC_QUERY_REPORT, timeout );
        // fork = true, cancelable = true
        progressDialog.run( true, true, aMonitor -> {
          // Параметры запроса
          IOptionSetEdit options = new OptionSet( OptionSetUtils.createOpSet( //
              ISkHistoryQueryServiceConstants.OP_SK_MAX_EXECUTION_TIME, AvUtils.avInt( timeout ) //
          ) );
          // Формирование запроса
          ISkQueryProcessedData query = connSupp.defConn().coreApi().hqService().createProcessedQuery( options );
          try {
            // Подготовка запроса
            query.prepare( queryParams );
            // Настройка обработки результатов запроса
            IM5Model<IStringMap<IAtomicValue>> resultModel =
                ReportTemplateUtilities.createM5ModelForTemplate( aSelTemplate );
            query.genericChangeEventer().addListener( aSource -> {
              ISkQueryProcessedData q = (ISkQueryProcessedData)aSource;
              if( q.state() == ESkQueryState.READY ) {
                IList<ITimedList<?>> reportData = ReportTemplateUtilities.createResult( query, queryParams );
                IM5ItemsProvider<IStringMap<IAtomicValue>> resultProvider =
                    ReportTemplateUtilities.createM5ItemProviderForTemplate( aSelTemplate, reportData );
                // if( reportV == null ) {
                // reportV = new JasperReportViewer( rightBoard, tsContext() );
                // }

                ITsGuiContext reportContext = new TsGuiContext( tsContext() );
                IJasperReportConstants.REPORT_TITLE_M5_ID.setValue( reportContext.params(),
                    AvUtils.avStr( aSelTemplate.description() ) );

                // IJasperReportConstants.HAS_NUMBER_COLUMN_M5_ID.setValue( reportContext.params(),
                // AvUtils.avBool( false ) );

                // Многострочный подзаголовок отчёта
                IJasperReportConstants.SUBTITLE_STRINGS.setValue( reportContext.params(),
                    AvUtils.avValobj( new StringArrayList( getIntervalTitle( retVal, timestampFormat ) ) ) );

                // веса в процентах первых колонок
                // IJasperReportConstants.COLUMNS_WEIGTHS.setValue( reportContext.params(),
                // AvUtils.avValobj( new IntArrayList( 10, 20, 30 ) ) );

                // Многострочный заголовок страниц
                // IJasperReportConstants.PAGE_HEADER_STRINGS.setValue( reportContext.params(), AvUtils.avValobj(
                // new StringArrayList( "Заголовок страницы 1", "Заголовок страницы 2", "Заголовок страницы 3" ) ) );

                // выясняем текущего пользователя
                ISkConnection conn = connSupp.defConn();
                ISkUser user = ConnectionUtiles.getConnectedUser( conn.coreApi() );
                String userName = user.nmName().trim().length() > 0 ? user.nmName() : user.login();

                IJasperReportConstants.LEFT_BOTTOM_STR_M5_ID.setValue( reportContext.params(),
                    AvUtils.avStr( AUTHOR_STR + userName ) );
                IJasperReportConstants.RIGHT_BOTTOM_STR_M5_ID.setValue( reportContext.params(),
                    AvUtils.avStr( DATE_STR + timestampFormat.format( new Date() ) ) );

                // reportV.setJasperReportPrint( reportContext, resultModel, resultProvider );
                // создаем новую закладку
                CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
                tabItem.setText( aSelTemplate.nmName() + getIntervalTitle( retVal, timestampFormat4Tab ) );
                reportV = new JasperReportViewer( tabFolder, tsContext() );
                tabItem.setControl( reportV );

                tabFolder.setSelection( tabItem );
                reportV.setJasperReportPrint( reportContext, resultModel, resultProvider );
                reportV.requestLayout();

              }
              if( q.state() == ESkQueryState.FAILED ) {
                String stateMessage = q.stateMessage();
                TsDialogUtils.error( getShell(), ERR_QUERY_FAILED, stateMessage );
              }
            } );
            // Интервал запроса
            IQueryInterval interval =
                new QueryInterval( EQueryIntervalType.OSOE, retVal.startTime(), retVal.endTime() );
            // Выполнение запроса
            SkQueryUtils.execQueryWithProgress( query, interval, aMonitor, progressDialog );
          }
          finally {
            query.close();
          }
        } );
      }
      catch( InvocationTargetException | InterruptedException ex ) {
        LoggerUtils.errorLogger().error( ex );
      }
    }

    @SuppressWarnings( { "nls", "boxing" } )
    private static String getIntervalTitle( TimeInterval aInrvl, DateFormat aDateFormat ) {
      // формируем строку интервала времени
      return " [" + aDateFormat.format( aInrvl.startTime() ) + " - " + aDateFormat.format( aInrvl.endTime() ) + "]";
    }

  }

  final static String ACTID_FORM_REPORT = SK_ID + ".users.gui.RunReportForm"; //$NON-NLS-1$

  final static TsActionDef ACDEF_FORM_REPORT =
      TsActionDef.ofPush2( ACTID_FORM_REPORT, STR_N_GENERATE_REPORT, STR_D_GENERATE_REPORT, ICONID_RUN );

  final static String ACTID_COPY_TEMPLATE = SK_ID + ".users.gui.CopyTemplate"; //$NON-NLS-1$

  final static TsActionDef ACDEF_COPY_TEMPLATE = TsActionDef.ofPush2( ACTID_COPY_TEMPLATE, STR_N_COPY_TEMPLATE,
      STR_D_COPY_TEMPLATE, ITsStdIconIds.ICONID_EDIT_COPY );

  static TimeInterval initValues =
      new TimeInterval( System.currentTimeMillis() - 24L * 60L * 60L * 1000L, System.currentTimeMillis() );

  final ISkConnection                   conn;
  IM5CollectionPanel<IVtReportTemplate> reportTemplatesPanel;

  // private TsComposite rightBoard;
  private static CTabFolder tabFolder;

  /**
   * лист шаблона
   */
  final ITsNodeKind<IVtReportTemplate> NK_TEMPLATE_LEAF =
      new TsNodeKind<>( "LeafTemplate", IVtReportTemplate.class, false, ICONID_REPORT_TEMPLATE ); //$NON-NLS-1$

  /**
   * узел пользователя
   */
  final ITsNodeKind<ISkUser> NK_USER_NODE = new TsNodeKind<>( "NodeUser", ISkUser.class, true, ICONID_USER ); //$NON-NLS-1$

  /**
   * панель отображения дерева шаблонов
   */
  private ReportTemplatePaneComponentModown componentModown;

  static final String TMIID_GROUP_BY_USER = "GroupByUser"; //$NON-NLS-1$

  /**
   * Создатель дерева пользователи-отчеты
   *
   * @author dima
   */
  private class User2ReportTemplatesTreeMaker
      implements ITsTreeMaker<IVtReportTemplate> {

    private final IVtReportTemplateService service;

    User2ReportTemplatesTreeMaker( IVtReportTemplateService aService ) {
      service = aService;
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return aNode.kind() == NK_TEMPLATE_LEAF;
    }

    private IStringMapEdit<DefaultTsNode<ISkUser>> makeUser2TemplatesTypesMap( ITsNode aRootNode ) {
      IStringMapEdit<DefaultTsNode<ISkUser>> retVal = new StringMap<>();
      // получаем все шаблоны системы и строим узлы дерева
      IList<IVtReportTemplate> templates = service.listReportTemplates();
      for( IVtReportTemplate template : templates ) {
        if( !retVal.hasKey( template.author().id() ) ) {
          DefaultTsNode<ISkUser> authorNode = new DefaultTsNode<>( NK_USER_NODE, aRootNode, template.author() );
          // присвоим красивую иконку и нормальное имя
          authorNode.setName( template.author().attrs().getStr( IM5Constants.FID_NAME ) );
          retVal.put( template.author().id(), authorNode );
        }
      }
      return retVal;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<IVtReportTemplate> aTemplates ) {
      IStringMapEdit<DefaultTsNode<ISkUser>> roots = makeUser2TemplatesTypesMap( aRootNode );
      for( IVtReportTemplate template : aTemplates ) {
        DefaultTsNode<ISkUser> userNode = roots.findByKey( template.author().id() );
        DefaultTsNode<IVtReportTemplate> templateLeaf = new DefaultTsNode<>( NK_TEMPLATE_LEAF, userNode, template );
        // присвоим красивую иконку и нормальное имя
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
  public ReportTemplateEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtReportTemplate> model = m5.getModel( IVtReportTemplate.CLASS_ID, IVtReportTemplate.class );

    IM5LifecycleManager<IVtReportTemplate> lm = new ReportTemplateM5LifecycleManager( model, conn );
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

    // dima 03.11.22 вынес в отдельный класс
    componentModown = new ReportTemplatePaneComponentModown( ctx, model, lm.itemsProvider(), lm );

    // дерево пользователи -> их шаблоны
    User2ReportTemplatesTreeMaker treeMaker =
        new User2ReportTemplatesTreeMaker( conn.coreApi().getService( IVtReportTemplateService.SERVICE_ID ) );

    componentModown.tree().setTreeMaker( treeMaker );

    componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMIID_GROUP_BY_USER, IReportsGuiResources.STR_N_BY_USERS,
        IReportsGuiResources.STR_D_BY_USERS, null, treeMaker ) );
    componentModown.treeModeManager().setCurrentMode( TMIID_GROUP_BY_USER );

    reportTemplatesPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    reportTemplatesPanel.createControl( sf );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }
}
