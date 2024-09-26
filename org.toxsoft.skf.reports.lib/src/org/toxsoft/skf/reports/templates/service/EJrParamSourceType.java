package org.toxsoft.skf.reports.templates.service;

import static org.toxsoft.skf.reports.templates.service.IVtResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Type of JasperReport param (field) data source type ( history data, rri, attribute, and so on)
 *
 * @author max
 */
public enum EJrParamSourceType
    implements IStridable {

  /**
   * History of rt data
   */
  RTDATA( "rtdata", STR_N_DATA ), //$NON-NLS-1$

  /**
   * Value of attribure of object
   */
  ATTRIBURES( "attributes", STR_N_ATTRIBUTES ), //$NON-NLS-1$

  /**
   * Value of rri attributte of object
   */
  RRI_ATTRIBUTES( "rri_attributes", STR_N_RRI_ATTRIBUTES ); //$NON-NLS-1$

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "EJrParamSourceType"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<EJrParamSourceType> KEEPER = new StridableEnumKeeper<>( EJrParamSourceType.class );

  private static IStridablesListEdit<EJrParamSourceType> list = null;

  private final String id;
  private final String name;
  private final String description;

  EJrParamSourceType( String aId, String aName ) {
    id = aId;
    name = aName;
    description = aName;
  }

  // --------------------------------------------------------------------------
  // IStridable
  //

  @Override
  public String id() {
    return id;
  }

  @Override
  public String nmName() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  // ----------------------------------------------------------------------------------
  // API
  //

  /**
   * Returns all constants in single list.
   *
   * @return {@link IStridablesList}&lt; {@link EJrParamSourceType} &gt; - list of constants in order of declaraion
   */
  public static IStridablesList<EJrParamSourceType> asList() {
    if( list == null ) {
      list = new StridablesList<>( values() );
    }
    return list;
  }

  /**
   * Returns the constant by the ID.
   *
   * @param aId String - the ID
   * @return {@link EJrParamSourceType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified ID
   */
  public static EJrParamSourceType getById( String aId ) {
    return asList().getByKey( aId );
  }

  /**
   * Finds the constant by the name.
   *
   * @param aName String - the name
   * @return {@link EJrParamSourceType} - found constant or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static EJrParamSourceType findByName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    for( EJrParamSourceType item : values() ) {
      if( item.name.equals( aName ) ) {
        return item;
      }
    }
    return null;
  }

  /**
   * Returns the constant by the name.
   *
   * @param aName String - the name
   * @return {@link EJrParamSourceType} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified name
   */
  public static EJrParamSourceType getByName( String aName ) {
    return TsItemNotFoundRtException.checkNull( findByName( aName ) );
  }

}
