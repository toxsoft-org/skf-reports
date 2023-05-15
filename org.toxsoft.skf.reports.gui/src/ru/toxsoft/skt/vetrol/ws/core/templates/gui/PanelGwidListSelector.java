package ru.toxsoft.skt.vetrol.ws.core.templates.gui;

import static ru.toxsoft.skt.vetrol.ws.core.templates.gui.IVtResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * {@link PanelGwidListSelector} wrapped by {@link AbstractTsDialogPanel}.
 *
 * @author dima
 */
public class PanelGwidListSelector
    extends AbstractTsDialogPanel<IGwidList, ITsGuiContext> {

  private VtClassInfoViewerPanel   classesPanel;
  private VtObjectCheckedListPanel skObjectCheckedListPanel;
  private RtDataCheckedListPanel   rtDataCheckedListPanel;

  /**
   * Конструктор панели, предназаначенной для вставки в диалог {@link TsDialog}.
   *
   * @param aParent Composite - родительская компонента
   * @param aOwnerDialog TsDialog - родительский диалог
   */
  public PanelGwidListSelector( Composite aParent, TsDialog<IGwidList, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );

    // this.setLayout( new GridLayout( 2, true ) );
    FillLayout fillLayout = new FillLayout();
    fillLayout.marginHeight = 5;
    fillLayout.marginWidth = 5;
    this.setLayout( fillLayout );
    init();
  }

  void init() {
    // create vertical sash
    SashForm verticalSashForm = new SashForm( this, SWT.VERTICAL );
    SashForm horizontalSashForm = new SashForm( verticalSashForm, SWT.HORIZONTAL );
    // panel for class selection
    classesPanel = new VtClassInfoViewerPanel( horizontalSashForm, tsContext() );

    // panel for skObjects selection
    skObjectCheckedListPanel = new VtObjectCheckedListPanel( horizontalSashForm, tsContext(), this );
    classesPanel.setSkObjectListPanel( skObjectCheckedListPanel );
    horizontalSashForm.setWeights( 1, 1 );
    horizontalSashForm.setSashWidth( 5 );
    verticalSashForm.setSashWidth( 5 );

    // panel for RtData
    rtDataCheckedListPanel = new RtDataCheckedListPanel( verticalSashForm, tsContext(), this );
    classesPanel.setRtDataCheckedListPanel( rtDataCheckedListPanel );
  }

  @Override
  protected void doSetDataRecord( IGwidList aGwidList ) {
    if( aGwidList != null && !aGwidList.isEmpty() ) {
      classesPanel.select( aGwidList.first() );
      skObjectCheckedListPanel.select( aGwidList );
      rtDataCheckedListPanel.select( aGwidList );
    }
  }

  @Override
  protected IGwidList doGetDataRecord() {
    GwidList retVal = new GwidList();
    // создаем список
    for( ISkObject skObject : skObjectCheckedListPanel.getSelectedObjs() ) {
      for( IDtoRtdataInfo rtInfo : rtDataCheckedListPanel.getSelectedRtDataList() ) {
        Gwid gwid = Gwid.createRtdata( classesPanel.getSelectedClass().id(), skObject.strid(), rtInfo.id() );
        retVal.add( gwid );
      }
    }
    return retVal;
  }

  /**
   * Выводит диалог выбора списка Gwid'ов.
   * <p>
   *
   * @param aGwidList {@link GwidList} для инициализации
   * @param aContext {@link ITsGuiContext} - контекст
   * @return IGwidList - выбранные парметры или <b>null</b> в случает отказа от выбора
   */
  public static final IGwidList selectGwids( IGwidList aGwidList, ITsGuiContext aContext ) {
    TsNullArgumentRtException.checkNull( aContext );
    IDialogPanelCreator<IGwidList, ITsGuiContext> creator = PanelGwidListSelector::new;
    ITsDialogInfo dlgInfo = new TsDialogInfo( aContext, DLG_T_GWID_LIST_SEL, STR_MSG_GWID_LIST_SELECTION );
    TsDialog<IGwidList, ITsGuiContext> d = new TsDialog<>( dlgInfo, aGwidList, aContext, creator );
    return d.execData();
  }

  @Override
  protected ValidationResult doValidate() {
    // check selected objects
    IList<ISkObject> selObjList = skObjectCheckedListPanel.getSelectedObjs();
    if( selObjList == null || selObjList.isEmpty() ) {
      return ValidationResult.error( STR_MSG_SELECT_OBJ );
    }
    // check selected rtDatas
    IList<IDtoRtdataInfo> selRtDataList = rtDataCheckedListPanel.getSelectedRtDataList();
    if( selRtDataList == null || selRtDataList.isEmpty() ) {
      return ValidationResult.error( STR_MSG_SELECT_DATA );
    }
    return ValidationResult.SUCCESS;
  }
}
