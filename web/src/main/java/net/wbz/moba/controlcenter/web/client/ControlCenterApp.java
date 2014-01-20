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
import net.wbz.moba.controlcenter.web.client.viewer.TrackViewerContainer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ControlCenterApp implements EntryPoint {

    private final AppMenu appMenu = new AppMenu();

    private TrackEditorContainer trackEditorContainer;
    private TrackViewerContainer trackViewerContainer;

    private SimplePanel contentContainerPanel;

    private WelcomePage welcomePageContainer;

    public ControlCenterApp() {
        // signal before straigt
        ModelManager.getInstance().registerModel(new SignalHorizontalWidget());
        ModelManager.getInstance().registerModel(new SignalVerticalWidget());
        // straigt after signal because signal extends straight
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

        initAppMenu();

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(appMenu, 43);

        dockLayoutPanel.addSouth(new StatePanel(), 50);

        // load the track viewer
        contentContainerPanel = new SimplePanel();
//        contentContainerPanel.add(trackViewerContainer);
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

    }

    public void showTrackEditor() {
        contentContainerPanel.remove(trackViewerContainer);
        contentContainerPanel.add(trackEditorContainer);
    }

    public void showTrackViewer() {
        contentContainerPanel.remove(trackEditorContainer);
        contentContainerPanel.add(trackViewerContainer);

    }
}
