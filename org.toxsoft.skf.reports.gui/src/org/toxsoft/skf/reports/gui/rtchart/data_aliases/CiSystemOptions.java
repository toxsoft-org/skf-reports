package org.toxsoft.skf.reports.gui.rtchart.data_aliases;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.impl.DataDef.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.ggprefs.lib.IGuiGwPrefsConstants.*;
import static org.toxsoft.skf.reports.gui.rtchart.data_aliases.ISkResources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.skf.reports.gui.rtchart.data_aliases.impl.*;

/**
 * Параметры настроек панели RtChart .
 * <p>
 *
 * @author dima
 */
public class CiSystemOptions {

  /**
   * Идентификатор опции, содержащей имя
   */
  public static final String NAME_OPTION_ID = "band.name"; //$NON-NLS-1$

  /**
   * id группы "Ci System options"
   */
  static String CI_SYSTEM_OPT_GROUP_ID = "CiSystemOptions"; //$NON-NLS-1$

  /**
   * путь к группе
   */
  static String CI_SYSTEM_OPT_GROUP_PATH = "/ciSystemOptions/group"; //$NON-NLS-1$

  /**
   * описание группы
   */
  public static IDataDef GROUP_OPTION_DEF = create( CI_SYSTEM_OPT_GROUP_ID, EAtomicType.STRING, //
      TSID_NAME, STR_CI_SYSTEM_OPTIONS_GROUP, //
      TSID_DESCRIPTION, STR_CI_SYSTEM_OPTIONS_GROUP_D, //
      OPID_TREE_PATH1, CI_SYSTEM_OPT_GROUP_PATH, //
      TSID_DEFAULT_VALUE, avStr( TREE_PATH1_ROOT ) ); // путь к группе

  // ------------------------------------------------------------------------------------
  // параметры панели
  //

  /**
   * Data name aliases list <br>
   */
  public static final IDataDef DATA_NAME_ALIASES = DataDef.create( createId( "dataNameAliases" ), EAtomicType.VALOBJ, // //$NON-NLS-1$
      TSID_DESCRIPTION, STR_DATA_NAME_ALIASES_D, //
      TSID_NAME, STR_DATA_NAME_ALIASES, //
      TSID_KEEPER_ID, DataNameAliasesList.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( new DataNameAliasesList() ), //
      OPID_TREE_PATH1, CI_SYSTEM_OPT_GROUP_PATH );

  /**
   * Все опции в виде списка
   *
   * @return IStridablesListEdit&lt;IDataDef> - все опции в виде списка
   */
  public static IStridablesList<IDataDef> allOptions() {
    IStridablesListEdit<IDataDef> options = new StridablesList<>();
    options.add( DATA_NAME_ALIASES );
    return options;
  }

  /**
   * Возвращает набор опций, инициализированный набором значений по-умолчанию
   *
   * @return IOptionSet - набор опций, инициализированный набором значений по-умолчанию
   */
  public static IOptionSet defaultOptions() {
    IOptionSetEdit opSet = new OptionSet();
    opSet.setValue( DATA_NAME_ALIASES, DATA_NAME_ALIASES.defaultValue() );

    return opSet;
  }

  // ------------------------------------------------------------------------------------
  // Внутренняя реализация
  //

  static String createId( String aShortId ) {
    return CiSystemOptions.class.getSimpleName() + "." + aShortId; //$NON-NLS-1$
  }

}
