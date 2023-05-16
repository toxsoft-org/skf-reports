package org.toxsoft.skf.reports.skide.main;

import static org.toxsoft.skf.reports.skide.ISkidePluginReportsConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;

import ru.toxsoft.skt.vetrol.ws.reports.e4.uiparts.*;

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
    VtReportTemplateEditorPanel panel = new VtReportTemplateEditorPanel( tabFolder, tsContext() );
    tiReports.setControl( panel );
    tiReports.setText( STR_TAB_REPORTS );
    tiReports.setToolTipText( STR_TAB_REPORTS_D );
    tiReports.setImage( iconManager().loadStdIcon( ICONID_REPORT_TEMPL, tabIconSize ) );

    // graphs tab
    TabItem tiGraphs = new TabItem( tabFolder, SWT.NONE );
    VtGraphTemplateEditorPanel gPanel = new VtGraphTemplateEditorPanel( tabFolder, tsContext() );
    tiGraphs.setControl( gPanel );
    tiGraphs.setText( STR_TAB_GRAPHS );
    tiGraphs.setToolTipText( STR_TAB_GRAPHS_D );
    tiGraphs.setImage( iconManager().loadStdIcon( ICONID_GRAPH_TEMPL, tabIconSize ) );
    return backPanel;
  }

}
