package org.toxsoft.skf.reports.chart.utils.gui.console;

import static org.toxsoft.skf.reports.chart.utils.gui.IChartUtilsGuiSharedResources.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;

/**
 * Checkable list of Y axes.
 *
 * @author max
 */
public class AxisList {

  CheckboxTableViewer    table;
  private final IG2Chart chart;
  // private final IG2Console console;

  /**
   * Constructor.
   *
   * @param aChart IG2Chart - chart/
   */
  public AxisList( IG2Chart aChart ) {
    chart = aChart;
    // console = chart.console();
  }

  /**
   * Creates control.
   *
   * @param aParent Composite - parent control.
   * @return CheckboxTableViewer
   */
  public CheckboxTableViewer createControl( Composite aParent ) {
    table = CheckboxTableViewer.newCheckList( aParent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
    table.getTable().setHeaderVisible( true );
    table.getTable().setLinesVisible( true );

    TableViewerColumn columnName;
    columnName = new TableViewerColumn( table, SWT.NONE );
    columnName.getColumn().setText( STR_NAME );
    columnName.getColumn().setWidth( 60 );
    columnName.setLabelProvider( new CellLabelProvider() {

      @Override
      public void update( ViewerCell aCell ) {
        IYAxisDef yDef = (IYAxisDef)aCell.getElement();
        // aCell.setText( yDef.title() );
        aCell.setText( yDef.description() );
      }
    } );

    table.setContentProvider( new ArrayContentProvider() );
    table.setInput( chart.yAxisDefs().toArray() );
    return table;
  }

  /**
   * Returns checked Y axes.
   *
   * @return IList - list of checked Y axes.
   */
  public IList<IYAxisDef> checkedAxises() {
    IListEdit<IYAxisDef> axises = new ElemArrayList<>();
    Object[] elems = table.getCheckedElements();
    for( int i = 0; i < elems.length; i++ ) {
      axises.add( (IYAxisDef)elems[i] );
    }
    return axises;
  }
}
