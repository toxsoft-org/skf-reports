package org.toxsoft.skf.reports.gui.utils;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.reports.gui.utils.ISkResources.*;

import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.skf.refbooks.lib.*;
import org.toxsoft.skf.refbooks.lib.impl.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Create Y scale refbook to be used in chart
 *
 * @author dima
 */
public class YScaleRefbookGenerator {

  static class RefookItemBuider {

    IOptionSetEdit attrValues = new OptionSet();

    private final Skid   skid;
    private final String name;
    private final String descr;

    RefookItemBuider( String aId, String aName, String aDescr, ISkRefbook aRefbook ) {
      String classId = aRefbook.itemClassId();
      skid = new Skid( classId, aId );
      name = aName;
      descr = aDescr;

      DtoRefbookInfo rbInfo = DtoRefbookInfo.of( aRefbook );
      for( IDtoAttrInfo ai : rbInfo.attrInfos() ) {
        attrValues.put( ai.id(), IAtomicValue.NULL );
      }
    }

    void setValue( String aFieldId, IAtomicValue aValue ) {
      attrValues.put( aFieldId, aValue );
    }

    IDtoFullObject buildItem() {
      IDtoObject dtoObj = new DtoObject( skid, attrValues, IStringMap.EMPTY );
      DtoFullObject retVal = new DtoFullObject( dtoObj, IStringMap.EMPTY, IStringMap.EMPTY );
      retVal.attrs().setStr( TSID_NAME, name );
      retVal.attrs().setStr( TSID_DESCRIPTION, descr );
      return retVal;
    }
  }

  /**
   * server connection
   */
  private final ISkConnection conn;

  // y.scale - описание Y шкалы
  public static String RBID_Y_SCALE                = "y.scale";
  public static String RBATRID_Y_SCALE___ID        = "scaleId"; // id шкалы
  public static String RBATRID_Y_SCALE___UNIT_NAME = "name";    // Единицы измерения
  public static String RBATRID_Y_SCALE___FORMAT    = "format";  // Формат отображения значений шкалы
  public static String RBATRID_Y_SCALE___MIN       = "minVal";  // Мин. начальное значение
  public static String RBATRID_Y_SCALE___MAX       = "maxVal";  // Макс. начальное значение

  public static String ITEMID_Y_SCALE___TEMPERATURE = "Temperature"; // scale for temperature
  public static String ITEMID_Y_SCALE___VIBRATION   = "Vibration";   // scale for vibration
  public static String ITEMID_Y_SCALE___PRESSURE    = "Pressure";    // scale for pressure
  public static String ITEMID_Y_SCALE___FLOW        = "Flow";        // scale for flow

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_Y_SCALE___SCALEID}.
   */
  static IDtoAttrInfo ATRINF_Y_SCALE_ID = DtoAttrInfo.create2( RBATRID_Y_SCALE___ID, DT_STRING, //
      TSID_NAME, STR_Y_SCALE_ID, //
      TSID_DESCRIPTION, STR_Y_SCALE_ID_D, //
      TSID_DEFAULT_VALUE, avStr( "T" ) // //$NON-NLS-1$
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_Y_SCALE___NAME}.
   */
  static IDtoAttrInfo ATRINF_Y_SCALE_NAME = DtoAttrInfo.create2( RBATRID_Y_SCALE___UNIT_NAME, DT_STRING, //
      TSID_NAME, STR_Y_SCALE_UNIT_NAME, //
      TSID_DESCRIPTION, STR_Y_SCALE_UNIT_NAME_D //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_Y_SCALE___FORMAT}.
   */
  static IDtoAttrInfo ATRINF_FORMAT = DtoAttrInfo.create2( RBATRID_Y_SCALE___FORMAT, //
      DataType.create( VALOBJ, //
          TSID_KEEPER_ID, EDisplayFormat.KEEPER_ID, //
          TSID_DEFAULT_VALUE, avValobj( EDisplayFormat.TWO_DIGIT ) //
      ), // , //
      TSID_NAME, STR_Y_SCALE_FORMAT, //
      TSID_DESCRIPTION, STR_Y_SCALE_FORMAT_D //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_Y_SCALE___MIN}.
   */
  static IDtoAttrInfo ATRINF_MIN = DtoAttrInfo.create2( RBATRID_Y_SCALE___MIN, DT_FLOATING, //
      TSID_NAME, STR_Y_SCALE_MIN, //
      TSID_DESCRIPTION, STR_Y_SCALE_MIN_D //
  );

  /**
   * Attribute {@link ISkRefbook#attrs() #RBATRID_Y_SCALE___MAX}.
   */
  static IDtoAttrInfo ATRINF_MAX = DtoAttrInfo.create2( RBATRID_Y_SCALE___MAX, DT_FLOATING, //
      TSID_NAME, STR_Y_SCALE_MAX, //
      TSID_DESCRIPTION, STR_Y_SCALE_MAX_D //
  );

  /**
   * Refbook: Вертикальные шкалы.
   * <p>
   */
  public static IDtoRefbookInfo REFBOOK_Y_SCALES = DtoRefbookInfo.create( RBID_Y_SCALE, ///
      OptionSetUtils.createOpSet( ///
          TSID_NAME, "Y шкалы", //
          TSID_DESCRIPTION, "Вертикальные шкалы для графиков" //
      ), ///
      new StridablesList<>( ///
          ATRINF_Y_SCALE_ID, //
          ATRINF_Y_SCALE_NAME, //
          ATRINF_FORMAT, //
          ATRINF_MIN, //
          ATRINF_MAX //
      ), //
      new StridablesList<>( ///
      // no CLOBs
      ), ///
      new StridablesList<>( ///
      // no rivets
      ), ///
      new StridablesList<>( ///
      // no links
      ) ///
  );

  /**
   * Constructor.
   *
   * @param aConn - server
   */
  public YScaleRefbookGenerator( ISkConnection aConn ) {
    conn = aConn;
  }

  /**
   * Create refbook Y scales
   */
  @SuppressWarnings( "nls" )
  public void createYScalesRb() {
    ISkRefbookService rbServ = conn.coreApi().getService( ISkRefbookService.SERVICE_ID );

    // create refbook
    ISkRefbook rbTranslators = rbServ.defineRefbook( REFBOOK_Y_SCALES );
    // fill refbook
    addYScaleRbItem( rbTranslators, ITEMID_Y_SCALE___TEMPERATURE, "Temperature", "Temperature Y scale", "T", "°С",
        EDisplayFormat.ONE_DIGIT, 0, 100 );
    addYScaleRbItem( rbTranslators, ITEMID_Y_SCALE___VIBRATION, "Vibration", "Vibration Y scale", "V", "мм/с",
        EDisplayFormat.ONE_DIGIT, 0, 20 );
    addYScaleRbItem( rbTranslators, ITEMID_Y_SCALE___PRESSURE, "Pressure", "Pressure Y scale", "P", "кГс/см²",
        EDisplayFormat.TWO_DIGIT, 0, 10 );
    addYScaleRbItem( rbTranslators, ITEMID_Y_SCALE___FLOW, "Flow", "Flow Y scale", "F", "м³/ч",
        EDisplayFormat.AS_INTEGER, 0, 20000 );
  }

  private static void addYScaleRbItem( ISkRefbook aRefbook, String aItemId, String aName, String aDescr,
      String aScaleId, String aScaleName, EDisplayFormat aDisplayFormat, float aMin, float aMax ) {
    String rbItemId = aItemId;
    RefookItemBuider b = new RefookItemBuider( rbItemId, aName, aDescr, aRefbook );
    b.setValue( RBATRID_Y_SCALE___ID, AvUtils.avStr( aScaleId ) );
    b.setValue( RBATRID_Y_SCALE___UNIT_NAME, AvUtils.avStr( aScaleName ) );
    b.setValue( RBATRID_Y_SCALE___FORMAT, AvUtils.avValobj( aDisplayFormat ) );
    b.setValue( RBATRID_Y_SCALE___MIN, AvUtils.avFloat( aMin ) );
    b.setValue( RBATRID_Y_SCALE___MAX, AvUtils.avFloat( aMax ) );
    IDtoFullObject rbItem = b.buildItem();
    aRefbook.defineItem( rbItem );
  }

}
