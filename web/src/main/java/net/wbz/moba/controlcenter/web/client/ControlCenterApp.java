package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import net.wbz.moba.controlcenter.web.client.device.StatePanel;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.viewer.bus.BusMonitorPanel;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConfigPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerContainer;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionProxy;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ControlCenterApp implements EntryPoint {

    private TrackEditorContainer trackEditorContainer;
    private TrackViewerContainer trackViewerContainer;
    private BusMonitorPanel busMonitorPanel;
    private ConfigPanel configPanel;

    private SimplePanel contentContainerPanel;

    private WelcomeContainer welcomeContainerContainer;

    private List<Widget> containerPanels = new ArrayList<>();

    public ControlCenterApp() {
//        ModelManager.getInstance().init();
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        ModelManager.getInstance().init();

            loadWelcomePage();
//        if (Settings.getInstance().getShowWelcome().getValue()) {
//        } else {
//
//            RequestUtils.getInstance().getConstructionRequest().loadConstructions().fire(
//                    new Receiver<List<ConstructionProxy>>() {
//                        @Override
//                        public void onSuccess(List<ConstructionProxy> response) {
//                            String lastUsedConstruction = Settings.getInstance().getLastUsedConstruction().getValue();
//                            ConstructionProxy constructionToLoad = null;
//                            for (ConstructionProxy construction : response) {
//                                if (construction.getName().equals(lastUsedConstruction)) {
//                                    constructionToLoad = construction;
//                                    break;
//                                }
//                            }
//                            if (constructionToLoad != null) {
//                                RequestUtils.getInstance().getConstructionRequest().setCurrentConstruction(
//                                        constructionToLoad).fire(new Receiver<Void>() {
//                                    @Override
//                                    public void onSuccess(Void response) {
//                                        loadControlCenter();
//                                    }
//                                });
//                            } else {
//                                Notify.notify("", "can't load last used construction", IconType.WARNING);
//
//                                loadWelcomePage();
//                            }
//                        }
//                    });
//
//        }
    }

    private void loadWelcomePage() {
        welcomeContainerContainer = new WelcomeContainer() {
            @Override
            void onCurrentConstructionLoaded() {
                loadControlCenter();
            }
        };
        RootLayoutPanel.get().add(welcomeContainerContainer);
    }

    private void loadControlCenter() {
        if (RootLayoutPanel.get().getWidgetIndex(welcomeContainerContainer) >= 0) {
            RootLayoutPanel.get().remove(welcomeContainerContainer);
        }

        trackViewerContainer = new TrackViewerContainer();
        containerPanels.add(trackViewerContainer);
        trackEditorContainer = new TrackEditorContainer();
        containerPanels.add(trackEditorContainer);
        busMonitorPanel = new BusMonitorPanel();
        containerPanels.add(busMonitorPanel);
        configPanel = new ConfigPanel();
        containerPanels.add(configPanel);

        final AppMenu appMenu = createAppMenu();

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(appMenu, 50);

        StatePanel statePanel = new StatePanel();
        dockLayoutPanel.addSouth(statePanel, 50);

        // allow elements to overlap the max height of the south container (e.g. device select)
        dockLayoutPanel.getWidgetContainerElement(statePanel).getStyle().setOverflow(Style.Overflow.VISIBLE);

        // load the track viewer
        contentContainerPanel = new SimplePanel();
        contentContainerPanel.setStyleName("contentContainerPanel");
        dockLayoutPanel.add(contentContainerPanel);

        show(trackViewerContainer);

        RootLayoutPanel.get().add(dockLayoutPanel);
    }

    private AppMenu createAppMenu() {
        return new AppMenu() {

            @Override
            void showViewer() {
                show(trackViewerContainer);
            }

            @Override
            void showEditor() {
                show(trackEditorContainer);
            }

            @Override
            void showBusMonitor() {
                show(busMonitorPanel);
            }

            @Override
            void showConfiguration() {
                show(configPanel);
            }
        };
    }

    private void show(Widget containerPanel) {
        for (Widget container : containerPanels) {
            if (contentContainerPanel.getElement().isOrHasChild(container.getElement())) {
                contentContainerPanel.remove(container);
            }
        }
        contentContainerPanel.add(containerPanel);
    }

}
