package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.Composite;
import org.toxsoft.core.tsgui.bricks.actions.ITsActionDef;
import org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.bricks.ctx.impl.TsGuiContext;
import org.toxsoft.core.tsgui.graphics.icons.EIconSize;
import org.toxsoft.core.tsgui.m5.IM5Domain;
import org.toxsoft.core.tsgui.m5.IM5Model;
import org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.MultiPaneComponentModown;
import org.toxsoft.core.tsgui.m5.gui.panels.IM5CollectionPanel;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.M5CollectionPanelMpcModownWrapper;
import org.toxsoft.core.tsgui.m5.model.IM5ItemsProvider;
import org.toxsoft.core.tsgui.m5.model.impl.M5DefaultPanelCreator;
import org.toxsoft.core.tsgui.m5.model.impl.M5Model;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.panels.toolbar.ITsToolbar;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.utils.layout.EBorderLayoutPlacement;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.bricks.events.change.IGenericChangeListener;
import org.toxsoft.core.tslib.bricks.filter.ITsFilter;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.gw.gwid.IGwidList;
import org.toxsoft.core.tslib.utils.errors.TsNotAllEnumsUsedRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.uskat.core.api.sysdescr.ISkClassInfo;
import org.toxsoft.uskat.core.api.sysdescr.dto.IDtoRtdataInfo;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;
import org.toxsoft.uskat.core.gui.km5.sgw.ISgwM5Constants;

/**
 * Панель просмотра checkable списка параметров класса {#link IDtoRtdataInfo}.<br>
 *
 * @author dima
 */
public class RtDataCheckedListPanel
    extends TsPanel
    implements IM5ItemsProvider<IDtoRtdataInfo>, IGenericChangeListener {

  static final ITsFilter<IDtoRtdataInfo>     FILTER_SYNC_OUT    = IDtoRtdataInfo::isSync;
  private final ISkConnection                conn;
  private IM5CollectionPanel<IDtoRtdataInfo> rtDataInfoPanel;
  private ISkClassInfo                       currClass;
  private IList<IDtoRtdataInfo>              selectedRtDataList = null;
  private final PanelGwidListSelector        panelGwidListSelector;

  /**
   * @return { @link ILink<IDtoRtdataInfo> } list of parameters selected by user
   */
  public IList<IDtoRtdataInfo> getSelectedRtDataList() {
    return selectedRtDataList;
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
  public RtDataCheckedListPanel( Composite aParent, ITsGuiContext aContext,
      PanelGwidListSelector aPanelGwidListSelector ) {
    super( aParent, aContext );
    panelGwidListSelector = aPanelGwidListSelector;
    this.setLayout( new BorderLayout() );
    ISkConnectionSupplier connSup = aContext.get( ISkConnectionSupplier.class );
    conn = connSup.defConn();
    IM5Domain m5Domain = conn.scope().get( IM5Domain.class );
    // тут получаем KM5 модель ISkObject
    IM5Model<IDtoRtdataInfo> model = m5Domain.getModel( ISgwM5Constants.MID_SGW_RTDATA_INFO, IDtoRtdataInfo.class );
    ITsGuiContext ctx = new TsGuiContext( aContext );
    ctx.params().addAll( aContext.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    // добавляем в панель фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AV_TRUE );

    ((M5Model<IDtoRtdataInfo>)model).setPanelCreator( new M5DefaultPanelCreator<IDtoRtdataInfo>() {

      @Override
      public IM5CollectionPanel<IDtoRtdataInfo> createCollChecksPanel( ITsGuiContext aInContext,
          IM5ItemsProvider<IDtoRtdataInfo> aItemsProvider ) {

        MultiPaneComponentModown<IDtoRtdataInfo> mpc =
            new MultiPaneComponentModown<>( aInContext, model, aItemsProvider ) {

              @Override
              protected ITsToolbar doCreateToolbar( @SuppressWarnings( "hiding" ) ITsGuiContext aContext, String aName,
                  EIconSize aIconSize, IListEdit<ITsActionDef> aActs ) {
                aActs.add( ITsStdActionDefs.ACDEF_SEPARATOR );
                aActs.add( RtDataInfoViewerPanel.ACDEF_HIDE_ASYNC );

                return super.doCreateToolbar( aContext, aName, aIconSize, aActs );
              }

              @Override
              protected void doProcessAction( String aActionId ) {

                switch( aActionId ) {
                  case RtDataInfoViewerPanel.ACTID_HIDE_ASYNC: {
                    if( toolbar().isActionChecked( RtDataInfoViewerPanel.ACTID_HIDE_ASYNC ) ) {
                      tree().filterManager().setFilter( FILTER_SYNC_OUT );
                    }
                    else {
                      tree().filterManager().setFilter( ITsFilter.ALL );
                    }
                    refresh();
                    break;
                  }
                  default:
                    throw new TsNotAllEnumsUsedRtException( aActionId );
                }
              }

            };
        return new M5CollectionPanelMpcModownWrapper<>( mpc, true );
      }
    } );
    rtDataInfoPanel = model.panelCreator().createCollChecksPanel( ctx, this );
    rtDataInfoPanel.checkSupport().checksChangeEventer().addListener( this );
    rtDataInfoPanel
        .addTsSelectionListener( ( aSource, aSelectedItem ) -> panelGwidListSelector.fireContentChangeEvent() );

    rtDataInfoPanel.setItemsProvider( this );
    rtDataInfoPanel.createControl( this );

  }

  /**
   * @param aClassInfo {@link ISkClassInfo} выбранный класс объекты которого нужно отобразить
   */
  public void setClass( ISkClassInfo aClassInfo ) {
    currClass = aClassInfo;
    rtDataInfoPanel.refresh();
  }

  @Override
  public IList<IDtoRtdataInfo> listItems() {
    if( currClass != null ) {
      return conn.coreApi().sysdescr().getClassInfo( currClass.id() ).rtdata().list();
    }
    return IList.EMPTY;
  }

  /**
   * Установить выбранные параметры
   *
   * @param aGwidList выбранные параметры
   */
  public void select( IGwidList aGwidList ) {
    ISkClassInfo classInfo = conn.coreApi().sysdescr().findClassInfo( aGwidList.first().classId() );
    for( Gwid gwid : aGwidList ) {
      IDtoRtdataInfo dataInfo = classInfo.rtdata().list().findByKey( gwid.propId() );
      rtDataInfoPanel.checkSupport().setItemCheckState( dataInfo, true );
    }
  }

  @Override
  public void onGenericChangeEvent( Object aSource ) {
    selectedRtDataList = rtDataInfoPanel.checkSupport().listCheckedItems( true );
  }
}
