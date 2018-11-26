package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.scenario.ScenarioEditor;
import net.wbz.moba.controlcenter.web.client.train.TrainPanel;
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
    private TrainPanel trainPanel;
    private ConfigPanel configPanel;

    private WelcomeContainer welcomeContainerContainer;

    private ControlCenterContainer controlCenterContainer;

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

        RemoteEventListener constructionListener = anEvent -> {
            if (anEvent instanceof CurrentConstructionChangeEvent) {
                reloadControlCenter();
            }
        };
        EventReceiver.getInstance().addListener(CurrentConstructionChangeEvent.class, constructionListener);
    }

    private void reloadControlCenter() {
        if (welcomeContainerContainer != null && RootLayoutPanel.get().getWidgetIndex(welcomeContainerContainer) >= 0) {
            RootLayoutPanel.get().remove(welcomeContainerContainer);
        }
        if (controlCenterContainer != null && RootLayoutPanel.get().getWidgetIndex(controlCenterContainer) >= 0) {
            RootLayoutPanel.get().remove(controlCenterContainer);
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
        trackEditorContainer = new TrackEditorContainer();
        busMonitorPanel = new BusMonitorPanel();
        trackScenarioEditorPanel = new ScenarioEditor();
        trainPanel = new TrainPanel();
        configPanel = new ConfigPanel();
        controlCenterContainer = new ControlCenterContainer(createAppMenu());

        show(trackViewerContainer);

        RootLayoutPanel.get().add(controlCenterContainer);
    }

    private AppMenu.AppMenuCallback createAppMenu() {
        return new AppMenu.AppMenuCallback() {

            @Override
           public void showViewer() {
                show(trackViewerContainer);
            }

            @Override
            public  void showEditor() {
                show(trackEditorContainer);
            }

            @Override
            public void showBusMonitor() {
                show(busMonitorPanel);
            }

            @Override
            public void showScenarioEditor() {
                show(trackScenarioEditorPanel);
            }

            @Override
            public void showTrainEditor() {
                show(trainPanel);
            }

            @Override
            public void showConfiguration() {
                show(configPanel);
            }
        };
    }

    private void show(Widget containerPanel) {
        controlCenterContainer.setContent(containerPanel);
    }

}
