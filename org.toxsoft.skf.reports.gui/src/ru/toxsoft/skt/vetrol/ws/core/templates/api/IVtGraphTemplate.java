package ru.toxsoft.skt.vetrol.ws.core.templates.api;

/**
 * Interface to specify template of graph.
 *
 * @author dima
 */

public interface IVtGraphTemplate
    extends IVtBaseTemplate<IVtGraphParam> {

  /**
   * The {@link IVtGraphTemplate} class identifier.
   */
  String CLASS_ID = IVtTemplateEditorServiceHardConstants.CLSID_GRAPH_TEMPLATE;

}
