package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.device.StatePanel;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.scenario.ScenarioEditor;
import net.wbz.moba.controlcenter.web.client.viewer.bus.BusMonitorPanel;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConfigPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerContainer;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.CurrentConstructionChangeEvent;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ControlCenterApp implements EntryPoint {

    private TrackEditorContainer trackEditorContainer;
    private TrackViewerContainer trackViewerContainer;
    private BusMonitorPanel busMonitorPanel;
    private ScenarioEditor trackScenarioEditorPanel;
    private ConfigPanel configPanel;

    private SimplePanel contentContainerPanel;

    private WelcomeContainer welcomeContainerContainer;

    private List<Widget> containerPanels = new ArrayList<>();
    private DockLayoutPanel dockLayoutPanel;

    public ControlCenterApp() {
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        ModelManager.getInstance().init();

        // check existing construction on server
        RequestUtils.getInstance().getConstructionService()
            .getCurrentConstruction(new OnlySuccessAsyncCallback<Construction>() {
                @Override
                public void onSuccess(Construction result) {
                    if (result != null) {
                        loadControlCenter();
                    } else {
                        // construction on server, set construction
                        if (Settings.getInstance().getShowWelcome().getValue()) {
                            loadWelcomePage();
                        } else {
                            loadLastUsedConstruction();
                        }
                    }
                }
            });

        RemoteEventListener constructionListener = new RemoteEventListener() {
            @Override
            public void apply(Event anEvent) {
                if (anEvent instanceof CurrentConstructionChangeEvent) {
                    reloadControlCenter();
                }
            }
        };
        EventReceiver.getInstance().addListener(CurrentConstructionChangeEvent.class, constructionListener);
    }

    private void reloadControlCenter() {
        if (welcomeContainerContainer != null && RootLayoutPanel.get().getWidgetIndex(welcomeContainerContainer) >= 0) {
            RootLayoutPanel.get().remove(welcomeContainerContainer);
        }
        if (dockLayoutPanel != null && RootLayoutPanel.get().getWidgetIndex(dockLayoutPanel) >= 0) {
            RootLayoutPanel.get().remove(dockLayoutPanel);
        }
        loadControlCenter();
    }

    private void loadLastUsedConstruction() {
        RequestUtils.getInstance().getConstructionService()
            .loadConstructions(new OnlySuccessAsyncCallback<Collection<Construction>>() {
                @Override
                public void onSuccess(Collection<Construction> result) {

                    String lastUsedConstruction = Settings.getInstance().getLastUsedConstruction().getValue();
                    Construction constructionToLoad = null;
                    for (Construction construction : result) {
                        if (construction.getName().equals(lastUsedConstruction)) {
                            constructionToLoad = construction;
                            break;
                        }
                    }
                    if (constructionToLoad != null) {
                        RequestUtils.getInstance().getConstructionService()
                            .setCurrentConstruction(constructionToLoad, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                }

                                @Override
                                public void onSuccess(Void result) {
                                    loadControlCenter();
                                }
                            });
                    } else {
                        Notify.notify("", "can't load last used construction", IconType.WARNING);

                        loadWelcomePage();
                    }
                }
            });
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
        trackScenarioEditorPanel = new ScenarioEditor();
        containerPanels.add(trackScenarioEditorPanel);
        configPanel = new ConfigPanel();
        containerPanels.add(configPanel);

        final AppMenu appMenu = createAppMenu();

        dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
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
            void showScenarioEditor() {
                show(trackScenarioEditorPanel);
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
