package net.wbz.moba.controlcenter.web.client.editor.track;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface ClickActionViewerWidgetHandler {

    public void onClick();

    public void repaint();

    public void repaint(boolean state);
}
