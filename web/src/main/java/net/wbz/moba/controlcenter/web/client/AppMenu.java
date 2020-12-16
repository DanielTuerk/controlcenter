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
class AppMenu extends Composite {

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
    AnchorListItem linkTrainEditor;
    @UiField
    AnchorListItem linkConfiguration;
    @UiField
    AnchorListItem linkStationsBoard;

    private AppMenuCallback callback;

    public AppMenu() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setCallback(AppMenuCallback callback) {
        this.callback = callback;
    }

    private void setAllInactive() {
        linkViewer.setActive(false);
        linkEditor.setActive(false);
        linkBusMonitor.setActive(false);
        linkScenarioEditor.setActive(false);
        linkTrainEditor.setActive(false);
        linkConfiguration.setActive(false);
        linkStationsBoard.setActive(false);
    }

    @UiHandler("linkViewer")
    void clickLinkViewer(ClickEvent event) {
        callback.showViewer();
        setAllInactive();
        linkViewer.setActive(true);
    }

    @UiHandler("linkEditor")
    void clickLinkEditor(ClickEvent event) {
        callback.showEditor();
        setAllInactive();
        linkEditor.setActive(true);
    }

    @UiHandler("linkBusMonitor")
    void clickLinkBusMonitor(ClickEvent event) {
        callback.showBusMonitor();
        setAllInactive();
        linkBusMonitor.setActive(true);
    }

    @UiHandler("linkScenarioEditor")
    void clickLinkScenarioEditor(ClickEvent event) {
        callback.showScenarioEditor();
        setAllInactive();
        linkScenarioEditor.setActive(true);
    }

    @UiHandler("linkTrainEditor")
    void clickLinkTrainEditor(ClickEvent event) {
        callback.showTrainEditor();
        setAllInactive();
        linkTrainEditor.setActive(true);
    }

    @UiHandler("linkConfiguration")
    void clickLinkConfiguration(ClickEvent event) {
        callback.showConfiguration();
        setAllInactive();
        linkConfiguration.setActive(true);
    }

    @UiHandler("linkStationsBoard")
    void clickLinkStationsBoard(ClickEvent event) {
        callback.showStationsBoard();
        setAllInactive();
        linkStationsBoard.setActive(true);
    }

    interface AppMenuBinder extends UiBinder<Widget, AppMenu> {

    }

    interface AppMenuCallback {

        void showViewer();

        void showEditor();

        void showBusMonitor();

        void showScenarioEditor();

        void showTrainEditor();

        void showConfiguration();

        void showStationsBoard();
    }
}
