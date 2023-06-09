package org.toxsoft.skf.reports.chart.utils.gui.console;

import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.reports.chart.utils.gui.IChartUtilsGuiSharedResources.*;
import static org.toxsoft.skf.reports.chart.utils.gui.IReportsChartUtilsGuiConstants.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.chart.impl.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Control panel of graphic component.
 *
 * @author vs
 * @author dima // ts4 conversion
 */
public class ConsoleWindow {

  final static TsActionDef ACDEF_ZOOM_IN =
      TsActionDef.ofPush2( ACTID_PLOT_ZOOM_IN, STR_N_PLOT_ZOOM_IN, STR_D_PLOT_ZOOM_IN, ICONID_ZOOM_IN );

  final static TsActionDef ACDEF_ZOOM_OUT =
      TsActionDef.ofPush2( ACTID_PLOT_ZOOM_OUT, STR_N_PLOT_ZOOM_OUT, STR_D_PLOT_ZOOM_OUT, ICONID_ZOOM_OUT );

  Shell            wnd = null;
  final Control    parent;
  final G2Chart    g2Chart;
  final IG2Console g2Console;

  MiniMap miniMap;

  AxisList axisList;

  // private final ImageDescriptor imgPlus;
  // private final ImageDescriptor imgMinus;

  private final ITsGuiContext context;

  /**
   * Constructor.
   *
   * @param aParent Control - parent control.
   * @param aChart G2Chart - chart.
   * @param aContext ITsGuiContext - context.
   */
  public ConsoleWindow( Control aParent, G2Chart aChart, ITsGuiContext aContext ) {
    parent = aParent;
    g2Chart = aChart;
    context = aContext;

    // imgPlus = AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is22x22/plus.png" );
    // imgMinus = AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is22x22/minus.png" );

    Shell aShell = aParent.getShell();
    wnd = new Shell( aShell, SWT.BORDER | SWT.CLOSE );
    wnd.setText( STR_N_CONTROL_PANEL );
    wnd.setLayout( new BorderLayout() );
    wnd.addDisposeListener( aE -> {
      // FIXME чего-нибудь уничтожить (если надо)
    } );

    ITsToolbar tb = createToolBar( wnd );
    tb.getControl().setLayoutData( BorderLayout.NORTH );

    g2Console = g2Chart.console();

    miniMap = new MiniMap( wnd );
    miniMap.addGenericChangeListener( aSource -> {
      Pair<Double, Double> dragShif = miniMap.dragShift();
      if( dragShif.left().doubleValue() != 0.0 ) {
        g2Console.changeXScale( -dragShif.left().doubleValue(), 1.0 );
      }
      if( dragShif.right().doubleValue() != 0.0 ) {
        doVerticalShift( dragShif.right() );
      }
      g2Chart.refresh();
    } );
    miniMap.bkPanel.setLayoutData( BorderLayout.CENTER );

    axisList = new AxisList( aChart );
    CheckboxTableViewer table = axisList.createControl( wnd );
    table.getTable().setLayoutData( BorderLayout.WEST );

    wnd.setSize( 400, 400 );
    setLocation( 100, 100 );
    wnd.open();
  }

  // ------------------------------------------------------------------------------------
  // API
  //

  /**
   * Disposes window.
   */
  public void dispose() {
    if( wnd != null && !wnd.isDisposed() ) {
      wnd.dispose();
      wnd = null;
    }
  }

  /**
   * Returns shell.
   *
   * @return Shell - shell.
   */
  public Shell shell() {
    return wnd;
  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //

  void setLocation( int aX, int aY ) {
    Point p = parent.toDisplay( aX, aY );
    wnd.setLocation( p.x, p.y + 2 );
  }

  void doVerticalShift( Double aShift ) {
    IList<IYAxisDef> axises = axisList.checkedAxises();
    if( axises.size() == 0 ) {
      for( IYAxisDef yDef : g2Chart.yAxisDefs() ) {
        g2Console.shiftYAxis( yDef.id(), aShift.doubleValue() );
      }
    }
    else {
      for( IYAxisDef yDef : axises ) {
        g2Console.shiftYAxis( yDef.id(), aShift.doubleValue() );
      }
    }
  }

  ITsToolbar createToolBar( Composite aParent ) {

    ITsToolbar retVal = TsToolbar.create( aParent, context, ACDEF_ZOOM_IN, ACDEF_ZOOM_OUT );
    retVal.setIconSize( EIconSize.IS_64X64 );
    retVal.addListener( aActionId -> {
      switch( aActionId ) {
        case ACTID_PLOT_ZOOM_IN: {
          doZoomIn();
          break;
        }
        case ACTID_PLOT_ZOOM_OUT: {
          doZoomOut();
          break;
        }
        default:
          throw new TsNotAllEnumsUsedRtException( aActionId );
      }
    } );
    return retVal;
  }

  void doZoomIn() {
    IList<IYAxisDef> axises = axisList.checkedAxises();
    for( IYAxisDef yDef : axises ) {
      g2Console.scaleYAxis( yDef.id(), 2 );
    }
    g2Chart.refresh();
  }

  void doZoomOut() {
    IList<IYAxisDef> axises = axisList.checkedAxises();
    for( IYAxisDef yDef : axises ) {
      g2Console.scaleYAxis( yDef.id(), 0.5 );
    }
    g2Chart.refresh();
  }

}
