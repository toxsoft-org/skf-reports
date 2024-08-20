package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.reports.gui.IReportsGuiConstants.*;
import static org.toxsoft.skf.reports.gui.panels.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.io.*;
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
import org.toxsoft.core.tsgui.panels.opsedit.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.temporal.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.*;
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
import org.toxsoft.uskat.core.impl.*;

import net.sf.jasperreports.engine.*;

/**
 * Панель редактора специфичных шаблонов отчетов ts4.<br>
 *
 * @author max
 */
public class SpecReportTemplateEditorPanel
    extends TsPanel {

  private class SpecReportTemplatePaneComponentModown
      extends MultiPaneComponentModown<IVtSpecReportTemplate> {

    SpecReportTemplatePaneComponentModown( ITsGuiContext aContext, IM5Model<IVtSpecReportTemplate> aModel,
        IM5ItemsProvider<IVtSpecReportTemplate> aItemsProvider,
        IM5LifecycleManager<IVtSpecReportTemplate> aLifecycleManager ) {
      super( aContext, aModel, aItemsProvider, aLifecycleManager );

    }

    @Override
    protected IVtSpecReportTemplate doAddItem() {

      // tsContext().put( "jr.test", "TestJrXml" );

      return super.doAddItem();
    }

    @Override
    protected IVtSpecReportTemplate doEditItem( IVtSpecReportTemplate aItem ) {

      tsContext().put( "jr.test", aItem.design() );

      return super.doEditItem( aItem );
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
      IVtSpecReportTemplate selTemplate = selectedItem();

      switch( aActionId ) {
        case ACTID_COPY_TEMPLATE:
          // copyTemplate( selTemplate );
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
      ISkConnection connection = connSup.defConn();

      IM5Domain m5 = connection.scope().get( IM5Domain.class );
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

  }

  final static String ACTID_FORM_REPORT = SK_ID + ".users.gui.RunReportForm"; //$NON-NLS-1$

  final static TsActionDef ACDEF_FORM_REPORT =
      TsActionDef.ofPush2( ACTID_FORM_REPORT, STR_N_GENERATE_REPORT, STR_D_GENERATE_REPORT, ICONID_RUN );

  final static String ACTID_COPY_TEMPLATE = SK_ID + ".users.gui.CopyTemplate"; //$NON-NLS-1$

  final static TsActionDef ACDEF_COPY_TEMPLATE = TsActionDef.ofPush2( ACTID_COPY_TEMPLATE, STR_N_COPY_TEMPLATE,
      STR_D_COPY_TEMPLATE, ITsStdIconIds.ICONID_EDIT_COPY );

  static TimeInterval initValues =
      new TimeInterval( System.currentTimeMillis() - 24L * 60L * 60L * 1000L, System.currentTimeMillis() );

  IM5CollectionPanel<IVtSpecReportTemplate> reportTemplatesPanel;

  private CTabFolder tabFolder;

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
  private SpecReportTemplatePaneComponentModown componentModown;

  static final String TMIID_GROUP_BY_USER = "GroupByUser"; //$NON-NLS-1$

  private static final String timestampFormatString  = "dd.MM.yy HH:mm:ss"; //$NON-NLS-1$
  private static final String timestampFormat4TabStr = "dd.MM HH:mm";       //$NON-NLS-1$

  private static final DateFormat timestampFormat     = new SimpleDateFormat( timestampFormatString );
  private static final DateFormat timestampFormat4Tab = new SimpleDateFormat( timestampFormat4TabStr );

  private JasperReportViewer reportV;

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
  public SpecReportTemplateEditorPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtSpecReportTemplate> model = m5.getModel( IVtSpecReportTemplate.CLASS_ID, IVtSpecReportTemplate.class );

    IM5LifecycleManager<IVtSpecReportTemplate> lm = new SpecReportTemplateM5LifecycleManager( model, aContext );
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
    componentModown = new SpecReportTemplatePaneComponentModown( ctx, model, lm.itemsProvider(), lm );

    // dima 27.06.23 отключаем дерево, автор шаблона непонятно как испарился
    // дерево пользователи -> их шаблоны
    // User2ReportTemplatesTreeMaker treeMaker =
    // new User2ReportTemplatesTreeMaker( conn.coreApi().getService( IVtReportTemplateService.SERVICE_ID ) );
    //
    // componentModown.tree().setTreeMaker( treeMaker );
    //
    // componentModown.treeModeManager().addTreeMode( new TreeModeInfo<>( TMIID_GROUP_BY_USER,
    // IReportsGuiResources.STR_N_BY_USERS, IReportsGuiResources.STR_D_BY_USERS, null, treeMaker ) );
    // componentModown.treeModeManager().setCurrentMode( TMIID_GROUP_BY_USER );

    reportTemplatesPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    reportTemplatesPanel.createControl( sf );

    tabFolder = new CTabFolder( sf, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );

    sf.setWeights( 300, 500 );

  }

  protected void formReport( IVtSpecReportTemplate aSelTemplate ) {
    ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );

    doFormReport( aSelTemplate, conSupp.defConn() );
  }

  protected void doFormReport( IVtSpecReportTemplate aSelTemplate, ISkConnection aReportDataConnection ) {
    Shell shell = tsContext().get( Shell.class );
    // запросим у пользователя интервал времени
    TimeInterval retVal = IntervalSelectionExtandedDialogPanel.getParams( shell, initValues, tsContext() );
    if( retVal == null ) {
      return;
    }
    // запомним выбранный интервал
    initValues = new TimeInterval( retVal.startTime(), retVal.endTime() );

    if( true ) {
      testForm( aSelTemplate, initValues );
      return;
    }

    IStringMap<IDtoQueryParam> queryParams = new StringMap<>();// ReportTemplateUtilities.formQueryParams( aSelTemplate
                                                               // );

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
              LoggerUtils.defaultLogger().info( "State %s , %s", q.toString(), q.state().nmName() ); //$NON-NLS-1$
              if( q.state() == ESkQueryState.READY ) {
                // разделить все параметры на три группы
                // JR fields - поля - колонки таблицы - функции от времени (это классические параметры обычного отчёта)
                // для них обязательно наличие gwid и поля $F{...}
                IListEdit<IVtSpecReportParam> fieldParams = new ElemArrayList<>();

                // JR params - параметры - вычисляемые значения - условно одно значение на отчёт
                // для них обязательно наличие gwid и параметра $P{...}
                IListEdit<IVtSpecReportParam> calcParams = new ElemArrayList<>();

                // JR params - параметры - значение которых не вычисляется, а указывается либо на этапе создания
                // шаблона,
                // либо на момент формирования отчёта
                // для них обязательно наличие только параметра $P{...}
                // TODO подумать над указанием gwid типа атрибута или спец значений (юзер и т.д.)
                IListEdit<IVtSpecReportParam> valParams = new ElemArrayList<>();

                IList<IVtSpecReportParam> params = aSelTemplate.listParams();
                for( IVtSpecReportParam p : params ) {
                  if( p.jrParamId().startsWith( ReportTemplateUtilities.JR_PARAM_FIELD_PREFIX ) ) {
                    fieldParams.add( p );
                  }
                  else {
                    if( p.gwid() != null
                        && p.gwid().canonicalString().contains( IGwHardConstants.GW_KEYWORD_RTDATA ) ) {
                      calcParams.add( p );
                    }
                    else {
                      valParams.add( p );
                    }
                  }
                }

                // заполнить значения по умолчанию из значений в параметрах valParams
                IOptionSetEdit initVales = new OptionSet();
                for( IVtSpecReportParam valP : valParams ) {
                  if( valP.canBeOverwritten() ) {
                    initVales.put( ReportTemplateUtilities.getPureJrParamIdFromTemplateJrParamId( valP.jrParamId() ),
                        avStr( valP.value() ) );
                  }
                }

                // после в диалоге - задать и вставить параметры в valParams
                IOptionSet editedValSet = new OptionSet();

                if( initVales.size() > 0 ) {
                  TsDialogInfo dlgInfo =
                      new TsDialogInfo( tsContext(), "Задаваемые параметры отчёта", "Задайте значения параметров" );
                  editedValSet =
                      DialogOptionsEdit.editOpset( dlgInfo, TsGuiUtils.prepareDefaultDefs( initVales ), initVales );
                }

                // для полей - получить стандартные параметры запроса
                IStringMap<IDtoQueryParam> fieldQueryParams = ReportTemplateUtilities.template2Query( fieldParams,
                    Integer.valueOf( (int)aSelTemplate.aggrStep().timeInMills() ) );

                // для вычисляемых параметров - два путиЖ \\
                // - получить параметры запроса с шагом аггрегации как у всего отчёта (тогда вручную суммировать
                // результат) \\
                // - получить параметры запроса с шагом аггрегации равным интервалу запроса (т.е. будет запрошено одно
                // значение на
                // параметр)
                IStringMap<IDtoQueryParam> calcQueryParams = ReportTemplateUtilities.template2Query( "calc", calcParams,
                    Integer.valueOf( (int)aSelTemplate.aggrStep().timeInMills() ) );

                // для параметров с задаваемыми значениями - запросов не требуется

                IStringMapEdit<IDtoQueryParam> allRequestParams = new StringMap<>();

                allRequestParams.putAll( calcQueryParams );
                allRequestParams.putAll( fieldQueryParams );

                // получение значний для параметров запроса
                IList<ITimedList<?>> reportData = ReportTemplateUtilities.createResult( aQuery, allRequestParams );

                IListEdit<ITimedList<?>> fieldData = new ElemArrayList<>();
                for( int i = 0; i < fieldQueryParams.size(); i++ ) {
                  fieldData.add( reportData.get( i + fieldQueryParams.size() ) );
                }

                ITsGuiContext reportContext = new TsGuiContext( tsContext() );

                // выясняем текущего пользователя
                // ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );
                // ISkConnection connectionForUser = conSupp.defConn();
                // ISkUser user = ConnectionUtiles.getConnectedUser( connectionForUser.coreApi() );
                // String userName = user.nmName().trim().length() > 0 ? user.nmName() : user.login();

                // создаем новую закладку
                CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
                tabItem.setText( aSelTemplate.nmName() + ", getIntervalTitle( retVal, timestampFormat4Tab )" );
                reportV = new JasperReportViewer( tabFolder, tsContext() );
                tabItem.setControl( reportV );

                tabFolder.setSelection( tabItem );

                Map<String, Object> reportParameters = calcParametorsValues( calcParams, reportData );

                for( IVtSpecReportParam valP : valParams ) {
                  if( !valP.canBeOverwritten() ) {
                    reportParameters.put(
                        ReportTemplateUtilities.getPureJrParamIdFromTemplateJrParamId( valP.jrParamId() ),
                        valP.value() );
                  }
                }

                // добавляем параметры из диалога
                for( String paramKey : editedValSet.keys() ) {
                  reportParameters.put( paramKey, editedValSet.getStr( paramKey ) );
                }

                JRDataSource dataSource = ReportTemplateUtilities.createReportDetailDataSource( fieldParams, fieldData,
                    aSelTemplate.hasSummary() );

                String design = aSelTemplate.design();

                if( design.length() > 0 ) {
                  try( InputStream stream = new ByteArrayInputStream( design.getBytes() ) ) {
                    JasperReport jasperReport = JasperCompileManager.compileReport( stream );

                    JasperPrint jasperPrint =
                        JasperFillManager.fillReport( jasperReport, reportParameters, dataSource );

                    reportV.displeyJasperReportPrint( reportContext, jasperPrint );

                    reportV.requestLayout();
                  }
                  catch( Exception ee ) {
                    ee.printStackTrace();
                  }
                }

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

  private void testForm( IVtSpecReportTemplate aSelTemplate, TimeInterval aInterval ) {

    // разделить все параметры на три группы
    // JR fields - поля - колонки таблицы - функции от времени (это классические параметры обычного отчёта)
    // для них обязательно наличие gwid и поля $F{...}
    IListEdit<IVtSpecReportParam> fieldParams = new ElemArrayList<>();

    // JR params - параметры - вычисляемые значения - условно одно значение на отчёт
    // для них обязательно наличие gwid и параметра $P{...}
    IListEdit<IVtSpecReportParam> calcParams = new ElemArrayList<>();

    // JR params - параметры - значение которых не вычисляется, а указывается либо на этапе создания шаблона,
    // либо на момент формирования отчёта
    // для них обязательно наличие только параметра $P{...}
    // TODO подумать над указанием gwid типа атрибута или спец значений (юзер и т.д.)
    IListEdit<IVtSpecReportParam> valParams = new ElemArrayList<>();

    IList<IVtSpecReportParam> params = aSelTemplate.listParams();
    for( IVtSpecReportParam p : params ) {
      if( p.jrParamId().startsWith( ReportTemplateUtilities.JR_PARAM_FIELD_PREFIX ) ) {
        fieldParams.add( p );
      }
      else {
        if( p.gwid() != null && p.gwid().canonicalString().contains( IGwHardConstants.GW_KEYWORD_RTDATA ) ) {
          calcParams.add( p );
        }
        else {
          valParams.add( p );
        }
      }
    }

    // заполнить значения по умолчанию из значений в параметрах valParams
    IOptionSetEdit initVales = new OptionSet();
    for( IVtSpecReportParam valP : valParams ) {
      if( valP.canBeOverwritten() ) {
        initVales.put( ReportTemplateUtilities.getPureJrParamIdFromTemplateJrParamId( valP.jrParamId() ),
            avStr( valP.value() ) );
      }
    }

    // после в диалоге - задать и вставить параметры в valParams
    IOptionSet editedValSet = new OptionSet();

    if( initVales.size() > 0 ) {
      TsDialogInfo dlgInfo =
          new TsDialogInfo( tsContext(), "Задаваемые параметры отчёта", "Задайте значения параметров" );
      editedValSet = DialogOptionsEdit.editOpset( dlgInfo, TsGuiUtils.prepareDefaultDefs( initVales ), initVales );
    }

    // для полей - получить стандартные параметры запроса
    IStringMap<IDtoQueryParam> fieldQueryParams = ReportTemplateUtilities.template2Query( fieldParams,
        Integer.valueOf( (int)aSelTemplate.aggrStep().timeInMills() ) );

    // для вычисляемых параметров - два путиЖ \\
    // - получить параметры запроса с шагом аггрегации как у всего отчёта (тогда вручную суммировать результат) \\
    // - получить параметры запроса с шагом аггрегации равным интервалу запроса (т.е. будет запрошено одно значение на
    // параметр)
    IStringMap<IDtoQueryParam> calcQueryParams = ReportTemplateUtilities.template2Query( "calc", calcParams,
        Integer.valueOf( (int)aSelTemplate.aggrStep().timeInMills() ) );

    // для параметров с задаваемыми значениями - запросов не требуется

    IStringMapEdit<IDtoQueryParam> allRequestParams = new StringMap<>();

    allRequestParams.putAll( calcQueryParams );
    allRequestParams.putAll( fieldQueryParams );

    // получение значний для параметров запроса
    IList<ITimedList<?>> reportData = ReportTemplateUtilities.createTestResult( allRequestParams );

    IListEdit<ITimedList<?>> fieldData = new ElemArrayList<>();
    for( int i = 0; i < fieldQueryParams.size(); i++ ) {
      fieldData.add( reportData.get( i + fieldQueryParams.size() ) );
    }

    ITsGuiContext reportContext = new TsGuiContext( tsContext() );

    // выясняем текущего пользователя
    // ISkConnectionSupplier conSupp = tsContext().get( ISkConnectionSupplier.class );
    // ISkConnection connectionForUser = conSupp.defConn();
    // ISkUser user = ConnectionUtiles.getConnectedUser( connectionForUser.coreApi() );
    // String userName = user.nmName().trim().length() > 0 ? user.nmName() : user.login();

    // создаем новую закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    tabItem.setText( aSelTemplate.nmName() + getIntervalTitle( aInterval, timestampFormat4Tab ) );
    reportV = new JasperReportViewer( tabFolder, tsContext() );
    tabItem.setControl( reportV );

    tabFolder.setSelection( tabItem );

    Map<String, Object> reportParameters = calcParametorsValues( calcParams, reportData );

    for( IVtSpecReportParam valP : valParams ) {
      if( !valP.canBeOverwritten() ) {
        reportParameters.put( ReportTemplateUtilities.getPureJrParamIdFromTemplateJrParamId( valP.jrParamId() ),
            valP.value() );
      }
    }

    // добавляем параметры из диалога
    for( String paramKey : editedValSet.keys() ) {
      reportParameters.put( paramKey, editedValSet.getStr( paramKey ) );
    }

    JRDataSource dataSource =
        ReportTemplateUtilities.createReportDetailDataSource( fieldParams, fieldData, aSelTemplate.hasSummary() );

    String design = aSelTemplate.design();

    if( design.length() > 0 ) {
      try( InputStream stream = new ByteArrayInputStream( design.getBytes() ) ) {
        JasperReport jasperReport = JasperCompileManager.compileReport( stream );

        JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, reportParameters, dataSource );

        reportV.displeyJasperReportPrint( reportContext, jasperPrint );

        reportV.requestLayout();
      }
      catch( Exception ee ) {
        ee.printStackTrace();
      }
    }

  }

  @SuppressWarnings( { "nls", "boxing" } )
  private static String getIntervalTitle( TimeInterval aInrvl, DateFormat aDateFormat ) {
    // формируем строку интервала времени
    return " [" + aDateFormat.format( aInrvl.startTime() ) + " - " + aDateFormat.format( aInrvl.endTime() ) + "]";
  }

  private static Map<String, Object> calcParametorsValues( IList<IVtSpecReportParam> aCalcParams,
      IList<ITimedList<?>> aReportData ) {
    Map<String, Object> result = new HashMap<>();

    for( int i = 0; i < aCalcParams.size(); i++ ) {
      IVtSpecReportParam param = aCalcParams.get( i );

      EDisplayFormat displayFormat = param.displayFormat();
      IAggrigationFunction aggrFunc = ReportTemplateUtilities.createAggrigationFunction( param.aggrFunc() );
      // s.get( i ).nextValue( val.value() );
      ITimedList<?> timedList = aReportData.get( i );

      for( int j = 0; j < timedList.size(); j++ ) {
        ITemporalAtomicValue val = (TemporalAtomicValue)timedList.get( j );

        aggrFunc.nextValue( val.value() );
      }
      result.put( ReportTemplateUtilities.getPureJrParamIdFromTemplateJrParamId( param.jrParamId() ),
          ReportTemplateUtilities.convertValueToView( aggrFunc.getCurrentResult(), displayFormat ) );
    }

    return result;
  }

}
