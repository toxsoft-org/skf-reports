package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.Composite;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.bricks.ctx.impl.TsGuiContext;
import org.toxsoft.core.tsgui.bricks.stdevents.ITsSelectionChangeListener;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants;
import org.toxsoft.core.tsgui.m5.gui.panels.IM5CollectionPanel;
import org.toxsoft.core.tsgui.m5.model.IM5ItemsProvider;
import org.toxsoft.core.tsgui.m5.model.impl.M5FieldDef;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.utils.layout.EBorderLayoutPlacement;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.uskat.core.ISkHardConstants;
import org.toxsoft.uskat.core.api.objserv.ISkObject;
import org.toxsoft.uskat.core.api.sysdescr.ISkClassInfo;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;
import org.toxsoft.uskat.core.gui.km5.sgw.ISgwM5Constants;

/**
 * Панель просмотра списка объектов выбранного класса.<br>
 *
 * @author dima
 */
public class ObjectViewerPanel
    extends TsPanel
    implements IM5ItemsProvider<ISkObject> {

  private final ITsSelectionChangeListener<ISkObject> objChangeListener = ( aSource, aSelectedItem ) -> {
    this.selectedObj = aSelectedItem;
  };

  private final ISkConnection           conn;
  private IM5CollectionPanel<ISkObject> skObjectPanel;
  private ISkClassInfo                  currClass;
  private ISkObject                     selectedObj = null;
  private final PanelGwidSelector       panelGwidSelector;

  /**
   * @return {#link ISkObject} skObject selected by user
   */
  public ISkObject getSelectedObj() {
    return selectedObj;
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
  public ObjectViewerPanel( Composite aParent, ITsGuiContext aContext, PanelGwidSelector aPanelGwidSelector ) {
    super( aParent, aContext );
    panelGwidSelector = aPanelGwidSelector;
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();
    IM5Domain m5Domain = conn.scope().get( IM5Domain.class );
    // тут получаем KM5 модель ISkObject
    IM5Model<ISkObject> model = m5Domain.getModel( ISgwM5Constants.MID_SGW_SK_OBJECT, ISkObject.class );
    @SuppressWarnings( "unchecked" )
    M5FieldDef<ISkObject, Skid> fd =
        (M5FieldDef<ISkObject, Skid>)model.fieldDefs().getByKey( ISkHardConstants.AID_SKID );
    fd.addFlags( IM5Constants.M5FF_COLUMN );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );

    skObjectPanel = model.panelCreator().createCollViewerPanel( ctx, this );
    skObjectPanel.addTsSelectionListener( objChangeListener );
    skObjectPanel.addTsSelectionListener( ( aSource, aSelectedItem ) -> panelGwidSelector.fireContentChangeEvent() );

    skObjectPanel.setItemsProvider( this );
    skObjectPanel.createControl( this );

  }

  /**
   * @param aClassInfo {@link ISkClassInfo} выбранный класс объекты которого нужно отобразить
   */
  public void setClass( ISkClassInfo aClassInfo ) {
    currClass = aClassInfo;
    skObjectPanel.refresh();
  }

  @Override
  public IList<ISkObject> listItems() {
    if( currClass != null ) {
      return conn.coreApi().objService().listObjs( currClass.id(), false );
    }
    return IList.EMPTY;
  }

  /**
   * Установить выбранный объект
   *
   * @param aGwid выбранный параметр
   */
  public void select( Gwid aGwid ) {
    ISkObject skObj = conn.coreApi().objService().find( aGwid.skid() );
    skObjectPanel.setSelectedItem( skObj );
  }
}
