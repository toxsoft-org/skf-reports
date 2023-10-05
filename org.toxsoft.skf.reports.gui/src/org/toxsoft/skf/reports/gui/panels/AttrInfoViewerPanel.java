package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.sgw.*;

/**
 * Панель просмотра списка атрибутов {@link IDtoAttrInfo} класса.<br>
 *
 * @author dima
 */
public class AttrInfoViewerPanel
    extends TsPanel
    implements IM5ItemsProvider<IDtoAttrInfo>, IClassPropPanel {

  private final ITsSelectionChangeListener<IDtoAttrInfo> dataChangeListener = ( aSource, aSelectedItem ) -> {
    this.selectedAttr = aSelectedItem;
  };

  private final ISkConnection              conn;
  private IM5CollectionPanel<IDtoAttrInfo> cmdInfoPanel;
  private ISkClassInfo                     currClass;
  private IDtoAttrInfo                     selectedAttr = null;
  private final PanelGwidSelector          panelGwidSelector;

  /**
   * @return {@link IDtoAttrInfo} parameter selected by user
   */
  @Override
  public IDtoAttrInfo getSelectedProp() {
    return selectedAttr;
  }

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @param aPanelGwidSelector {@link PanelGwidSelector} - диалог в который вставлена панель
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public AttrInfoViewerPanel( Composite aParent, ITsGuiContext aContext, PanelGwidSelector aPanelGwidSelector ) {
    super( aParent, aContext );
    panelGwidSelector = aPanelGwidSelector;
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();
    IM5Domain m5Domain = conn.scope().get( IM5Domain.class );
    // тут получаем KM5 модель IDtoAttrInfo
    IM5Model<IDtoAttrInfo> model = m5Domain.getModel( ISgwM5Constants.MID_SGW_ATTR_INFO, IDtoAttrInfo.class );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    cmdInfoPanel = model.panelCreator().createCollViewerPanel( ctx, this );
    cmdInfoPanel.addTsSelectionListener( dataChangeListener );
    cmdInfoPanel.addTsSelectionListener( ( aSource, aSelectedItem ) -> panelGwidSelector.fireContentChangeEvent() );

    cmdInfoPanel.setItemsProvider( this );
    cmdInfoPanel.createControl( this );

  }

  /**
   * @param aClassInfo {@link ISkClassInfo} выбранный класс объекты которого нужно отобразить
   */
  @Override
  public void setClass( ISkClassInfo aClassInfo ) {
    currClass = aClassInfo;
    cmdInfoPanel.refresh();
  }

  @Override
  public IList<IDtoAttrInfo> listItems() {
    if( currClass != null ) {
      return conn.coreApi().sysdescr().getClassInfo( currClass.id() ).attrs().list();
    }
    return IList.EMPTY;
  }

  /**
   * Установить выбранный параметр
   *
   * @param aGwid выбранный параметр
   */
  @Override
  public void select( Gwid aGwid ) {
    ISkClassInfo classInfo = conn.coreApi().sysdescr().findClassInfo( aGwid.classId() );
    IDtoAttrInfo dataInfo = classInfo.attrs().list().findByKey( aGwid.propId() );
    cmdInfoPanel.setSelectedItem( dataInfo );
  }
}
