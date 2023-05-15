package org.toxsoft.skf.reports.gui;

import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.users.*;

/**
 * Утилитный класс для работы с соединением
 *
 * @author Max
 */
public class ConnectionUtiles {

  private ConnectionUtiles() {

  }

  /**
   * TODO Заглушка!!!! Возвращает root Возвращает пользователя подключенного к серверу.
   *
   * @param aCoreApi {@link ISkCoreApi} API соединения
   * @return {@link ISkUser} пользователь подключенный к серверу
   * @throws TsNullArgumentRtException аргумент = null
   * @throws TsIllegalStateRtException нет активного соединения с сервером
   */
  public static ISkUser getConnectedUser( ISkCoreApi aCoreApi ) {
    TsNullArgumentRtException.checkNull( aCoreApi );

    return aCoreApi.userService().getUser( "root" );
  }
}
