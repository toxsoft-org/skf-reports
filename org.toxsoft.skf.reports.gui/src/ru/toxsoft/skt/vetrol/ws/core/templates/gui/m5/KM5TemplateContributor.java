package ru.toxsoft.skt.vetrol.ws.core.templates.gui.m5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.reports.templates.service.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * Contributes M5-models for template entities.
 *
 * @author dima
 */
public class KM5TemplateContributor
    extends KM5AbstractContributor {

  /**
   * Creator singleton.
   */
  public static final IKM5ContributorCreator CREATOR = KM5TemplateContributor::new;

  private static final IStringList CONRTIBUTED_MODEL_IDS = new StringArrayList( //
      VtReportParamM5Model.MODEL_ID, //
      IVtReportTemplate.CLASS_ID, //
      IVtTemplateEditorServiceHardConstants.GRAPH_PARAM_MODEL_ID, //
      IVtGraphTemplate.CLASS_ID //
  );

  private final IStringListEdit myModels = new StringArrayList();

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @param aDomain {@link IM5Domain} - connection domain
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public KM5TemplateContributor( ISkConnection aConn, IM5Domain aDomain ) {
    super( aConn, aDomain );
  }

  @Override
  protected IStringList papiCreateModels() {
    m5().addModel( new VtReportParamM5Model() );
    VtReportTemplateM5Model m5ReportTemplateModel = new VtReportTemplateM5Model( skConn() );
    myModels.add( m5ReportTemplateModel.id() );
    m5().addModel( m5ReportTemplateModel );
    m5().addModel( new VtGraphParamM5Model() );
    VtGraphTemplateM5Model m5GraphTemplateModel = new VtGraphTemplateM5Model( skConn() );
    myModels.add( m5GraphTemplateModel.id() );
    m5().addModel( m5GraphTemplateModel );

    return CONRTIBUTED_MODEL_IDS;
  }

  // jsut for one more rebuild 3...
  // GOGA 2023-01-04
  // не нужно отрабатывать изменения в Sysdescr, поскольку классы CONRTIBUTED_MODEL_IDS не меняются на ходу
  // @Override
  // protected void papiUpdateModel( ECrudOp aOp, String aClassId ) {
  // switch( aOp ) {
  // case CREATE:
  // case EDIT: {
  // if( isMine( aClassId ) ) {
  // m5().replaceModel( mineModel( aClassId ) );
  // }
  // break;
  // }
  // case REMOVE: {
  // m5().removeModel( aClassId );
  // break;
  // }
  // case LIST: {
  //
  // // FIXME what to do here?
  //
  // break;
  // }
  // default:
  // throw new TsNotAllEnumsUsedRtException();
  // }
  // }
  //
  // private M5Model<?> mineModel( String aClassId ) {
  // M5Model<?> retVal = new VtReportTemplateM5Model( skConn() );
  // switch( aClassId ) {
  // case IVtReportTemplate.CLASS_ID: {
  // break;
  // }
  // case IVtGraphTemplate.CLASS_ID: {
  // retVal = new VtGraphTemplateM5Model( skConn() );
  // break;
  // }
  // default:
  // throw new TsNotAllEnumsUsedRtException( aClassId );
  // }
  // return retVal;
  // }
  //
  // private boolean isMine( String aClassId ) {
  // return myModels.hasElem( aClassId );
  // }

}
