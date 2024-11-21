package org.toxsoft.skf.reports.gui.rtchart;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.bricks.actions.TsActionDef.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.reports.gui.rtchart.ISkResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
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
  private final boolean   top;
  private final TsToolbar toolbar;
  private CTabFolder      tabFolder;

  private IGuiGwPrefsSection prefSection;

  private final ISkConnection conn;
  private final Skid          masteObjectSkid;
  private final Gwid          masteObjectGwid;
  SkidList                    rtChartSkids = new SkidList();

  /**
   * Action: open template.
   */
  ITsActionDef ACDEF_OPEN_TEMPLATE = ofPush2( ACTID_OPEN, //
      STR_T_OPEN_TEMPLATE, STR_D_OPEN_TEMPLATE, ICONID_DOCUMENT_OPEN );

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aConnection {@link ISkConnection} - соединение с сервером
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @param aMasteObjectSkid {@link Skid } - мастер объект панели
   * @param isTop признак того что панель сверху
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public ChartsTabPanel( Composite aParent, ISkConnection aConnection, ITsGuiContext aContext, Skid aMasteObjectSkid,
      boolean isTop ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    conn = aConnection;
    top = isTop;
    masteObjectSkid = aMasteObjectSkid;
    masteObjectGwid = Gwid.createClass( masteObjectSkid.classId() );

    // toolbar
    toolbar = new TsToolbar( tsContext() );
    // toolbar.setNameLabelText( RTCHARTS_TOOLBAR_TITLE );
    toolbar.addActionDefs( //
        ACDEF_OPEN_TEMPLATE, ACDEF_ADD, ACDEF_EDIT, ACDEF_REMOVE //
    );
    toolbar.setIconSize( EIconSize.IS_16X16 );
    toolbar.createControl( this );
    toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    toolbar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_OPEN_TEMPLATE.id() ) ) {
        IVtGraphTemplate newRtGraphTemplate = doSelectTemplate();
        addRtChartTemplate( newRtGraphTemplate );
      }
      if( aActionId.equals( ACDEF_ADD.id() ) ) {
        IVtGraphTemplate newRtGraphTemplate = doAddTemplate();
        addRtChartTemplate( newRtGraphTemplate );
      }
      if( aActionId.equals( ACDEF_REMOVE.id() ) ) {
        // получаем текущий график
        CTabItem selTab = tabFolder.getSelection();
        IVtGraphTemplate selGraphTemplate = (IVtGraphTemplate)selTab.getData();
        rtChartSkids.remove( selGraphTemplate.skid() );
        // гасим RtChart
        RtChartPanel chartPanel = (RtChartPanel)selTab.getControl();
        chartPanel.dispose();
        selTab.dispose();
        saveUserSettings();
      }
      if( aActionId.equals( ACDEF_EDIT.id() ) ) {
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

    tabFolder = new CTabFolder( this, SWT.BORDER );
    tabFolder.setLayout( new BorderLayout() );
    tabFolder.setLayoutData( BorderLayout.CENTER );
    // инициализируем настройки панели
    initPanelPrefs();
    // восстанавливаем внешний вид панели
    restoreUserSettings();
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
    CTabItem tabItem = new CTabItem( tabFolder, SWT.NONE );
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
    prefSection.setOptions( masteObjectSkid, userPrefs );
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

    IList<IDataDef> currOpDefs = prefSection.listOptionDefs( masteObjectSkid );
    IStridablesListEdit<IDataDef> newPrefDefs = new StridablesList<>();
    // перебираем все устанавливаемые опции и добавляем только новые
    for( IDataDef addingOptDef : RtChartPanelOptions.allOptions() ) {
      if( !PrefUtils.hasOptionDef( currOpDefs, addingOptDef ) ) {
        newPrefDefs.add( addingOptDef );
      }
    }
    prefSection.bindOptions( masteObjectGwid, newPrefDefs );
  }

  private IOptionSet getUserPrefs() {
    return prefSection.getOptions( masteObjectSkid );
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
