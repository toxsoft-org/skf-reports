package org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl;

import static org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl.ISkResources.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.skf.ggprefs.lib.*;
import org.toxsoft.skf.reports.gui.rtchart.data_aliases.*;

/**
 * Модель используемая при редактировании списка data aliases
 *
 * @author dima
 */
public class GuiDataAliasesPrefsEditModel
    extends M5Model<Skid> {

  /**
   * Идентификатор модели.
   */
  public static final String MODEL_ID =
      "ru.toxsoft.ci.ws.mnemos.app.rt.chart.data_aliases.impl.GuiDataAliasesPrefsEditModel"; //$NON-NLS-1$

  /**
   * Идентификатор модели списка data aliases
   */
  public static final String DATA_ALIASES_MODEL_ID = "data.aliases.list.model"; //$NON-NLS-1$

  private IGuiGwPrefsSection prefsSection;

  /**
   * Конструктор модели редактирования опций gui объекта
   *
   * @param aPrefsSection - редактируемый раздел
   * @param aObjSkid - идентификатор gui объекта
   */
  public GuiDataAliasesPrefsEditModel( IGuiGwPrefsSection aPrefsSection, Skid aObjSkid ) {
    super( MODEL_ID, Skid.class );

    prefsSection = aPrefsSection;

    IStridablesList<IDataDef> optDefs = prefsSection.listOptionDefs( aObjSkid );

    IListEdit<IM5FieldDef<?, ?>> fDefs = new ElemArrayList<>();

    // формируем поля - по одному на опцию
    for( IDataDef optDef : optDefs ) {
      fDefs.add( createFieldDef( optDef ) );
    }

    addFieldDefs( fDefs.toArray( new IM5FieldDef[0] ) );

  }

  private IM5FieldDef<?, ?> createFieldDef( IDataDef optDef ) {

    IM5MultiModownFieldDef<Skid, IDataNameAlias> fDef =
        new M5MultiModownFieldDef<>( DATA_ALIASES_MODEL_ID, DataAliasM5Model.MODEL_ID ) {

          @Override
          protected void doInit() {
            setNameAndDescription( STR_DATA_ALIAS, STR_DATA_ALIAS_D );
          }

          @Override
          protected IList<IDataNameAlias> doGetFieldValue( Skid aEntity ) {
            IOptionSet values = prefsSection.getOptions( aEntity );
            IDataNameAliasesList list = (IDataNameAliasesList)values.getValue( optDef.id() ).asValobj();
            return list.items();
          }

        };

    return fDef;
  }

}
