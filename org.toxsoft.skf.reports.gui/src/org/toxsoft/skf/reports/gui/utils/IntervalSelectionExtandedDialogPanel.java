package org.toxsoft.skf.reports.gui.utils;

import static org.toxsoft.skf.reports.gui.utils.ISkResources.*;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Extanded interval selection dialog (includs preset intervals from refbook)
 *
 * @author max
 */
public class IntervalSelectionExtandedDialogPanel
    extends AbstractTsDialogPanel<TimeInterval, ITsGuiContext>
    implements SelectionListener {

  // ------------------------------------------------------------------------------------
  // Создание панели
  //

  /**
   * Конcтруктор для использования панели в диалоге.
   *
   * @param aParent Composite - родительская компонента
   * @param aOwnerDialog {@link TsDialog} - родительский диалог
   */
  public IntervalSelectionExtandedDialogPanel( Composite aParent, TsDialog<TimeInterval, ITsGuiContext> aOwnerDialog ) {
    super( aParent, aOwnerDialog );
    init( this );
  }

  // ------------------------------------------------------------------------------------
  // Реализация методов {@link AbstractDataRecordPanel}
  //

  @Override
  protected void doSetDataRecord( TimeInterval aParams ) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( aParams.startTime() );
    int year = cal.get( Calendar.YEAR );
    int month = cal.get( Calendar.MONTH );
    int day = cal.get( Calendar.DAY_OF_MONTH );
    int hours = cal.get( Calendar.HOUR_OF_DAY );
    int minutes = cal.get( Calendar.MINUTE );
    int seconds = cal.get( Calendar.SECOND );
    startCalendar.setDate( year, month, day );
    startTime.setTime( hours, minutes, seconds );

    cal.setTimeInMillis( aParams.endTime() );
    year = cal.get( Calendar.YEAR );
    month = cal.get( Calendar.MONTH );
    day = cal.get( Calendar.DAY_OF_MONTH );
    hours = cal.get( Calendar.HOUR_OF_DAY );
    minutes = cal.get( Calendar.MINUTE );
    seconds = cal.get( Calendar.SECOND );
    endCalendar.setDate( year, month, day );
    endTime.setTime( hours, minutes, seconds );
  }

  @Override
  protected TimeInterval doGetDataRecord() {
    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.YEAR, startCalendar.getYear() );
    cal.set( Calendar.MONTH, startCalendar.getMonth() );
    cal.set( Calendar.DAY_OF_MONTH, startCalendar.getDay() );
    cal.set( Calendar.HOUR_OF_DAY, startTime.getHours() );
    cal.set( Calendar.MINUTE, startTime.getMinutes() );
    cal.set( Calendar.SECOND, startTime.getSeconds() );
    long start = cal.getTimeInMillis();

    cal.set( Calendar.YEAR, endCalendar.getYear() );
    cal.set( Calendar.MONTH, endCalendar.getMonth() );
    cal.set( Calendar.DAY_OF_MONTH, endCalendar.getDay() );
    cal.set( Calendar.HOUR_OF_DAY, endTime.getHours() );
    cal.set( Calendar.MINUTE, endTime.getMinutes() );
    cal.set( Calendar.SECOND, endTime.getSeconds() );
    long end = cal.getTimeInMillis();

    TimeInterval retVal = new TimeInterval( start, end );
    return retVal;
  }

  static IDialogPanelCreator<TimeInterval, ITsGuiContext> creator = IntervalSelectionExtandedDialogPanel::new;

  // --------------------------------------------------------------------------
  // Открытое API
  //

  /**
   * Выводит диалог выбора диапазон времени запроса
   * <p>
   * Если пользователь отказался от выбора, возвращает null.
   *
   * @param aShell {@link Shell} - родительское окно
   * @param aInitParams - начальное значение параметров
   * @param aContext - контекст приложения
   * @return {@link TimeInterval} - выбранные параметры или null, если пользователь отказался от выбора
   */
  public static TimeInterval getParams( Shell aShell, TimeInterval aInitParams, ITsGuiContext aContext ) {

    TsNullArgumentRtException.checkNull( aContext );
    ITsDialogInfo dlgInfo = new TsDialogInfo( aContext, DLG_TITLE_PERIOD_SEL, DLG_HEADER_PERIOD_SEL );
    TsDialog<TimeInterval, ITsGuiContext> d = new TsDialog<>( dlgInfo, aInitParams, aContext, creator );
    return d.execData();

  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //

  private static final String STR_N_INTERVAL_FROM_REFBOOK               = "Интервал из справочника";
  private static final String STR_ADDITIONAL_TITLE_CORRECT_FROM_REFBOOK = " (коррект. из справочника)";

  private static final String REFBOOK_INTERVAL_ID                     = "report.intervalw"; //$NON-NLS-1$
  private static final String REFBOOK_INTERVAL_LENGTH_ATTR_ID         = "length";           //$NON-NLS-1$
  private static final String REFBOOK_INTERVAL_SHIFT_ATTR_ID          = "shift";            //$NON-NLS-1$
  private static final String REFBOOK_INTERVAL_LENGTH_UNIT_LINK_ID    = "length.unit";      //$NON-NLS-1$
  private static final String REFBOOK_INTERVAL_SHIFT_UNIT_LINK_ID     = "shift.unit";       //$NON-NLS-1$
  private static final String REFBOOK_INTERVAL_SHIFT_RELATIVE_LINK_ID = "shift.relative";   //$NON-NLS-1$

  // Контроль ввода времени начала
  private DateTime startCalendar;
  private DateTime startTime;
  // Контроль ввода времени окончания
  private DateTime endCalendar;
  private DateTime endTime;
  // Контроль Выбора интервала из справочника
  private CCombo refbooklIntervalsCombo;

  private IList<ISkRefbookItem> rbItems;

  private void init( Composite aParent ) {
    setLayout( new GridLayout( 2, false ) );

    Group gridParamsGroup = new Group( aParent, SWT.NONE );
    gridParamsGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
    gridParamsGroup.setLayout( new GridLayout( 3, false ) );

    CLabel startLabel = new CLabel( gridParamsGroup, SWT.CENTER );
    startLabel.setText( START_STR );
    startCalendar = new DateTime( gridParamsGroup, SWT.DROP_DOWN );
    startTime = new DateTime( gridParamsGroup, SWT.TIME );

    GridData gd = new GridData( GridData.FILL_BOTH );
    gd.verticalAlignment = GridData.FILL;
    gd.grabExcessHorizontalSpace = true;
    startTime.setLayoutData( gd );

    CLabel l = new CLabel( gridParamsGroup, SWT.CENTER );
    l.setText( END_STR );
    endCalendar = new DateTime( gridParamsGroup, SWT.DROP_DOWN );
    endTime = new DateTime( gridParamsGroup, SWT.TIME );

    gd.verticalAlignment = GridData.FILL;
    gd.grabExcessHorizontalSpace = true;
    endTime.setLayoutData( gd );

    // поиск справочника интервалов
    ISkConnectionSupplier connSupplier = tsContext().get( ISkConnectionSupplier.class );
    ISkConnection conn = connSupplier.defConn();

    ISkRefbookService skRefServ = (ISkRefbookService)conn.coreApi().getService( ISkRefbookService.SERVICE_ID );
    ISkRefbook intervalsRefbook = skRefServ.findRefbook( REFBOOK_INTERVAL_ID );
    if( intervalsRefbook == null ) {
      return;
    }

    Button intervalCheck = new Button( gridParamsGroup, SWT.CHECK );
    intervalCheck.setText( STR_N_INTERVAL_FROM_REFBOOK );
    intervalCheck.addSelectionListener( new SelectionListener() {

      @Override
      public void widgetSelected( SelectionEvent aE ) {
        refbooklIntervalsCombo.setEnabled( intervalCheck.getSelection() );
        endCalendar.setEnabled( !intervalCheck.getSelection() );
        endTime.setEnabled( !intervalCheck.getSelection() );

        startLabel.setText(
            intervalCheck.getSelection() ? START_STR + STR_ADDITIONAL_TITLE_CORRECT_FROM_REFBOOK : START_STR );
        gridParamsGroup.layout();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent aE ) {
        // nop
      }
    } );

    refbooklIntervalsCombo = new CCombo( gridParamsGroup, SWT.BORDER | SWT.READ_ONLY );
    refbooklIntervalsCombo.setEnabled( false );
    GridData gd2 = new GridData( GridData.FILL_BOTH );
    gd2.verticalAlignment = GridData.FILL;
    gd2.grabExcessHorizontalSpace = true;
    gd2.horizontalSpan = 2;
    refbooklIntervalsCombo.setLayoutData( gd2 );

    rbItems = intervalsRefbook.listItems();

    for( ISkRefbookItem rbItem : rbItems ) {
      refbooklIntervalsCombo.add( rbItem.nmName() );
    }

    refbooklIntervalsCombo.addSelectionListener( this );
  }

  @Override
  public void widgetSelected( SelectionEvent aE ) {
    int selectionIntervalIndex = refbooklIntervalsCombo.getSelectionIndex();
    if( selectionIntervalIndex < 0 ) {
      return;
    }

    ISkRefbookItem selectionInterval = rbItems.get( selectionIntervalIndex );

    int intervalLength = selectionInterval.attrs().getInt( REFBOOK_INTERVAL_LENGTH_ATTR_ID );
    ECommonTimeUnit lenghtUnit = ECommonTimeUnit.valueOf(
        selectionInterval.getLinkSkids( REFBOOK_INTERVAL_LENGTH_UNIT_LINK_ID ).first().strid().toUpperCase() );
    int intervalShift = selectionInterval.attrs().getInt( REFBOOK_INTERVAL_SHIFT_ATTR_ID );
    ECommonTimeUnit shiftUnit = ECommonTimeUnit
        .valueOf( selectionInterval.getLinkSkids( REFBOOK_INTERVAL_SHIFT_UNIT_LINK_ID ).first().strid().toUpperCase() );
    ECommonTimeUnit shiftRelativeFrom = ECommonTimeUnit.valueOf(
        selectionInterval.getLinkSkids( REFBOOK_INTERVAL_SHIFT_RELATIVE_LINK_ID ).first().strid().toUpperCase() );

    // Выбрать сравнимый с интервалом или с тем относительно чего сдвиг (если он больше) (можно путём выбора меньшего,
    // но с округлением до сравнимого)

    // нужно выбрать характерный интервал (не меньше самого интервала)
    ECommonTimeUnit charcInterval = ECommonTimeUnit.firstEqualOrLongerThan( intervalLength, lenghtUnit );

    // если есть сдвиг - то в качестве характерного участка выбрать тот, отноительно которого сдвиг
    if( intervalShift != 0 ) {
      charcInterval = shiftRelativeFrom;
    }

    // в текущем инетрвале найти начало и сопоставить с характерным интервалом
    TimeInterval currInterval = doGetDataRecord();

    long startTimeSec = currInterval.startTime() / 1000;

    long nearestTimeSec = getNearestLeftInSec( startTimeSec, charcInterval );

    if( intervalShift != 0 ) {
      nearestTimeSec = getIntervalEndInSec( nearestTimeSec, intervalShift, shiftUnit );
    }

    TimeInterval newInterval = new TimeInterval( nearestTimeSec * 1000L,
        getIntervalEndInSec( nearestTimeSec, intervalLength, lenghtUnit ) * 1000L );

    doSetDataRecord( newInterval );

  }

  @Override
  public void widgetDefaultSelected( SelectionEvent aE ) {
    // nop
  }

  long getIntervalEndInSec( long aStartTimeSec, int aLength, ECommonTimeUnit aUnitLength ) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( aStartTimeSec * 1000L );
    return switch( aUnitLength ) {
      case SECOND -> aStartTimeSec + aLength;
      case MINUTE -> aStartTimeSec + aLength * 60;
      case HOUR -> aStartTimeSec + aLength * 3600;
      case DAY -> {
        cal.add( Calendar.DAY_OF_MONTH, aLength );
        yield cal.getTimeInMillis() / 1000;

      }
      case WEEK -> {
        cal.add( Calendar.WEEK_OF_YEAR, aLength );
        yield cal.getTimeInMillis() / 1000;
      }
      case MONTH -> {
        cal.add( Calendar.MONTH, aLength );
        yield cal.getTimeInMillis() / 1000;
      }
      case QUARTER -> {
        cal.add( Calendar.MONTH, aLength * 3 );
        yield cal.getTimeInMillis() / 1000;
      }
      case YEAR -> {
        cal.add( Calendar.YEAR, 1 );
        yield cal.getTimeInMillis() / 1000;
      }
      default -> aStartTimeSec + aLength;
    };
  }

  long getNearestLeftInSec( long aTimeSec, ECommonTimeUnit aCharcInterval ) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( aTimeSec * 1000L );
    int year = cal.get( Calendar.YEAR );
    int month = cal.get( Calendar.MONTH );
    Calendar cal2 = Calendar.getInstance();
    return switch( aCharcInterval ) {
      case SECOND -> aTimeSec;
      case MINUTE -> (aTimeSec / 60) * 60;
      case HOUR -> (aTimeSec / 3600) * 3600;
      case DAY -> {
        cal2.set( Calendar.YEAR, year );
        cal2.set( Calendar.MONTH, month );
        cal2.set( Calendar.DAY_OF_MONTH, cal.get( Calendar.DAY_OF_MONTH ) );
        cal2.set( Calendar.HOUR_OF_DAY, 0 );
        cal2.set( Calendar.MINUTE, 0 );
        cal2.set( Calendar.SECOND, 0 );
        yield cal2.getTimeInMillis() / 1000;

      }
      case WEEK -> {
        int weekOfYear = cal.get( Calendar.WEEK_OF_YEAR );
        int dayOfYear = cal.get( Calendar.DAY_OF_YEAR );
        int newYear = year;
        if( weekOfYear > 51 && dayOfYear < 7 ) {
          newYear--;
        }
        cal2.set( Calendar.YEAR, newYear );
        cal2.set( Calendar.WEEK_OF_YEAR, weekOfYear );
        cal2.set( Calendar.DAY_OF_WEEK, 2 );
        cal2.set( Calendar.HOUR_OF_DAY, 0 );
        cal2.set( Calendar.MINUTE, 0 );
        cal2.set( Calendar.SECOND, 0 );
        yield cal2.getTimeInMillis() / 1000;
      }
      case MONTH -> {
        cal2.set( Calendar.YEAR, year );
        cal2.set( Calendar.MONTH, month );
        cal2.set( Calendar.DAY_OF_MONTH, 1 );
        cal2.set( Calendar.HOUR_OF_DAY, 0 );
        cal2.set( Calendar.MINUTE, 0 );
        cal2.set( Calendar.SECOND, 0 );
        yield cal2.getTimeInMillis() / 1000;
      }
      case QUARTER -> {
        cal2.set( Calendar.YEAR, year );
        cal2.set( Calendar.MONTH, (month / 3) * 3 );
        cal2.set( Calendar.DAY_OF_MONTH, 1 );
        cal2.set( Calendar.HOUR_OF_DAY, 0 );
        cal2.set( Calendar.MINUTE, 0 );
        cal2.set( Calendar.SECOND, 0 );
        yield cal2.getTimeInMillis() / 1000;
      }
      case YEAR -> {
        cal2.set( Calendar.YEAR, year );
        cal2.set( Calendar.MONTH, 0 );
        cal2.set( Calendar.DAY_OF_MONTH, 1 );
        cal2.set( Calendar.HOUR_OF_DAY, 0 );
        cal2.set( Calendar.MINUTE, 0 );
        cal2.set( Calendar.SECOND, 0 );
        yield cal2.getTimeInMillis() / 1000;
      }
      default -> aTimeSec;
    };

  }
}
