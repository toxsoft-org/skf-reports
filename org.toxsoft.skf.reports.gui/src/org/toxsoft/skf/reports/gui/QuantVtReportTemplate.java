package org.toxsoft.skf.reports.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.chart.api.*;
import org.toxsoft.core.tsgui.valed.impl.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.reports.gui.km5.*;
import org.toxsoft.skf.reports.gui.panels.valed.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.skf.reports.templates.service.impl.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * The libtary quant.
 *
 * @author hazard157
 */
public class QuantVtReportTemplate
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantVtReportTemplate() {
    super( QuantVtReportTemplate.class.getSimpleName() );

    SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );
    SkCoreUtils.registerSkServiceCreator( VtSpecReportTemplateService.CREATOR );

    // dima 26.05.23 падает в этом месте если запускать в SkIDE вместе с другими плагинами
    // max 29.05.23 поставил
    TsValobjUtils.registerKeeperIfNone( EAggregationFunc.KEEPER_ID, EAggregationFunc.KEEPER );
    TsValobjUtils.registerKeeperIfNone( EDisplayFormat.KEEPER_ID, EDisplayFormat.KEEPER );
    TsValobjUtils.registerKeeperIfNone( ETimeUnit.KEEPER_ID, ETimeUnit.KEEPER );
    TsValobjUtils.registerKeeperIfNone( VtGraphParamsList.KEEPER_ID, VtGraphParamsList.KEEPER );

    TsValobjUtils.registerKeeperIfNone( EJrParamSourceType.KEEPER_ID, EJrParamSourceType.KEEPER );

    KM5Utils.registerContributorCreator( KM5TemplateContributor.CREATOR );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {
    // IVtWsCoreConstants.init( aWinContext );

    // SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    // SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );

    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    //
    // // регистрируем свои m5 модели
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new VtReportParamM5Model() );
    // m5.addModel( new VtReportTemplateM5Model( conn ) );
    // m5.addModel( new VtGraphParamM5Model() );
    // m5.addModel( new VtGraphTemplateM5Model( conn ) );

    // ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    // vcReg.registerFactory( ValedGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    // vcReg.registerFactory( ValedSkidEditor.FACTORY );
    // vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IReportsGuiConstants.init( aWinContext );

    // SkCoreUtils.registerSkServiceCreator( VtReportTemplateService.CREATOR );
    // SkCoreUtils.registerSkServiceCreator( VtGraphTemplateService.CREATOR );

    // ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    // ISkConnection conn = connSup.defConn();
    //
    // // регистрируем свои m5 модели
    // IM5Domain m5 = aWinContext.get( IM5Domain.class );
    // m5.addModel( new VtReportParamM5Model() );
    // m5.addModel( new VtReportTemplateM5Model( conn ) );
    // m5.addModel( new VtGraphParamM5Model() );
    // m5.addModel( new VtGraphTemplateM5Model( conn ) );

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    vcReg.registerFactory( ValedGwidEditor.FACTORY );
    vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    vcReg.registerFactory( ValedSkidEditor.FACTORY );
    vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );

    vcReg.registerFactory( SpecValedAvValobjGwidEditor.FACTORY );
    vcReg.registerFactory( SpecValedGwidEditor.FACTORY );
  }

}
