package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class TrackEditorPanel extends AbstractTrackPanel {

    @Override
    protected void onLoad() {
        addStyleName("boundary");
        setSize("100%", "800px");



    }

    @Override
    public void addTrackWidget(Widget widget, int left, int top) {
        add(widget,left,top);
    }
}
