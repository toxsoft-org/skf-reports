package ru.toxsoft.skt.vetrol.ws.core.templates.api.impl;

import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.EEncloseMode;
import org.toxsoft.core.tslib.bricks.keeper.IEntityKeeper;
import org.toxsoft.core.tslib.bricks.strio.IStrioReader;
import org.toxsoft.core.tslib.bricks.strio.IStrioWriter;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.coll.impl.ElemArrayList;

import ru.toxsoft.skt.vetrol.ws.core.templates.api.*;

/**
 * {@link IVtGraphParamsList} mutable implementation.
 *
 * @author dima
 */
public final class VtGraphParamsList
    implements IVtGraphParamsList {

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "VtGraphParamsList"; //$NON-NLS-1$

  /**
   * Keeper of list.
   */
  public static final IEntityKeeper<IVtGraphParamsList> KEEPER =
      new AbstractEntityKeeper<>( IVtGraphParamsList.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, IVtGraphParamsList aEntity ) {
          VtGraphParam.KEEPER.writeColl( aSw, aEntity.items(), false );
        }

        @Override
        protected IVtGraphParamsList doRead( IStrioReader aSr ) {
          return new VtGraphParamsList( VtGraphParam.KEEPER.readColl( aSr ) );
        }
      };

  private final IListEdit<IVtGraphParam> items = new ElemArrayList<>();

  /**
   * Constructor.
   */
  public VtGraphParamsList() {
    // nop
  }

  /**
   * Constructor.
   *
   * @param aList {@link IVtGraphParam} список параметров
   */
  public VtGraphParamsList( IList<IVtGraphParam> aList ) {
    items.addAll( aList );
  }

  @Override
  public IListEdit<IVtGraphParam> items() {
    return items;
  }
}
