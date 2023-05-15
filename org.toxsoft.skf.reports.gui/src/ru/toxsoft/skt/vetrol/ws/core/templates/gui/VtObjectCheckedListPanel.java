package ru.toxsoft.skt.vetrol.ws.core.templates.gui;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.Composite;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.bricks.ctx.impl.TsGuiContext;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants;
import org.toxsoft.core.tsgui.m5.gui.panels.IM5CollectionPanel;
import org.toxsoft.core.tsgui.m5.model.IM5ItemsProvider;
import org.toxsoft.core.tsgui.m5.model.impl.M5FieldDef;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.utils.checkcoll.ITsCheckSupport;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.utils.layout.EBorderLayoutPlacement;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.bricks.events.change.IGenericChangeListener;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.gw.gwid.IGwidList;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.uskat.core.ISkHardConstants;
import org.toxsoft.uskat.core.api.objserv.ISkObject;
import org.toxsoft.uskat.core.api.sysdescr.ISkClassInfo;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;
import org.toxsoft.uskat.core.gui.km5.sgw.ISgwM5Constants;

/**
 * Панель просмотра checkable списка объектов выбранного класса.<br>
 *
 * @author dima
 */
public class VtObjectCheckedListPanel
    extends TsPanel
    implements IM5ItemsProvider<ISkObject> {

  private final ISkConnection           conn;
  private IM5CollectionPanel<ISkObject> skObjectPanel;
  private ISkClassInfo                  currClass;
  private IList<ISkObject>              selectedObjs = null;
  private final PanelGwidListSelector   panelGwidListSelector;

  /**
   * @return {#link IList<ISkObject>} List of skObjectы selected by user
   */
  public IList<ISkObject> getSelectedObjs() {
    return selectedObjs;
  }

  /**
   * Конструктор панели.
   * <p>
   * Конструктор просто запоминает ссылку на контекст, без создания копии.
   *
   * @param aParent {@link Composite} - родительская панель
   * @param aContext {@link ITsGuiContext} - контекст панели
   * @param aPanelGwidListSelector {@link PanelGwidListSelector} - диалог в который вставлена панель
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public VtObjectCheckedListPanel( Composite aParent, ITsGuiContext aContext,
      PanelGwidListSelector aPanelGwidListSelector ) {
    super( aParent, aContext );
    panelGwidListSelector = aPanelGwidListSelector;
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

    skObjectPanel = model.panelCreator().createCollChecksPanel( ctx, this );
    skObjectPanel.checkSupport().checksChangeEventer().addListener( this::whenObjsListCheckStateChanged );

    skObjectPanel
        .addTsSelectionListener( ( aSource, aSelectedItem ) -> panelGwidListSelector.fireContentChangeEvent() );

    skObjectPanel.setItemsProvider( this );
    skObjectPanel.createControl( this );

  }

  /**
   * Handles any ability chack state change in the abilities tree on the right.
   * <p>
   * Has the same signature as {@link IGenericChangeListener#onGenericChangeEvent(Object)} to set as listener of
   * {@link ITsCheckSupport#checksChangeEventer()}.
   *
   * @param aSource Object - the event source
   */
  void whenObjsListCheckStateChanged( Object aSource ) {
    selectedObjs = skObjectPanel.checkSupport().listCheckedItems( true );
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
   * Установить выбранный список объектов
   *
   * @param aGwidList список выбранных параметров
   */
  public void select( IGwidList aGwidList ) {
    IList<ISkObject> skObjs = conn.coreApi().objService().getObjs( aGwidList.objIds() );
    skObjectPanel.checkSupport().setItemsCheckState( skObjs, true );
  }
}
