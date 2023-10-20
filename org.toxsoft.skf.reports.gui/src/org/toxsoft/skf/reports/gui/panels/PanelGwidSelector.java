package org.toxsoft.skf.reports.gui.panels;

import static org.toxsoft.core.tslib.ITsHardConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.panels.ISkResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.strid.more.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.sysdescr.*;

/**
 * {@link PanelGwidSelector} wrapped by {@link AbstractTsDialogPanel}.
 *
 * @author dima
 */
public class PanelGwidSelector
    extends AbstractTsDialogPanel<Gwid, ITsGuiContext> {

  static String MPC_OP_ID = TS_ID + ".m5.gui.mpc"; //$NON-NLS-1$

  static IDataDef OPDEF_CLASS_PROP_KIND = DataDef.create( MPC_OP_ID + ".SkClassPropKind", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_N_CLASS_PROP_KIND, //
      TSID_DESCRIPTION, STR_D_CLASS_PROP_KIND, //
      TSID_KEEPER_ID, ESkClassPropKind.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ESkClassPropKind.RTDATA ) //
  );

  /**
   * id of ISkConnection to use in dialog
   */
  public static IDataDef OPDEF_CONN_ID_CHAIN = DataDef.create( MPC_OP_ID + ".IdChain", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_N_CONN_ID_CHAIN, //
      TSID_DESCRIPTION, STR_D_CONN_ID_CHAIN, //
      TSID_KEEPER_ID, IdChain.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( IdChain.NULL ) //
  );

  private ClassInfoViewerPanel classesPanel;
  private ObjectViewerPanel    skObjectsPanel;
  private IClassPropPanel      propPanel;
  private ESkClassPropKind     propKind;

  /**
   * Конструктор панели, предназаначенной для вставки в диалог {@link TsDialog}.
   *
   * @param aParent Composite - родительская компонента
   * @param aOwnerDialog TsDialog - родительский диалог
   */
  public PanelGwidSelector( Composite aParent, TsDialog<Gwid, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );

    // this.setLayout( new GridLayout( 2, true ) );
    FillLayout fillLayout = new FillLayout();
    fillLayout.marginHeight = 5;
    fillLayout.marginWidth = 5;
    this.setLayout( fillLayout );
    propKind = OPDEF_CLASS_PROP_KIND.getValue( tsContext().params() ).asValobj();

    init();
  }

  void init() {
    // create vertical sash
    SashForm verticalSashForm = new SashForm( this, SWT.VERTICAL );
    SashForm horizontalSashForm = new SashForm( verticalSashForm, SWT.HORIZONTAL );
    // panel for class selection
    classesPanel = new ClassInfoViewerPanel( horizontalSashForm, tsContext() );
    // classesPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    // panel for skObject selection
    skObjectsPanel = new ObjectViewerPanel( horizontalSashForm, tsContext(), this );
    // skObjectsPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    classesPanel.setSkObjectPanel( skObjectsPanel );
    horizontalSashForm.setWeights( 1, 1 );
    horizontalSashForm.setSashWidth( 5 );
    // verticalSashForm.setWeights( 1, 1 );
    verticalSashForm.setSashWidth( 5 );

    // panel class properties
    propPanel = createPropPanel( verticalSashForm );
    // propPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
    classesPanel.setPropPanel( propPanel );
  }

  private IClassPropPanel createPropPanel( Composite aParent ) {
    IClassPropPanel retVal = null;
    switch( propKind ) {
      case ATTR:
        retVal = new AttrInfoViewerPanel( aParent, tsContext(), this );
        break;
      case CLOB:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select clob" );
      case CMD:
        retVal = new CmdInfoViewerPanel( aParent, tsContext(), this );
        break;
      case EVENT:
        retVal = new EventInfoViewerPanel( aParent, tsContext(), this );
        break;
      case LINK:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select link" );
      case RIVET:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select rivet" );
      case RTDATA:
        retVal = new RtDataInfoViewerPanel( aParent, tsContext(), this );
        break;
      default:
        break;
    }
    return retVal;
  }

  @Override
  protected void doSetDataRecord( Gwid aGwid ) {
    if( aGwid != null ) {
      classesPanel.select( aGwid );
      skObjectsPanel.select( aGwid );
      propPanel.select( aGwid );
    }
  }

  @Override
  protected Gwid doGetDataRecord() {
    Gwid retVal = getSelection();
    return retVal;
  }

  private Gwid getSelection() {
    Gwid retVal = null;
    switch( propKind ) {
      case ATTR:
        retVal = Gwid.createAttr( classesPanel.getSelectedClass().id(), skObjectsPanel.getSelectedObj().strid(),
            propPanel.getSelectedProp().id() );
        break;
      case CLOB:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select clob" );
      case CMD:
        retVal = Gwid.createCmd( classesPanel.getSelectedClass().id(), skObjectsPanel.getSelectedObj().strid(),
            propPanel.getSelectedProp().id() );
        break;
      case EVENT:
        retVal = Gwid.createEvent( classesPanel.getSelectedClass().id(), skObjectsPanel.getSelectedObj().strid(),
            propPanel.getSelectedProp().id() );
        break;
      case LINK:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select link" );
      case RIVET:
        throw new TsUnderDevelopmentRtException( "PanelGwidSelector can't select rivet" );
      case RTDATA:
        retVal = Gwid.createRtdata( classesPanel.getSelectedClass().id(), skObjectsPanel.getSelectedObj().strid(),
            propPanel.getSelectedProp().id() );
        break;
      default:
        break;
    }
    return retVal;
  }

  /**
   * Выводит диалог выбора Gwid.
   * <p>
   *
   * @param aGwid {@link Gwid} для инициализации
   * @param aContext {@link ITsGuiContext} - контекст
   * @param aClassPropKind {@link ESkClassPropKind} - тип свойств для выбора
   * @param aIdChain {@link IdChain} - id connection
   * @return {@link Gwid} - выбранный параметр <b>null</b> в случает отказа от редактирования
   */
  public static final Gwid selectGwid( Gwid aGwid, ITsGuiContext aContext, ESkClassPropKind aClassPropKind,
      IdChain aIdChain ) {
    TsNullArgumentRtException.checkNull( aContext );
    OPDEF_CLASS_PROP_KIND.setValue( aContext.params(), avValobj( aClassPropKind ) );
    if( aIdChain != null ) {
      OPDEF_CONN_ID_CHAIN.setValue( aContext.params(), avValobj( aIdChain ) );
    }
    IDialogPanelCreator<Gwid, ITsGuiContext> creator = PanelGwidSelector::new;
    ITsDialogInfo dlgInfo = new TsDialogInfo( aContext, DLG_GWID_SELECTOR, DLG_GWID_SELECTOR_D );
    TsDialog<Gwid, ITsGuiContext> d = new TsDialog<>( dlgInfo, aGwid, aContext, creator );
    return d.execData();
  }

  @Override
  protected ValidationResult doValidate() {
    // check selected object
    if( skObjectsPanel.getSelectedObj() == null ) {
      return ValidationResult.error( MSG_ERR_NO_OBJ_SELECTED );
    }
    // check selected prop
    if( propPanel.getSelectedProp() == null ) {
      return ValidationResult.error( MSG_ERR_NO_PROP_SELECTED );
    }
    return ValidationResult.SUCCESS;
  }
}
