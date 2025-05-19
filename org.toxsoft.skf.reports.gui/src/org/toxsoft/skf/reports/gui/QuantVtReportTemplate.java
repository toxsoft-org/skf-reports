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
import org.toxsoft.uskat.core.api.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * The libtary quant.
 *
 * @author hazard157
 */
public class QuantVtReportTemplate
    extends AbstractQuant
    implements ISkCoreExternalHandler {

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
    // register as initialize listener
    SkCoreUtils.registerCoreApiHandler( this );
  }

  @Override
  protected void doInitApp( IEclipseContext aWinContext ) {
    // nop
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    IReportsGuiConstants.init( aWinContext );

    ValedControlFactoriesRegistry vcReg = aWinContext.get( ValedControlFactoriesRegistry.class );
    if( !vcReg.hasFactory( ValedGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedAvValobjGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedAvValobjGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedSkidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedSkidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( ValedAvValobjSkidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( ValedAvValobjSkidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( SpecValedAvValobjGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( SpecValedAvValobjGwidEditor.FACTORY );
    }
    if( !vcReg.hasFactory( SpecValedGwidEditor.FACTORY_NAME ) ) {
      vcReg.registerFactory( SpecValedGwidEditor.FACTORY );
    }
  }

  @Override
  public void processSkCoreInitialization( IDevCoreApi aCoreApi ) {
    // register abilities
    aCoreApi.userService().abilityManager().defineAbility( IReportsGuiConstants.ABILITY_ACCESS_TEMPLATE_EDITOR );
  }

}
