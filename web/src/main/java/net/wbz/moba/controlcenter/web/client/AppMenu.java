package net.wbz.moba.controlcenter.web.client;

import org.gwtbootstrap3.client.ui.AnchorListItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Menu bar on the top of the application.
 * Links are switching the active panel of the content container.
 *
 * @author Daniel Tuerk
 */
abstract class AppMenu extends Composite {

    private static AppMenuBinder uiBinder = GWT.create(AppMenuBinder.class);
    @UiField
    AnchorListItem linkViewer;
    @UiField
    AnchorListItem linkEditor;
    @UiField
    AnchorListItem linkBusMonitor;
    @UiField
    AnchorListItem linkScenarioEditor;
    @UiField
    AnchorListItem linkConfiguration;

    public AppMenu() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private void setAllInactive() {
        linkViewer.setActive(false);
        linkEditor.setActive(false);
        linkBusMonitor.setActive(false);
        linkScenarioEditor.setActive(false);
        linkConfiguration.setActive(false);
    }

    @UiHandler("linkViewer")
    void clickLinkViewer(ClickEvent event) {
        showViewer();
        setAllInactive();
        linkViewer.setActive(true);
    }

    @UiHandler("linkEditor")
    void clickLinkEditor(ClickEvent event) {
        showEditor();
        setAllInactive();
        linkEditor.setActive(true);
    }

    @UiHandler("linkBusMonitor")
    void clickLinkBusMonitor(ClickEvent event) {
        showBusMonitor();
        setAllInactive();
        linkBusMonitor.setActive(true);
    }

    @UiHandler("linkScenarioEditor")
    void clickLinkScenarioEditor(ClickEvent event) {
        showScenarioEditor();
        setAllInactive();
        linkScenarioEditor.setActive(true);
    }

    @UiHandler("linkConfiguration")
    void clickLinkConfiguration(ClickEvent event) {
        showConfiguration();
        setAllInactive();
        linkConfiguration.setActive(true);
    }

    abstract void showViewer();

    abstract void showEditor();

    abstract void showBusMonitor();

    abstract void showScenarioEditor();

    abstract void showConfiguration();

    interface AppMenuBinder extends UiBinder<Widget, AppMenu> {
    }

}
