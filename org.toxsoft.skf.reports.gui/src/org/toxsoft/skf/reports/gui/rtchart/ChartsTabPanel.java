package org.toxsoft.skf.reports.gui.rtchart;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.bricks.actions.TsActionDef.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.reports.gui.rtchart.ISkResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.ggprefs.lib.*;
import org.toxsoft.skf.ggprefs.lib.impl.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Панель отображения графиков реального времени.<br>
 *
 * @author dima
 */
public class ChartsTabPanel
    extends TsPanel {

  /**
   * признак того что панель сверху
   */
  private final boolean top;
  // private final TsToolbar toolbar;
  private CTabFolder tabFolder;

  private IGuiGwPrefsSection prefSection;

  private final ISkConnection conn;
  /*
   * Skid пользователя настройки которого используются в данном сеансе
   */
  private final Skid userSkid4GwPrefs;
  /*
   * Gwid пользователя настройки которого используются в данном сеансе
   */
  private final Gwid userGwid4GwPrefs;
  SkidList           rtChartSkids = new SkidList();

  /**
   * Action: open template.
   */
  ITsActionDef ACDEF_OPEN_TEMPLATE = ofPush2( ACTID_OPEN, //
      STR_T_OPEN_TEMPLATE, STR_OPEN_TEMPLATE_D, ICONID_DOCUMENT_OPEN );

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aConnection {@link ISkConnection} - соединение с сервером
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @param aCurrUser {@link ISkUser } - текущий пользователь настрйки которого актуализируются
   * @param isTop признак того что панель сверху
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public ChartsTabPanel( Composite aParent, ISkConnection aConnection, ITsGuiContext aContext, ISkUser aCurrUser,
      boolean isTop ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    conn = aConnection;
    top = isTop;
    userSkid4GwPrefs = aCurrUser.skid();
    userGwid4GwPrefs = Gwid.createObj( userSkid4GwPrefs );

    // инициализируем панель быстрых кнопок
    initToolBar();
    // инициализируем настройки панели
    initPanelPrefs();
    // восстанавливаем внешний вид панели
    restoreUserSettings();
  }

  private void initToolBar() {
    tabFolder = new CTabFolder( this, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );
    tabFolder.setLayoutData( BorderLayout.CENTER );
    ToolBar toolBar = new ToolBar( tabFolder, SWT.FLAT );
    tabFolder.setTopRight( toolBar, SWT.RIGHT );
    // open graph template
    ToolItem openTemplate = new ToolItem( toolBar, SWT.PUSH );
    Image tbImage = iconManager().loadStdIcon( ITsStdIconIds.ICONID_DOCUMENT_OPEN, EIconSize.IS_16X16 );
    openTemplate.setImage( tbImage );
    openTemplate.setToolTipText( STR_OPEN_TEMPLATE_D );
    openTemplate.addSelectionListener( new SelectionListener() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        extracted();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent aE ) {
        extracted();
      }

      private void extracted() {
        IVtGraphTemplate selectedTemplate = doSelectTemplate();
        addRtChartTemplate( selectedTemplate );
      }

    } );

    // add new graph template
    ToolItem addNewTemplate = new ToolItem( toolBar, SWT.PUSH );
    tbImage =

        iconManager().loadStdIcon( ITsStdIconIds.ICONID_LIST_ADD, EIconSize.IS_16X16 );
    addNewTemplate.setImage( tbImage );
    addNewTemplate.setToolTipText( STR_ADD_TEMPLATE_D );
    addNewTemplate.addSelectionListener( new SelectionListener() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        extracted();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent aE ) {
        extracted();
      }

      private void extracted() {
        IVtGraphTemplate newRtGraphTemplate = doAddTemplate();
        addRtChartTemplate( newRtGraphTemplate );
      }
    } );
    // edit graph template
    ToolItem editTemplate = new ToolItem( toolBar, SWT.PUSH );
    tbImage = iconManager().loadStdIcon( ITsStdIconIds.ICONID_DOCUMENT_EDIT, EIconSize.IS_16X16 );
    editTemplate.setImage( tbImage );
    editTemplate.setToolTipText( STR_EDIT_TEMPLATE_D );
    editTemplate.addSelectionListener( new SelectionListener() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        extracted();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent aE ) {
        extracted();
      }

      private void extracted() {
        // получаем текущий график
        CTabItem selTab = tabFolder.getSelection();
        IVtGraphTemplate selGraphTemplate = (IVtGraphTemplate)selTab.getData();
        IVtGraphTemplate newRtGraphTemplate = doEditTemplate( selGraphTemplate );
        if( newRtGraphTemplate != null ) {
          rtChartSkids.remove( selGraphTemplate.skid() );
          // гасим RtChart
          RtChartPanel chartPanel = (RtChartPanel)selTab.getControl();
          chartPanel.dispose();
          selTab.dispose();
          addRtChartTemplate( newRtGraphTemplate );
        }
      }
    } );
    // listen close tab event
    tabFolder.addCTabFolder2Listener( new CTabFolder2Adapter() {

      @Override
      public void close( CTabFolderEvent event ) {
        // ask user to continue
        if( TsDialogUtils.askYesNoCancel( getShell(), STR_CONFIRM_REMOVE_TEMPLATE ) == ETsDialogCode.YES ) {
          // получаем текущий график
          CTabItem selTab = (CTabItem)event.widget;
          IVtGraphTemplate selGraphTemplate = (IVtGraphTemplate)selTab.getData();
          rtChartSkids.remove( selGraphTemplate.skid() );
          // гасим RtChart
          RtChartPanel chartPanel = (RtChartPanel)selTab.getControl();
          chartPanel.dispose();
          selTab.dispose();
          saveUserSettings();
          event.doit = true; // allow close
        }
        else {
          event.doit = false; // disallow close
        }
      }
    } );

    // !!!!
    // Need to set height of tab to show toolbar
    tabFolder.setTabHeight( Math.max( toolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y, tabFolder.getTabHeight() ) );
  }

  private IVtGraphTemplate doEditTemplate( IVtGraphTemplate aSelGraphTemplate ) {
    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtGraphTemplate> model = m5.getModel( IVtGraphTemplate.CLASS_ID, IVtGraphTemplate.class );
    ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
    return M5GuiUtils.askEdit( tsContext(), model, aSelGraphTemplate, cdi, model.getLifecycleManager( null ) );
  }

  private void addRtChartTemplate( IVtGraphTemplate aRtGraphTemplate ) {
    if( aRtGraphTemplate != null ) {
      addRtChartTemplateToTabPanel( aRtGraphTemplate );
      rtChartSkids.add( aRtGraphTemplate.skid() );
      saveUserSettings();
    }
  }

  private void addRtChartTemplateToTabPanel( IVtGraphTemplate aRtGraphTemplate ) {
    // создаем новую закладку
    CTabItem tabItem = new CTabItem( tabFolder, SWT.CLOSE );
    // закладке дадим имя параметра
    tabItem.setText( aRtGraphTemplate.nmName() );
    RtChartPanel chartPanel = new RtChartPanel( tabFolder, tsContext(), aRtGraphTemplate, conn );
    tabItem.setControl( chartPanel );
    tabFolder.setSelection( tabItem );
    tabItem.setData( aRtGraphTemplate );
  }

  private void restoreUserSettings() {
    // получаем список графиков и создаем для каждого свой RtChart
    IOptionSet userPrefs = getUserPrefs();
    rtChartSkids = RtChartPanelOptions.RTCHART_SKIDS.getValue( userPrefs ).asValobj();
    for( Skid rtChartTemplateSkid : rtChartSkids ) {
      if( conn.coreApi().objService().find( rtChartTemplateSkid ) != null ) {
        IVtGraphTemplate graphTemplate = conn.coreApi().objService().get( rtChartTemplateSkid );
        addRtChartTemplateToTabPanel( graphTemplate );
      }
    }
  }

  private void saveUserSettings() {
    IOptionSetEdit userPrefs = new OptionSet( getUserPrefs() );
    RtChartPanelOptions.RTCHART_SKIDS.setValue( userPrefs, AvUtils.avValobj( rtChartSkids ) );
    prefSection.setOptions( userSkid4GwPrefs, userPrefs );
  }

  private void initPanelPrefs() {
    ISkCoreApi coreApi = conn.coreApi();

    if( !coreApi.services().hasKey( ISkGuiGwPrefsService.SERVICE_ID ) ) {
      coreApi.addService( SkGuiGwPrefsService.CREATOR );
    }
    prefSection = PrefUtils
        .section( top ? PrefUtils.TOP_RTCHARTS_PREFS_SECTION_ID : PrefUtils.BOTTOM_RTCHARTS_PREFS_SECTION_ID, conn );

    // Задание опций
    IStridablesListEdit<IDataDef> panelPrefs = new StridablesList<>();
    panelPrefs.addAll( RtChartPanelOptions.allOptions() );

    IList<IDataDef> currOpDefs = prefSection.listOptionDefs( userSkid4GwPrefs );
    IStridablesListEdit<IDataDef> newPrefDefs = new StridablesList<>();
    // перебираем все устанавливаемые опции и добавляем только новые
    for( IDataDef addingOptDef : RtChartPanelOptions.allOptions() ) {
      if( !PrefUtils.hasOptionDef( currOpDefs, addingOptDef ) ) {
        newPrefDefs.add( addingOptDef );
      }
    }
    prefSection.bindOptions( userGwid4GwPrefs, newPrefDefs );
  }

  private IOptionSet getUserPrefs() {
    return prefSection.getOptions( userSkid4GwPrefs );
  }

  protected IVtGraphTemplate doAddTemplate() {
    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtGraphTemplate> model = m5.getModel( IVtGraphTemplate.CLASS_ID, IVtGraphTemplate.class );
    ITsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
    IM5BunchEdit<IVtGraphTemplate> initVals = new M5BunchEdit<>( model );
    return M5GuiUtils.askCreate( tsContext(), model, initVals, cdi, model.getLifecycleManager( null ) );
  }

  protected IVtGraphTemplate doSelectTemplate() {
    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<IVtGraphTemplate> model = m5.getModel( IVtGraphTemplate.CLASS_ID, IVtGraphTemplate.class );
    TsDialogInfo cdi = TsDialogInfo.forCreateEntity( tsContext() );
    cdi.setMinSizeShellRelative( 60, 90 );
    IM5LifecycleManager<IVtGraphTemplate> lm = model.getLifecycleManager( null );
    return M5GuiUtils.askSelectItem( cdi, model, null, lm.itemsProvider(), lm );
  }

}
