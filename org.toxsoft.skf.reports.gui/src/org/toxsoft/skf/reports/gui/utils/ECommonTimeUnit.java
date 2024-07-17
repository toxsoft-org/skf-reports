package org.toxsoft.skf.reports.gui.utils;

public enum ECommonTimeUnit {

  SECOND( 1L ),
  MINUTE( 60L ),
  HOUR( 60L * 60L ),
  DAY( 24L * 60L * 60L ),
  WEEK( 7L * 24L * 60L * 60L ),
  MONTH( 31L * 24L * 60L * 60L ),
  QUARTER( 3L * 31L * 24L * 60L * 60L ),
  YEAR( 12L * 31L * 24L * 60L * 60L );

  private long sec;

  ECommonTimeUnit( long aSec ) {
    sec = aSec;
  }

  public static ECommonTimeUnit firstEqualOrLongerThan( int aIntervalLong, ECommonTimeUnit aUit ) {
    long intervalInSec = aIntervalLong * aUit.sec;

    for( ECommonTimeUnit v : values() ) {
      if( v.sec >= intervalInSec ) {
        return v;
      }
    }

    return SECOND;
  }

  public long getSec() {
    return sec;
  }

}
