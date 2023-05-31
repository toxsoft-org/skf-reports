package org.toxsoft.skf.reports.skide.utils;

import static org.toxsoft.skf.reports.skide.ISkidePluginReportsConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.skf.reports.gui.km5.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.dto.*;

/**
 * Migration utils to move reports templates from one sk to another.
 *
 * @author Max
 */
public class ReportsTemplatesMigrationUtiles {

  private ReportsTemplatesMigrationUtiles() {

  }

  /**
   * Перемещает объекты шаблонов отчётов (со всеми потрохами) из одного соединения в другое
   *
   * @param aSrcContext ITsGuiContext - контекст исходного соединения.
   * @param aDestConnection ISkConnection - целевое соединение.
   */
  public static void moveReportTemplates( ITsGuiContext aSrcContext, ISkConnection aDestConnection ) {

    ITsGuiContext srcCtx = new TsGuiContext( aSrcContext );

    ISkConnectionSupplier connSup = srcCtx.get( ISkConnectionSupplier.class );
    ISkConnection srcConnection = connSup.defConn();

    IM5Domain m5 = srcConnection.scope().get( IM5Domain.class );

    KM5ModelBasic<IVtReportTemplate> model =
        new KM5ModelBasic<>( REPORTS_TEMPORARY_MODEL_ID, IVtReportTemplate.class, srcConnection );
    model.setNameAndDescription( STR_MODEL_REPORT, STR_MODEL_REPORT_D );
    model.addFieldDefs( model.NAME, model.DESCRIPTION );
    m5.initTemporaryModel( model );

    IM5LifecycleManager<IVtReportTemplate> lm = new ReportTemplateM5LifecycleManager( model, srcConnection );

    TsDialogInfo di = new TsDialogInfo( srcCtx, STR_EXPORT_TEMPLATE_DIALOG, STR_EXPORT_TEMPLATE_DIALOG_D );
    IList<IVtReportTemplate> selectedTemplates =
        M5GuiUtils.askSelectItemsList( di, model, new ElemArrayList<IVtReportTemplate>(), lm.itemsProvider() );

    if( selectedTemplates == null ) {
      return;
    }

    try {
      for( IVtReportTemplate template : selectedTemplates ) {
        DtoFullObject objDto = DtoFullObject.createDtoFullObject( template.skid(), srcConnection.coreApi() );

        IVtReportTemplateService reportTemplateService =
            aDestConnection.coreApi().getService( IVtReportTemplateService.SERVICE_ID );
        reportTemplateService.createReportTemplate( objDto );
      }
      TsDialogUtils.info( aSrcContext.get( Shell.class ), STR_EXPORT_COMPLETE_DIALOG );
    }
    catch( Exception e ) {
      TsDialogUtils.error( aSrcContext.get( Shell.class ), e );
    }
  }

  /**
   * Перемещает объекты шаблонов графиков (со всеми потрохами) из одного соединения в другое
   *
   * @param aSrcContext ITsGuiContext - контекст исходного соединения.
   * @param aDestConnection ISkConnection - целевое соединение.
   */
  public static void moveGraphTemplates( ITsGuiContext aSrcContext, ISkConnection aDestConnection ) {

    ITsGuiContext srcCtx = new TsGuiContext( aSrcContext );

    ISkConnectionSupplier connSup = srcCtx.get( ISkConnectionSupplier.class );
    ISkConnection srcConnection = connSup.defConn();

    IM5Domain m5 = srcConnection.scope().get( IM5Domain.class );

    KM5ModelBasic<IVtGraphTemplate> model =
        new KM5ModelBasic<>( GRAPHS_TEMPORARY_MODEL_ID, IVtGraphTemplate.class, srcConnection );
    model.setNameAndDescription( STR_MODEL_GRAPH, STR_MODEL_GRAPH_D );
    model.addFieldDefs( model.NAME, model.DESCRIPTION );
    m5.initTemporaryModel( model );

    IM5LifecycleManager<IVtGraphTemplate> lm = new GraphTemplateM5LifecycleManager( model, srcConnection );

    TsDialogInfo di = new TsDialogInfo( srcCtx, STR_EXPORT_TEMPLATE_DIALOG, STR_EXPORT_TEMPLATE_DIALOG_D );
    IList<IVtGraphTemplate> selectedTemplates =
        M5GuiUtils.askSelectItemsList( di, model, new ElemArrayList<IVtGraphTemplate>(), lm.itemsProvider() );

    if( selectedTemplates == null ) {
      return;
    }

    try {
      for( IVtGraphTemplate template : selectedTemplates ) {
        DtoFullObject objDto = DtoFullObject.createDtoFullObject( template.skid(), srcConnection.coreApi() );

        IVtGraphTemplateService reportTemplateService =
            aDestConnection.coreApi().getService( IVtGraphTemplateService.SERVICE_ID );
        reportTemplateService.createGraphTemplate( objDto );
      }
      TsDialogUtils.info( aSrcContext.get( Shell.class ), STR_EXPORT_COMPLETE_DIALOG );
    }
    catch( Exception e ) {
      TsDialogUtils.error( aSrcContext.get( Shell.class ), e );
    }
  }
}
