package org.toxsoft.skf.reports.chart.utils.gui.panels;

import static org.toxsoft.skf.reports.chart.utils.gui.IChartUtilsGuiSharedResources.*;
import static org.toxsoft.skf.reports.chart.utils.gui.IReportsChartUtilsGuiConstants.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;

/**
 * Dialog panel for change visibility of charts
 *
 * @author vs
 * @author dima // conversion ts4
 */
public class PanelPlotSelection
    extends AbstractTsDialogPanel<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> {

  static IDialogPanelCreator<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> creator = PanelPlotSelection::new;

  ListViewer          leftViewer;
  ListViewer          rightViewer;
  IListEdit<IPlotDef> visiblePlots;
  IListEdit<IPlotDef> hiddenPlots;

  /**
   * Конструктор для использования внутри диалога.
   *
   * @param aParent Composite - родительская панель
   * @param aOwnerDialog CommonDialogBase - родительский диалог
   */
  public PanelPlotSelection( Composite aParent,
      TsDialog<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );
    setLayout( new FillLayout() );
    init();
  }

  void init() {
    // EIconSize tabIconSize = hdpiService().getJFaceCellIconsSize();
    EIconSize tabIconSize = EIconSize.IS_24X24;

    SashForm sash = new SashForm( this, SWT.HORIZONTAL );

    Composite leftPane = new Composite( sash, SWT.NONE );
    leftPane.setLayout( new BorderLayout() );
    Composite tbPanel = new Composite( leftPane, SWT.NONE );
    tbPanel.setLayoutData( BorderLayout.NORTH );
    GridLayout gl = new GridLayout( 2, false );
    gl.marginWidth = 8;
    gl.marginHeight = 8;
    tbPanel.setLayout( gl );
    CLabel l = new CLabel( tbPanel, SWT.CENTER );
    l.setText( STR_VISIBLE_CHARTS );
    Button btnHide = new Button( tbPanel, SWT.PUSH );
    btnHide.setImage( iconManager().loadStdIcon( ICONID_VISIBLE_OFF, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    btnHide.addDisposeListener( aE -> btnHide.getImage().dispose() );
    btnHide.setToolTipText( STR_HIDE_CHARTS );
    btnHide.setEnabled( false );
    btnHide.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        IStructuredSelection s = (IStructuredSelection)leftViewer.getSelection();
        if( !s.isEmpty() ) {
          hidePlotDef( (IPlotDef)s.getFirstElement() );
        }
      }
    } );

    leftViewer = new ListViewer( leftPane, SWT.BORDER );
    leftViewer.getList().setLayoutData( BorderLayout.CENTER );
    leftViewer.addSelectionChangedListener( aEvent -> btnHide.setEnabled( !aEvent.getSelection().isEmpty() ) );
    leftViewer.setLabelProvider( new LabelProvider() {

      @Override
      public String getText( Object aElement ) {
        IPlotDef plotDef = (IPlotDef)aElement;
        return plotDef.nmName();
      }
    } );
    leftViewer.getList().addMouseListener( new MouseAdapter() {

      @Override
      public void mouseDoubleClick( MouseEvent aE ) {
        IStructuredSelection s = (IStructuredSelection)leftViewer.getSelection();
        if( !s.isEmpty() ) {
          hidePlotDef( (IPlotDef)s.getFirstElement() );
        }
      }
    } );

    leftViewer.setContentProvider( new ArrayContentProvider() );

    Composite rightPane = new Composite( sash, SWT.NONE );
    rightPane.setLayout( new BorderLayout() );
    tbPanel = new Composite( rightPane, SWT.NONE );
    tbPanel.setLayoutData( BorderLayout.NORTH );
    tbPanel.setLayout( gl );
    l = new CLabel( tbPanel, SWT.CENTER );
    l.setText( STR_HIDED_CHARTS );
    Button btnShow = new Button( tbPanel, SWT.PUSH );
    btnShow.setImage( iconManager().loadStdIcon( ICONID_VISIBLE_ON, tabIconSize ) );
    // явно удаляем ранее загруженную картинку
    btnShow.addDisposeListener( aE -> btnShow.getImage().dispose() );
    btnShow.setToolTipText( STR_SHOW_CHARTS );
    btnShow.setEnabled( false );
    btnShow.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        IStructuredSelection s = (IStructuredSelection)rightViewer.getSelection();
        if( !s.isEmpty() ) {
          showPlotDef( (IPlotDef)s.getFirstElement() );
        }
      }
    } );

    rightViewer = new ListViewer( rightPane, SWT.BORDER );
    rightViewer.getList().setLayoutData( BorderLayout.CENTER );
    rightViewer.addSelectionChangedListener( aEvent -> btnShow.setEnabled( !aEvent.getSelection().isEmpty() ) );
    rightViewer.setLabelProvider( new LabelProvider() {

      @Override
      public String getText( Object aElement ) {
        IPlotDef plotDef = (IPlotDef)aElement;
        return plotDef.nmName();
      }
    } );
    rightViewer.getList().addMouseListener( new MouseAdapter() {

      @Override
      public void mouseDoubleClick( MouseEvent aE ) {
        IStructuredSelection s = (IStructuredSelection)rightViewer.getSelection();
        if( !s.isEmpty() ) {
          showPlotDef( (IPlotDef)s.getFirstElement() );
        }
      }
    } );

    rightViewer.setContentProvider( new ArrayContentProvider() );

    sash.setWeights( 1, 1 );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов AbstractDataRecordPanel
  //

  @Override
  protected void doSetDataRecord( Pair<IList<IPlotDef>, IList<IPlotDef>> aData ) {
    visiblePlots = new ElemArrayList<>( aData.left() );
    hiddenPlots = new ElemArrayList<>( aData.right() );
    updateLists();
  }

  @Override
  protected Pair<IList<IPlotDef>, IList<IPlotDef>> doGetDataRecord() {
    return new Pair<>( visiblePlots, hiddenPlots );
  }

  // ------------------------------------------------------------------------------------
  // Статический метод вызова диалога
  //

  /**
   * Selects plots for visibility
   *
   * @param aShell Shell - shell
   * @param aPlots Pair - initially visible (left) and hided (right) plots
   * @param aContext ITsGuiContext - context
   * @return Pair - resulted visible (left) and hided (right) plots
   */
  public static Pair<IList<IPlotDef>, IList<IPlotDef>> selectPlots( Shell aShell,
      Pair<IList<IPlotDef>, IList<IPlotDef>> aPlots, ITsGuiContext aContext ) {
    String caption = STR_CAP_CHART_SELECTION_FOR_VISUALIZATION;
    String title = STR_TITLE_DOUBLE_CLICK_FOR_CHANGE_VISIBILITY;
    // CommonDialogBase<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> dlg;
    // dlg = new CommonDialogBase<>( aShell, aPlots, aContext, caption, title, 0, creator );
    IDialogPanelCreator<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> locCreator = PanelPlotSelection::new;
    ITsDialogInfo dlgInfo = new TsDialogInfo( aContext, caption, title );
    TsDialog<Pair<IList<IPlotDef>, IList<IPlotDef>>, ITsGuiContext> dlg =
        new TsDialog<>( dlgInfo, aPlots, aContext, locCreator );

    return dlg.execData();
  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //

  void updateLists() {
    leftViewer.setInput( visiblePlots.toArray() );
    rightViewer.setInput( hiddenPlots.toArray() );
  }

  void hidePlotDef( IPlotDef aPlotDef ) {
    visiblePlots.remove( aPlotDef );
    hiddenPlots.add( aPlotDef );
    updateLists();
  }

  void showPlotDef( IPlotDef aPlotDef ) {
    visiblePlots.add( aPlotDef );
    hiddenPlots.remove( aPlotDef );
    updateLists();
  }

}
