package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;

/**
 * Menu bar on the top of the application.
 * Links are switching the active panel of the content container.
 *
 * @author Daniel Tuerk
 */
abstract class AppMenu extends Composite {

    interface LoginUiBinder extends UiBinder<Widget, AppMenu> {
    }

    private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class);
    @UiField
    AnchorListItem linkViewer;
    @UiField
    AnchorListItem linkEditor;
    @UiField
    AnchorListItem linkBusMonitor;
    @UiField
    AnchorListItem linkConfiguration;

    public AppMenu() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("linkViewer")
    void clickLinkViewer(ClickEvent event) {
        showViewer();
        linkViewer.setActive(true);
    }

    @UiHandler("linkEditor")
    void clickLinkEditor(ClickEvent event) {
        showEditor();
        linkEditor.setActive(true);
    }

    @UiHandler("linkBusMonitor")
    void clickLinkBusMonitor(ClickEvent event) {
        showBusMonitor();
        linkBusMonitor.setActive(true);
    }

    @UiHandler("linkConfiguration")
    void clickLinkConfiguration(ClickEvent event) {
        showConfiguration();
        linkConfiguration.setActive(true);
    }

    abstract void showViewer();

    abstract void showEditor();

    abstract void showBusMonitor();

    abstract void showConfiguration();

}
