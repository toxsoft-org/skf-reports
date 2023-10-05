package org.toxsoft.skf.reports.gui.panels;

import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;

/**
 * Panel to select property of class
 *
 * @author dima
 */
public interface IClassPropPanel {

  /**
   * @param aClassInfo set class for view
   */
  void setClass( ISkClassInfo aClassInfo );

  /**
   * @param aGwid set selected prop
   */
  void select( Gwid aGwid );

  /**
   * @return selected prop
   */
  IDtoClassPropInfoBase getSelectedProp();
}
