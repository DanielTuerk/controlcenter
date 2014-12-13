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
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Trigger;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
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

//        FlowPanel flowPanel = new FlowPanel();
//
//        Button btn = new Button("test");
//        Button btn2 = new Button("test2");
//btn2.getElement().getStyle().setTop(200, Style.Unit.PX);
//btn2.getElement().getStyle().setLeft(200, Style.Unit.PX);
//
//
//
//        Popover   popover= new Popover();
//        popover.setWidget(btn);
////        popover.setContainer("body");
//        popover.setTitle("Control");
//        popover.setTrigger(Trigger.CLICK);
//        popover.setPlacement(Placement.RIGHT);
//
//        popover.setContent("content");
////        popover.reconfigure();
////        popover.show();
//
//
//        flowPanel.add(btn);
//        flowPanel.add(btn2);
//        RootLayoutPanel.get().add(flowPanel);
//
//
//        new net.wbz.moba.controlcenter.web.client.Popover(btn).show();
//        new net.wbz.moba.controlcenter.web.client.Popover(btn2).show();

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
        if(RootLayoutPanel.get().getWidgetIndex(welcomePageContainer) >= 0) {
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
        for(Widget container  :containerPanels){
            if(contentContainerPanel.getElement().isOrHasChild(container.getElement())){

            contentContainerPanel.remove(container);
            }
        }
        contentContainerPanel.add(containerPanel);
    }

}
