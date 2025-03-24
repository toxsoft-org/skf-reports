package org.toxsoft.skf.reports.skide.utils;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.bricks.gentask.IGenericTaskConstants.*;
import static org.toxsoft.skf.reports.skide.ISkidePluginReportsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.plugin.exconn.main.UploadToServerTaskProcessor.*;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.skide.main.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.tasks.*;
import org.toxsoft.skide.plugin.exconn.main.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * SkIDE task {@link UploadToServerTaskProcessor} runner for {@link SkideUnitReports}.
 *
 * @author dima
 */
public class TaskTemplatesUpload
    extends AbstractSkideUnitTaskSync {

  static final String OPID_TEMPLATES_DOWNLOAD = SKIDE_FULL_ID + ".TemplatesDownload"; //$NON-NLS-1$

  static final IDataDef OPDEF_TEMPLATES_DOWNLOAD = DataDef.create( OPID_TEMPLATES_DOWNLOAD, BOOLEAN, //
      TSID_NAME, STR_TEMPLATES_DOWNLOAD, //
      TSID_DESCRIPTION, STR_TEMPLATES_DOWNLOAD_D, //
      TSID_DEFAULT_VALUE, AV_FALSE //
  );

  /**
   * Constructor.
   *
   * @param aOwnerUnit {@link AbstractSkideUnit} - the owner unit
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public TaskTemplatesUpload( AbstractSkideUnit aOwnerUnit ) {
    super( aOwnerUnit, UploadToServerTaskProcessor.INSTANCE.taskInfo(), // configuration options
        new StridablesList<>( //
            OPDEF_TEMPLATES_DOWNLOAD //
        ) );
  }

  @Override
  protected void doRunSync( ITsContextRo aInput, ITsContext aOutput ) {
    ILongOpProgressCallback lop = REFDEF_IN_PROGRESS_MONITOR.getRef( aInput );
    String lopMessage = MSG_TEMPLATES_UPLOAD;
    ISkConnection srcConn = tsContext().get( ISkConnectionSupplier.class ).defConn();
    ISkConnection destConn = REFDEF_IN_OPEN_SK_CONN.getRef( aInput );
    boolean isDownload = OPDEF_TEMPLATES_DOWNLOAD.getValue( getCfgOptionValues() ).asBool();
    if( isDownload ) {
      lopMessage = MSG_TEMPLATES_DOWNLOAD;
      // reverse direction
      ISkConnection tmp = destConn;
      destConn = srcConn;
      srcConn = tmp;
    }
    // Начинаем процесс переноса
    lop.startWork( lopMessage, true );
    int transferedReportTemplatesCount = ReportsTemplatesMigrationUtiles.moveReportTemplates( srcConn, destConn );
    int transferedGraphTemplatesCount = ReportsTemplatesMigrationUtiles.moveGraphTemplates( srcConn, destConn );
    lop.finished( ValidationResult.info( isDownload ? FMT_TEMPLATES_DOWNLOADED : FMT_TEMPLATES_UPLOADED,
        Integer.valueOf( transferedReportTemplatesCount ), Integer.valueOf( transferedGraphTemplatesCount ) ) );
  }

}
