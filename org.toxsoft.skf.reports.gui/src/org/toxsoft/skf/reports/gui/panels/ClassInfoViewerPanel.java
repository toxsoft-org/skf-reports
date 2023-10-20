package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sgw.*;

/**
 * Панель просмотра списка классов ts4.<br>
 *
 * @author dima
 */
public class ClassInfoViewerPanel
    extends TsPanel {

  private final ITsSelectionChangeListener<ISkClassInfo> classChangeListener = ( aSource, aSelectedItem ) -> {
    this.selectedClass = aSelectedItem;
    if( this.skObjectPanel != null ) {
      this.skObjectPanel.setClass( this.selectedClass );
    }
    if( this.skObjectCheckedListPanel != null ) {
      this.skObjectCheckedListPanel.setClass( this.selectedClass );
    }
    if( this.propPanel != null ) {
      this.propPanel.setClass( this.selectedClass );
    }
    if( this.rtDataCheckedListPanel != null ) {
      this.rtDataCheckedListPanel.setClass( this.selectedClass );
    }
  };

  final ISkConnection                      conn;
  private IM5CollectionPanel<ISkClassInfo> classesPanel;
  private ObjectViewerPanel                skObjectPanel            = null;
  private IClassPropPanel                  propPanel                = null;
  private ISkClassInfo                     selectedClass            = null;
  private ObjectCheckedListPanel           skObjectCheckedListPanel = null;
  private RtDataCheckedListPanel           rtDataCheckedListPanel   = null;

  /**
   * @return {@link ISkClassInfo} class selected by user
   */
  public ISkClassInfo getSelectedClass() {
    return selectedClass;
  }

  /**
   * @param aSkObjectPanel {@link ObjectViewerPanel} panel to show objects of selected class
   */
  public void setSkObjectPanel( ObjectViewerPanel aSkObjectPanel ) {
    skObjectPanel = aSkObjectPanel;
  }

  /**
   * @param aPropPanel {@link IClassPropPanel} panel to show list of class properties
   */
  public void setPropPanel( IClassPropPanel aPropPanel ) {
    propPanel = aPropPanel;
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
  public ClassInfoViewerPanel( Composite aParent, ITsGuiContext aContext ) {
    super( aParent, aContext );
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    // проверяем в контекте наличие информации о соединении
    if( tsContext().params().hasKey( PanelGwidSelector.OPDEF_CONN_ID_CHAIN.id() ) ) {
      IdChain idChain = PanelGwidSelector.OPDEF_CONN_ID_CHAIN.getValue( tsContext().params() ).asValobj();
      conn = connSup.getConn( idChain );
    }
    else {
      conn = connSup.defConn();
    }
    // тут получаем KM5 модель ISkClassInfo
    IM5Domain m5 = conn.scope().get( IM5Domain.class );
    IM5Model<ISkClassInfo> model = m5.getModel( ISgwM5Constants.MID_SGW_CLASS_INFO, ISkClassInfo.class );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    classesPanel =
        model.panelCreator().createCollViewerPanel( ctx, model.findLifecycleManager( conn ).itemsProvider() );
    // setup
    classesPanel.addTsSelectionListener( classChangeListener );
    classesPanel.createControl( this );

  }

  /**
   * Установить выбранный класс
   *
   * @param aGwid выбранный параметр
   */
  public void select( Gwid aGwid ) {
    ISkClassInfo ci = conn.coreApi().sysdescr().findClassInfo( aGwid.classId() );
    classesPanel.setSelectedItem( ci );
  }

  /**
   * @param aSkObjectCheckedListPanel {@link ObjectCheckedListPanel} panel to show checkable list objects of selected
   *          class
   */
  public void setSkObjectListPanel( ObjectCheckedListPanel aSkObjectCheckedListPanel ) {
    skObjectCheckedListPanel = aSkObjectCheckedListPanel;
  }

  /**
   * @param aRtDataCheckedListPanel {@link RtDataCheckedListPanel} panel to show checkable list rtDatas of selected
   *          class
   */
  public void setRtDataCheckedListPanel( RtDataCheckedListPanel aRtDataCheckedListPanel ) {
    rtDataCheckedListPanel = aRtDataCheckedListPanel;
  }
}
