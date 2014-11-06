package net.wbz.moba.controlcenter.web.client;

import com.github.gwtbootstrap.client.ui.Brand;
import com.github.gwtbootstrap.client.ui.Nav;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Navbar;
import com.github.gwtbootstrap.client.ui.constants.Alignment;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

/**
 * Menu bar on the top of the application.
 * Links are switching the active panel of the content container.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class AppMenu extends Navbar {

    private Scheduler.ScheduledCommand showEditorCommand;
    private Command showBusMonitorCommand;

    private Scheduler.ScheduledCommand showViewerCommand;
    private Scheduler.ScheduledCommand showConfigCommand;

    @Override
    protected void onLoad() {
        ensureDebugId("cwMenuBar");

        addStyleName("appMenu");

        add(new Brand("Control Center"));

        Nav nav2 = new Nav();
        NavLink linkViewer = new NavLink("Viewer");
        linkViewer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showViewerCommand.execute();
            }
        });
        nav2.add(linkViewer);
            add(nav2);

        NavLink linkEditor = new NavLink("Editor");
        linkEditor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showEditorCommand.execute();
            }
        });
        Nav nav = new Nav();
        nav.add(linkEditor);
        add(nav);

        NavLink linkBusMonitor= new NavLink("Bus Monitor");
        linkBusMonitor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showBusMonitorCommand.execute();
            }
        });
        Nav navBusMonitor = new Nav();
        navBusMonitor.add(linkBusMonitor);
        add(navBusMonitor);

        NavLink linkSettings= new NavLink("Configuration");
        linkSettings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showConfigCommand.execute();
            }
        });
        Nav navSettings = new Nav();
        navSettings.setAlignment(Alignment.RIGHT);
        navBusMonitor.add(linkSettings);
        add(navSettings);

    }

    public void setShowBusMonitorCommand(Command showBusMonitorCommand) {
        this.showBusMonitorCommand = showBusMonitorCommand;
    }

    public void setShowEditorCommand(Scheduler.ScheduledCommand showEditorCommand) {
        this.showEditorCommand = showEditorCommand;
    }

    public void setShowViewerCommand(Scheduler.ScheduledCommand showViewerCommand) {
        this.showViewerCommand = showViewerCommand;
    }

    public void setShowConfigCommand(Scheduler.ScheduledCommand showConfigCommand) {
        this.showConfigCommand = showConfigCommand;
    }
}
