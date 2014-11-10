package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import net.wbz.moba.controlcenter.web.client.device.StatePanel;
import net.wbz.moba.controlcenter.web.client.editor.track.TrackEditorContainer;
import net.wbz.moba.controlcenter.web.client.model.track.*;
import net.wbz.moba.controlcenter.web.client.viewer.bus.BusMonitorPanel;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConfigPanel;
import net.wbz.moba.controlcenter.web.client.viewer.track.TrackViewerContainer;

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

    public ControlCenterApp() {
        // TODO: ugly
        ModelManager.getInstance().registerModel(new SignalHorizontalWidget());
        ModelManager.getInstance().registerModel(new SignalVerticalWidget());
        // straight after signal because signal extends straight
        ModelManager.getInstance().registerModel(new StraightVerticalWidget());
        ModelManager.getInstance().registerModel(new StraightHorizontalWidget());
        ModelManager.getInstance().registerModel(new CurveTopRightWidget());
        ModelManager.getInstance().registerModel(new CurveTopLeftWidget());
        ModelManager.getInstance().registerModel(new CurveBottomRightWidget());
        ModelManager.getInstance().registerModel(new CurveBottomLeftWidget());
        ModelManager.getInstance().registerModel(new SwitchLeftBottomToTopWidget());
        ModelManager.getInstance().registerModel(new SwitchLeftTopToBottomWidget());
        ModelManager.getInstance().registerModel(new SwitchLeftRightToLeftWidget());
        ModelManager.getInstance().registerModel(new SwitchLeftLeftToRightWidget());
        ModelManager.getInstance().registerModel(new SwitchRightBottomToTopWidget());
        ModelManager.getInstance().registerModel(new SwitchRightTopToBottomWidget());
        ModelManager.getInstance().registerModel(new SwitchRightRightToLeftWidget());
        ModelManager.getInstance().registerModel(new SwitchRightLeftToRightWidget());
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        loadWelcomePage();
    }

    private void loadWelcomePage() {
        welcomePageContainer = new WelcomePage(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loadControlCenter();
            }
        }, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loadControlCenter();
            }
        }
        );
        RootLayoutPanel.get().add(welcomePageContainer);
    }

    private void loadControlCenter() {
        RootLayoutPanel.get().remove(welcomePageContainer);

        trackViewerContainer = new TrackViewerContainer();
        trackEditorContainer = new TrackEditorContainer();
        busMonitorPanel=new BusMonitorPanel();
        configPanel =new ConfigPanel();

        initAppMenu();

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(appMenu, 50);

        dockLayoutPanel.addSouth(new StatePanel(), 50);

        // load the track viewer
        contentContainerPanel = new SimplePanel();
        contentContainerPanel.setStyleName("contentContainerPanel");
        dockLayoutPanel.add(contentContainerPanel);

        showTrackViewer();

        RootLayoutPanel.get().add(dockLayoutPanel);
    }

    private void initAppMenu() {
        appMenu.setShowEditorCommand(new Command() {
            @Override
            public void execute() {
                showTrackEditor();
            }
        });
        appMenu.setShowViewerCommand(new Command() {
            @Override
            public void execute() {
                showTrackViewer();
            }
        });
        appMenu.setShowBusMonitorCommand(new Command() {
            @Override
            public void execute() {
                showBusMonitor();
            }
        });
        appMenu.setShowConfigCommand(new Command() {
            @Override
            public void execute() {
                showSettings();
            }
        });
    }

    private void showSettings() {
        contentContainerPanel.remove(trackViewerContainer);
        contentContainerPanel.remove(trackEditorContainer);
        contentContainerPanel.remove(busMonitorPanel);
        contentContainerPanel.add(configPanel);
    }

    public void showTrackEditor() {
        contentContainerPanel.remove(trackViewerContainer);
        contentContainerPanel.remove(busMonitorPanel);
        contentContainerPanel.remove(configPanel);
        contentContainerPanel.add(trackEditorContainer);
    }

    public void showTrackViewer() {
        contentContainerPanel.remove(trackEditorContainer);
        contentContainerPanel.remove(busMonitorPanel);
        contentContainerPanel.remove(configPanel);
        contentContainerPanel.add(trackViewerContainer);
    }
    public void showBusMonitor() {
        contentContainerPanel.remove(trackEditorContainer);
        contentContainerPanel.remove(trackViewerContainer);
        contentContainerPanel.remove(configPanel);
        contentContainerPanel.add(busMonitorPanel);
    }
}
