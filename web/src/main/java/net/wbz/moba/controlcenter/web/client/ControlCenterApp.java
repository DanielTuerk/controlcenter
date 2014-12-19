package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.device.StatePanel;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.model.track.*;
import net.wbz.moba.controlcenter.web.client.model.track.signal.SignalHorizontalWidget;
import net.wbz.moba.controlcenter.web.client.model.track.signal.SignalVerticalWidget;
import net.wbz.moba.controlcenter.web.client.viewer.bus.BusMonitorPanel;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConfigPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerContainer;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.track.model.Curve;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;
import org.gwtbootstrap3.extras.growl.client.ui.Growl;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlHelper;
import org.gwtbootstrap3.extras.growl.client.ui.GrowlOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ControlCenterApp implements EntryPoint {

    private final AppMenu appMenu = new AppMenu();

    private TrackEditorContainer trackEditorContainer;
    private TrackViewerContainer trackViewerContainer;
    private BusMonitorPanel busMonitorPanel;
    private ConfigPanel configPanel;

    private SimplePanel contentContainerPanel;

    private WelcomePage welcomePageContainer;

    private List<Widget> containerPanels = new ArrayList<>();

    public ControlCenterApp() {
       ModelManager.getInstance().init();
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        if (Settings.getInstance().getShowWelcome().getValue()) {
            loadWelcomePage();
        } else {

            ServiceUtils.getConstrutionService().loadConstructions(new AsyncCallback<Construction[]>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(Construction[] constructions) {
                    String lastUsedConstruction = Settings.getInstance().getLastUsedConstruction().getValue();
                    Construction constructionToLoad = null;
                    for (Construction construction : constructions) {
                        if (construction.getName().equals(lastUsedConstruction)) {
                            constructionToLoad = construction;
                            break;
                        }
                    }
                    if (constructionToLoad != null) {
                        ServiceUtils.getConstrutionService().setCurrentConstruction(constructionToLoad, new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                            }

                            @Override
                            public void onSuccess(Void result) {
                                loadControlCenter();
                            }
                        });
                    } else {
                        GrowlOptions growlOptions = GrowlHelper.getNewOptions();
                        growlOptions.setWarningType();
                        Growl.growl("can't load last used construction", growlOptions);

                        loadWelcomePage();
                    }
                }
            });
        }
    }

    private void loadWelcomePage() {
        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loadControlCenter();
            }
        };
        welcomePageContainer = new WelcomePage(clickHandler, clickHandler);
        RootLayoutPanel.get().add(welcomePageContainer);
    }

    private void loadControlCenter() {
        if (RootLayoutPanel.get().getWidgetIndex(welcomePageContainer) >= 0) {
            RootLayoutPanel.get().remove(welcomePageContainer);
        }

        trackViewerContainer = new TrackViewerContainer();
        containerPanels.add(trackViewerContainer);
        trackEditorContainer = new TrackEditorContainer();
        containerPanels.add(trackEditorContainer);
        busMonitorPanel = new BusMonitorPanel();
        containerPanels.add(busMonitorPanel);
        configPanel = new ConfigPanel();
        containerPanels.add(configPanel);

        initAppMenu();

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(appMenu, 50);

        dockLayoutPanel.addSouth(new StatePanel(), 50);

        // load the track viewer
        contentContainerPanel = new SimplePanel();
        contentContainerPanel.setStyleName("contentContainerPanel");
        dockLayoutPanel.add(contentContainerPanel);

        show(trackViewerContainer);

        RootLayoutPanel.get().add(dockLayoutPanel);
    }

    private void initAppMenu() {
        appMenu.setShowEditorCommand(new Command() {
            @Override
            public void execute() {
                show(trackEditorContainer);
            }
        });
        appMenu.setShowViewerCommand(new Command() {
            @Override
            public void execute() {
                show(trackViewerContainer);
            }
        });
        appMenu.setShowBusMonitorCommand(new Command() {
            @Override
            public void execute() {
                show(busMonitorPanel);
            }
        });
        appMenu.setShowConfigCommand(new Command() {
            @Override
            public void execute() {
                show(configPanel);
            }
        });
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
