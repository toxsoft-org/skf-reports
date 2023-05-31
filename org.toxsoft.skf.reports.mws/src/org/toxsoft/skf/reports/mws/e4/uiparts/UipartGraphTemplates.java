package org.toxsoft.skf.reports.mws.e4.uiparts;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.skf.reports.gui.panels.*;
import org.toxsoft.uskat.core.gui.e4.uiparts.*;

/**
 * Вью работы c шаблонами графиков: список, создание, правка, удаление.
 * <p>
 *
 * @author dima
 */
public class UipartGraphTemplates
    extends SkMwsAbstractPart {

  GraphTemplateEditorPanel panel;

  @Override
  protected void doCreateContent( TsComposite aParent ) {
    ITsGuiContext ctx = new TsGuiContext( getWindowContext() );
    panel = new GraphTemplateEditorPanel( aParent, ctx );
    panel.setLayoutData( BorderLayout.CENTER );
  }

}
