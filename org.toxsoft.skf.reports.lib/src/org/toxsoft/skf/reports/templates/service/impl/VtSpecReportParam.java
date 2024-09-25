package org.toxsoft.skf.reports.templates.service.impl;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.reports.templates.service.*;

/**
 * {@link IVtSpecReportParam} immutable implementation.
 *
 * @author max
 */
public class VtSpecReportParam
    implements IVtSpecReportParam {

  private static final String STR_NULL_GWID = "Null"; //$NON-NLS-1$

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "VtSpecReportParam"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<IVtSpecReportParam> KEEPER =
      new AbstractEntityKeeper<>( IVtSpecReportParam.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, IVtSpecReportParam aEntity ) {
          // тип gwid
          aSw.writeAsIs( aEntity.jrParamSourceType().id() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем Gwid
          aSw.writeQuotedString( aEntity.gwid() != null ? aEntity.gwid().canonicalString() : STR_NULL_GWID );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // name
          aSw.writeQuotedString( aEntity.title() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // description
          aSw.writeQuotedString( aEntity.description() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // функция агрегации
          aSw.writeAsIs( aEntity.aggrFunc().id() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // формат отображения
          aSw.writeAsIs( aEntity.displayFormat().id() );

          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // jrParamId
          aSw.writeQuotedString( aEntity.jrParamId() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // value
          aSw.writeQuotedString( aEntity.value() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // canBeOverwritten
          aSw.writeBoolean( aEntity.canBeOverwritten() );
        }

        @Override
        protected IVtSpecReportParam doRead( IStrioReader aSr ) {
          // тип gwid
          EJrParamSourceType gwidType = EJrParamSourceType.getById( aSr.readIdName() );
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String gwidStr = aSr.readQuotedString();
          Gwid gwid = gwidStr.equals( STR_NULL_GWID ) ? null : Gwid.of( gwidStr );
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String title = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String descr = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          EAggregationFunc aggrFunc = EAggregationFunc.getById( aSr.readIdName() );
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          EDisplayFormat dispFormat = EDisplayFormat.getById( aSr.readIdName() );

          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String jrParamId = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          String value = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          boolean canBeOverwritten = aSr.readBoolean();

          return new VtSpecReportParam( gwidType, gwid, title, descr, aggrFunc, dispFormat, jrParamId, value,
              canBeOverwritten );
        }
      };

  protected final Gwid               gwid;
  protected final String             title;
  protected final String             description;
  protected final EAggregationFunc   aggrFunc;
  protected final EDisplayFormat     dispFormat;
  protected final String             jrParamId;
  protected final String             value;
  protected final boolean            canBeOverwritten;
  protected final EJrParamSourceType jrParamSourceType;

  /**
   * Constructor.
   *
   * @param aJrParamSourceType EJrParamSourceType - type of gwid
   * @param aGwid {@link Gwid} green world id of that parameter
   * @param aTitle name of parameter
   * @param aDescr description of parameter
   * @param aAggrFunc {@link EAggregationFunc} aggregation func
   * @param aDispFormat {@link EDisplayFormat} display format
   * @param aJrParamId String - param id
   * @param aValue String - preset value of param
   * @param aCanBeOverwritten boolean - flag indicating posibility of overwritten
   */
  public VtSpecReportParam( EJrParamSourceType aJrParamSourceType, Gwid aGwid, String aTitle, String aDescr,
      EAggregationFunc aAggrFunc, EDisplayFormat aDispFormat, String aJrParamId, String aValue,
      boolean aCanBeOverwritten ) {
    jrParamSourceType = aJrParamSourceType;
    gwid = aGwid;
    title = aTitle;
    description = aDescr;
    aggrFunc = aAggrFunc;
    dispFormat = aDispFormat;
    jrParamId = aJrParamId;
    value = aValue;
    canBeOverwritten = aCanBeOverwritten;
  }

  // ------------------------------------------------------------------------------------
  // API
  //

  @Override
  public Gwid gwid() {
    return gwid;
  }

  @Override
  public EAggregationFunc aggrFunc() {
    return aggrFunc;
  }

  @Override
  public EDisplayFormat displayFormat() {
    return dispFormat;
  }

  @Override
  public String title() {
    return title;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public String jrParamId() {
    return jrParamId;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public boolean canBeOverwritten() {
    return canBeOverwritten;
  }

  @Override
  public EJrParamSourceType jrParamSourceType() {
    return jrParamSourceType;
  }

}
