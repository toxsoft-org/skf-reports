package org.toxsoft.skf.reports.mws.e4.uiparts;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.uskat.core.gui.e4.uiparts.*;

import ru.toxsoft.skt.vetrol.ws.reports.e4.uiparts.*;

/**
 * Вью работы c шаблонами отчетов: список, создание, правка, удаление.
 * <p>
 *
 * @author dima
 */
public class UipartReportTemplates
    extends SkMwsAbstractPart {

  VtReportTemplateEditorPanel panel;

  @Override
  protected void doCreateContent( TsComposite aParent ) {
    ITsGuiContext ctx = new TsGuiContext( getWindowContext() );
    panel = new VtReportTemplateEditorPanel( aParent, ctx );
    panel.setLayoutData( BorderLayout.CENTER );
  }

}
