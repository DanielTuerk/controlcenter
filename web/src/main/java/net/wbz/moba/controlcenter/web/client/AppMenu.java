package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.Pull;

/**
 * Menu bar on the top of the application.
 * Links are switching the active panel of the content container.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class AppMenu extends Navbar {

    private final Scheduler.ScheduledCommand showEditorCommand;
    private final Scheduler.ScheduledCommand showBusMonitorCommand;

    private final Scheduler.ScheduledCommand showViewerCommand;
    private final Scheduler.ScheduledCommand showConfigCommand;
    private AnchorListItem viewerAnchorListItem;
    private AnchorListItem editorAnchorListItem;
    private AnchorListItem monitorAnchorListItem;
    private AnchorListItem configurationAnchorListItem;

    public AppMenu(Scheduler.ScheduledCommand showEditorCommand, Command showViewerCommand,
                   Scheduler.ScheduledCommand showBusMonitorCommand, Scheduler.ScheduledCommand showConfigCommand) {
        this.showEditorCommand = showEditorCommand;
        this.showBusMonitorCommand = showBusMonitorCommand;
        this.showViewerCommand = showViewerCommand;
        this.showConfigCommand = showConfigCommand;

        ensureDebugId("cwMenuBar");

        addStyleName("appMenu");
        NavbarHeader navbarHeader = new NavbarHeader();
        NavbarBrand brand = new NavbarBrand();
        brand.setText("Control Center");
        navbarHeader.add(brand);
        add(navbarHeader);

        NavbarNav navbarNav = new NavbarNav();
        viewerAnchorListItem = createLink("Viewer", showViewerCommand);
        navbarNav.add(viewerAnchorListItem);
        editorAnchorListItem = createLink("Editor", showEditorCommand);
        navbarNav.add(editorAnchorListItem);
        monitorAnchorListItem = createLink("Bus Monitor", showBusMonitorCommand);
        navbarNav.add(monitorAnchorListItem);
        add(navbarNav);
        NavbarNav rightNavbarNav = new NavbarNav();
        rightNavbarNav.setPull(Pull.RIGHT);
        configurationAnchorListItem = createLink("Configuration", showConfigCommand);
        rightNavbarNav.add(configurationAnchorListItem);
        add(rightNavbarNav);
    }

    @Override
    protected void onLoad() {
       super.onLoad();
    }

    private AnchorListItem createLink(String title, final Scheduler.ScheduledCommand command) {
        AnchorListItem anchor = new AnchorListItem();
        anchor.setText(title);
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        return anchor;
    }


    public AnchorListItem getViewerAnchorListItem() {
        return viewerAnchorListItem;
    }

    public AnchorListItem getEditorAnchorListItem() {
        return editorAnchorListItem;
    }

    public AnchorListItem getMonitorAnchorListItem() {
        return monitorAnchorListItem;
    }

    public AnchorListItem getConfigurationAnchorListItem() {
        return configurationAnchorListItem;
    }
}
