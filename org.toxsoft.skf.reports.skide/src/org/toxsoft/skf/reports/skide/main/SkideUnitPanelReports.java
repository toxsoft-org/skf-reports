package org.toxsoft.skf.reports.skide.main;

import static org.toxsoft.skf.reports.skide.ISkidePluginReportsConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.panels.misc.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.login.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.skf.reports.skide.utils.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.skide.plugin.exconn.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * {@link AbstractSkideUnitPanel} implementation.
 *
 * @author max
 */
class SkideUnitPanelReports
    extends AbstractSkideUnitPanel {

  public SkideUnitPanelReports( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    TsPanel backPanel = new TsPanel( aParent, tsContext() );
    backPanel.setLayout( new BorderLayout() );

    TabFolder tabFolder = new TabFolder( backPanel, SWT.TOP );
    EIconSize tabIconSize = hdpiService().getJFaceCellIconsSize();

    // reports tab
    TabItem tiReports = new TabItem( tabFolder, SWT.NONE );
    tiReports.setText( STR_TAB_REPORTS );
    tiReports.setToolTipText( STR_TAB_REPORTS_D );
    tiReports.setImage( iconManager().loadStdIcon( ICONID_REPORT_TEMPL, tabIconSize ) );

    TsPanel reportsBack = new TsPanel( tabFolder, tsContext() );
    reportsBack.setLayout( new BorderLayout() );
    tiReports.setControl( reportsBack );

    TsPanel reportsTopComposite = new TsPanel( reportsBack, tsContext() );
    reportsTopComposite.setLayoutData( BorderLayout.NORTH );
    reportsTopComposite.setLayout( new BorderLayout() );

    Button exportButton = new Button( reportsTopComposite, SWT.PUSH );

    exportButton.setImage( iconManager().loadStdIcon( ITsStdIconIds.ICONID_ARROW_RIGHT, tabIconSize ) );
    exportButton.setToolTipText( STR_BUTTON_EXPORT_D );
    exportButton.setLayoutData( BorderLayout.WEST );
    exportButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        Display.getDefault().asyncExec( () -> {
          ISkConnection destConn = selectConnection( tsContext() );
          if( destConn == null ) {
            return;
          }
          ITsGuiContext srcContext = tsContext();
          ReportsTemplatesMigrationUtiles.moveReportTemplates( srcContext, destConn );
        } );
      }
    } );

    TsGuiContext reportContext = new TsGuiContext( tsContext() );
    // SHOW_APPLY_BUTTON.setValue( reportContext.params(), AvUtils.AV_FALSE );

    ReportTemplateEditorPanel panel = new ReportTemplateEditorPanel( reportsBack, reportContext ) {

      @Override
      protected void formReport( IVtReportTemplate aSelTemplate ) {
        ISkConnection destConn = selectConnection( tsContext() );
        doFormReport( aSelTemplate, destConn );

        // IVtReportTemplateService service = destConn.coreApi().getService( IVtReportTemplateService.SERVICE_ID );
        // IVtReportTemplate loadedSelTemplate = service.findReportTemplate( aSelTemplate.strid() );
        // doFormReport( loadedSelTemplate, destConn );

      }

    };

    panel.setLayoutData( BorderLayout.CENTER );

    // spec reports tab
    TabItem tiSpecReports = new TabItem( tabFolder, SWT.NONE );

    tiSpecReports.setText( STR_TAB_SPEC_REPORTS );
    tiSpecReports.setToolTipText( STR_TAB_SPEC_REPORTS_D );
    tiSpecReports.setImage( iconManager().loadStdIcon( ICONID_GRAPH_TEMPL, tabIconSize ) );

    TsPanel specReportsBack = new TsPanel( tabFolder, tsContext() );
    specReportsBack.setLayout( new BorderLayout() );
    tiSpecReports.setControl( specReportsBack );

    TsPanel specReportsTopComposite = new TsPanel( specReportsBack, tsContext() );
    specReportsTopComposite.setLayoutData( BorderLayout.NORTH );
    specReportsTopComposite.setLayout( new BorderLayout() );

    Button exportSpecReportsButton = new Button( specReportsTopComposite, SWT.PUSH );
    exportSpecReportsButton.setImage( iconManager().loadStdIcon( ITsStdIconIds.ICONID_ARROW_RIGHT, tabIconSize ) );
    exportSpecReportsButton.setToolTipText( STR_BUTTON_EXPORT_D );
    exportSpecReportsButton.setLayoutData( BorderLayout.WEST );
    exportSpecReportsButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        // Display.getDefault().asyncExec( () -> {
        // ISkConnection destConn = selectConnection( tsContext() );
        // if( destConn == null ) {
        // return;
        // }
        // ITsGuiContext srcContext = tsContext();
        // ReportsTemplatesMigrationUtiles.moveGraphTemplates( srcContext, destConn );
        // } );
      }
    } );

    TsGuiContext specReportsContext = new TsGuiContext( tsContext() );
    // SHOW_APPLY_BUTTON.setValue( graphContext.params(), AvUtils.AV_FALSE );

    SpecReportTemplateEditorPanel srPanel = new SpecReportTemplateEditorPanel( specReportsBack, specReportsContext ) {

      @Override
      protected void formReport( IVtSpecReportTemplate aSelTemplate ) {
        ISkConnection destConn = selectConnection( tsContext() );
        doFormReport( aSelTemplate, destConn );
      }

    };
    srPanel.setLayoutData( BorderLayout.CENTER );

    // graphs tab
    TabItem tiGraphs = new TabItem( tabFolder, SWT.NONE );

    tiGraphs.setText( STR_TAB_GRAPHS );
    tiGraphs.setToolTipText( STR_TAB_GRAPHS_D );
    tiGraphs.setImage( iconManager().loadStdIcon( ICONID_GRAPH_TEMPL, tabIconSize ) );

    TsPanel graphsBack = new TsPanel( tabFolder, tsContext() );
    graphsBack.setLayout( new BorderLayout() );
    tiGraphs.setControl( graphsBack );

    TsPanel graphsTopComposite = new TsPanel( graphsBack, tsContext() );
    graphsTopComposite.setLayoutData( BorderLayout.NORTH );
    graphsTopComposite.setLayout( new BorderLayout() );

    Button exportGraphsButton = new Button( graphsTopComposite, SWT.PUSH );
    exportGraphsButton.setImage( iconManager().loadStdIcon( ITsStdIconIds.ICONID_ARROW_RIGHT, tabIconSize ) );
    exportGraphsButton.setToolTipText( STR_BUTTON_EXPORT_D );
    exportGraphsButton.setLayoutData( BorderLayout.WEST );
    exportGraphsButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        Display.getDefault().asyncExec( () -> {
          ISkConnection destConn = selectConnection( tsContext() );
          if( destConn == null ) {
            return;
          }
          ITsGuiContext srcContext = tsContext();
          ReportsTemplatesMigrationUtiles.moveGraphTemplates( srcContext, destConn );
        } );
      }
    } );

    TsGuiContext graphContext = new TsGuiContext( tsContext() );
    // SHOW_APPLY_BUTTON.setValue( graphContext.params(), AvUtils.AV_FALSE );

    GraphTemplateEditorPanel gPanel = new GraphTemplateEditorPanel( graphsBack, graphContext ) {

      @Override
      protected void formGraph( IVtGraphTemplate aSelTemplate ) {
        ISkConnection destConn = selectConnection( tsContext() );
        doFormGraph( aSelTemplate, destConn );
      }

    };
    gPanel.setLayoutData( BorderLayout.CENTER );

    return backPanel;
  }

  ISkConnection selectConnection( ITsGuiContext aContext ) {
    ISkideExternalConnectionsService connService =
        aContext.eclipseContext().get( ISkideExternalConnectionsService.class );
    IdChain idChain = null;
    String cfgId = connService.selectConfig( aContext );
    if( cfgId != null ) {
      ILoginInfo loginInfo = new LoginInfo( "root", "1", TsLibUtils.EMPTY_STRING ); //$NON-NLS-1$ //$NON-NLS-2$
      loginInfo = PanelLoginInfo.edit( tsContext(), loginInfo, ITsValidator.PASS );

      if( loginInfo != null ) {
        idChain = connService.openConnection( cfgId, aContext, loginInfo );
      }
    }
    if( idChain == null ) {
      return null;
    }
    ISkConnectionSupplier connSupp = aContext.eclipseContext().get( ISkConnectionSupplier.class );
    return connSupp.allConns().getByKey( idChain );
  }

}
