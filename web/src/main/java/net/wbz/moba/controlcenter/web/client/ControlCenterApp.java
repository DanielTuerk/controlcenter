package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.event.ConstructionRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.model.track.ModelManager;
import net.wbz.moba.controlcenter.web.client.monitor.BusMonitorPanel;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.scenario.ScenarioEditor;
import net.wbz.moba.controlcenter.web.client.settings.ConfigPanel;
import net.wbz.moba.controlcenter.web.client.settings.Settings;
import net.wbz.moba.controlcenter.web.client.train.TrainPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerContainer;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
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

    private ControlCenterContainer controlCenterContainer;

    public ControlCenterApp() {
    }

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        WebResources.INSTANCE.mainCss().ensureInjected();
        WebResources.INSTANCE.trackCss().ensureInjected();
        WebResources.INSTANCE.scenarioCss().ensureInjected();

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

        ConstructionRemoteListener constructionListener = anEvent -> reloadControlCenter();
        EventReceiver.getInstance().addListener(constructionListener);
    }

    private void reloadControlCenter() {
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
        WelcomeContainer welcomeContainerContainer = new WelcomeContainer() {
            @Override
            void onCurrentConstructionLoaded() {
                loadControlCenter();
            }
        };
        setRootPanelContent(welcomeContainerContainer);
    }

    private void setRootPanelContent(Widget content) {
        RootLayoutPanel.get().clear();
        RootLayoutPanel.get().getElement().setInnerHTML("");
        RootLayoutPanel.get().add(content);
    }

    private void loadControlCenter() {
        trackViewerContainer = new TrackViewerContainer();
        trackEditorContainer = new TrackEditorContainer();
        busMonitorPanel = new BusMonitorPanel();
        trackScenarioEditorPanel = new ScenarioEditor();
        trainPanel = new TrainPanel();
        configPanel = new ConfigPanel();
        controlCenterContainer = new ControlCenterContainer(createAppMenu());

        show(trackViewerContainer);

        setRootPanelContent(controlCenterContainer);
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
