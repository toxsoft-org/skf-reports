package org.toxsoft.skf.reports.gui.km5;

import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skf.reports.templates.service.impl.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * Lifecycle manager for {@link SpecReportParamM5Model}.
 *
 * @author max
 */
class SpecReportParamM5LifecycleManager
    extends M5LifecycleManager<IVtSpecReportParam, ISkConnection> {

  public SpecReportParamM5LifecycleManager( IM5Model<IVtSpecReportParam> aModel, ISkConnection aMaster ) {
    super( aModel, true, true, true, false, aMaster );
  }

  /**
   * Subclass may perform validation before instance creation.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IVtSpecReportParam> aValues ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If creation is supported subclass must create the entity instance.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return &lt;IVtReportParam&gt; - created instance
   */
  @Override
  protected IVtSpecReportParam doCreate( IM5Bunch<IVtSpecReportParam> aValues ) {
    EJrParamSourceType gwidKind = aValues.getAsAv( SpecReportParamM5Model.FID_GWID_KIND ).asValobj();
    IAtomicValue gwidAv = aValues.getAsAv( SpecReportParamM5Model.FID_GWID );
    Gwid gwid = gwidAv.isAssigned() ? gwidAv.asValobj() : null;

    String title = aValues.getAsAv( SpecReportParamM5Model.FID_TITLE ).asString();
    String descr = aValues.getAsAv( SpecReportParamM5Model.FID_DESCR ).asString();
    EAggregationFunc func = aValues.getAsAv( SpecReportParamM5Model.FID_AGGR_FUNC ).asValobj();
    EDisplayFormat format = aValues.getAsAv( SpecReportParamM5Model.FID_DISPL_FORMAT ).asValobj();

    String jrParam = aValues.get( SpecReportParamM5Model.FID_JR_PARAM );
    String value = aValues.getAsAv( SpecReportParamM5Model.FID_PRESET_VALUE ).asString();
    boolean canBeOverwritten = aValues.getAsAv( SpecReportParamM5Model.FID_FLAG_OVERRIDE_VALUE ).asBool();

    return new VtSpecReportParam( gwidKind, gwid, title, descr, func, format, jrParam, value, canBeOverwritten );
  }

  /**
   * Subclass may perform validation before existing editing.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<IVtSpecReportParam> aValues ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If editing is supported subclass must edit the existing entity.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   * <p>
   * The old values may be found in the {@link IM5Bunch#originalEntity()} which obviously is not <code>null</code>.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return &lt;IVtReportParam&gt; - created instance
   */
  @Override
  protected IVtSpecReportParam doEdit( IM5Bunch<IVtSpecReportParam> aValues ) {
    EJrParamSourceType gwidKind = aValues.getAsAv( SpecReportParamM5Model.FID_GWID_KIND ).asValobj();
    IAtomicValue gwidAv = aValues.getAsAv( SpecReportParamM5Model.FID_GWID );
    Gwid gwid = gwidAv.isAssigned() ? gwidAv.asValobj() : null;

    String title = aValues.getAsAv( SpecReportParamM5Model.FID_TITLE ).asString();
    String descr = aValues.getAsAv( SpecReportParamM5Model.FID_DESCR ).asString();
    EAggregationFunc func = aValues.getAsAv( SpecReportParamM5Model.FID_AGGR_FUNC ).asValobj();
    EDisplayFormat format = aValues.getAsAv( SpecReportParamM5Model.FID_DISPL_FORMAT ).asValobj();

    String jrParam = aValues.get( SpecReportParamM5Model.FID_JR_PARAM );
    String value = aValues.getAsAv( SpecReportParamM5Model.FID_PRESET_VALUE ).asString();
    boolean canBeOverwritten = aValues.getAsAv( SpecReportParamM5Model.FID_FLAG_OVERRIDE_VALUE ).asBool();

    if( aValues.originalEntity().gwid().equals( gwid ) ) {
      gwidKind = aValues.originalEntity().jrParamSourceType();
    }

    return new VtSpecReportParam( gwidKind, gwid, title, descr, func, format, jrParam, value, canBeOverwritten );
  }

  /**
   * Subclass may perform validation before remove existing entity.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aEntity &lt;IVtReportParam&gt; - the entity to be removed, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeRemove( IVtSpecReportParam aEntity ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If removing is supported subclass must remove the existing entity.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   *
   * @param aEntity &lt;IVtReportParam&gt; - the entity to be removed, never is <code>null</code>
   */
  @Override
  protected void doRemove( IVtSpecReportParam aEntity ) {
    // TODO
  }

  /**
   * If enumeration is supported subclass must return list of entities.
   * <p>
   * In base class returns {@link IList#EMPTY}, there is no need to call superclass method when overriding.
   *
   * @return {@link IList}&lt;IVtReportParam&gt; - list of entities in the scope of maetr object
   */
  @Override
  protected IList<IVtSpecReportParam> doListEntities() {
    return IList.EMPTY;
  }

  /**
   * If enumeration is supported subclass may allow items reordering.
   * <p>
   * In base class returns <code>null</code>, there is no need to call superclass method when overriding.
   * <p>
   * This method is called every time when user asks for {@link IM5ItemsProvider#reorderer()}.
   *
   * @return {@link IListReorderer}&lt;IVtReportParam&gt; - optional {@link IM5ItemsProvider#listItems()} reordering
   *         means
   */
  @Override
  protected IListReorderer<IVtSpecReportParam> doGetItemsReorderer() {
    return null;
  }
}
