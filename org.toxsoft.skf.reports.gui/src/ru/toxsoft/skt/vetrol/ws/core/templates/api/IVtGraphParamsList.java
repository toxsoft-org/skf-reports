package ru.toxsoft.skt.vetrol.ws.core.templates.api;

import org.toxsoft.core.tslib.coll.*;

/**
 * Interface to specify list parameters for template of graph report. <br>
 *
 * @author dima
 */

public interface IVtGraphParamsList {

  /**
   * @return list of template parameters
   */
  IList<IVtGraphParam> items();

}
