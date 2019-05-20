package net.wbz.moba.controlcenter.web.client.components;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.gwtbootstrap3.client.ui.Container;

/**
 * @author Daniel Tuerk
 */
public class ScrollPanel extends HTMLPanel {

    public ScrollPanel(String html) {
        super(html);

        Container widget = new Container();
        widget.setFluid(true);
        add(widget);
    }

    public ScrollPanel(SafeHtml safeHtml) {
        super(safeHtml);
    }

    public ScrollPanel(String tag, String html) {
        super(tag, html);
    }


}
