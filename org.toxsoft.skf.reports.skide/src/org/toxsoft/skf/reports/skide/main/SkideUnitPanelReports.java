package org.toxsoft.skf.reports.skide.main;

import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
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
    aParent.setLayout( new BorderLayout() );
    TabFolder tabFolder = new TabFolder( aParent, SWT.TOP );
    EIconSize tabIconSize = hdpiService().getJFaceCellIconsSize();
    // reports tab
    TabItem tiReports = new TabItem( tabFolder, SWT.NONE );
    VtReportTemplateEditorPanel panel = new VtReportTemplateEditorPanel( tabFolder, tsContext() );
    tiReports.setControl( panel );
    tiReports.setText( STR_TAB_REPORTS );
    tiReports.setToolTipText( STR_TAB_REPORTS_D );
    // tiReports.setImage( iconManager().loadStdIcon( ICONID_USERS_LIST, tabIconSize ) );

    // graphs tab
    TabItem tiGraphs = new TabItem( tabFolder, SWT.NONE );
    VtGraphTemplateEditorPanel gPanel = new VtGraphTemplateEditorPanel( tabFolder, tsContext() );
    tiGraphs.setControl( gPanel );
    tiGraphs.setText( STR_TAB_GRAPHS );
    tiGraphs.setToolTipText( STR_TAB_GRAPHS_D );
    // tiRoles.setImage( iconManager().loadStdIcon( ICONID_ROLES_LIST, tabIconSize ) );
    return tabFolder;
  }

}
