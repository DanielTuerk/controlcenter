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

    private Scheduler.ScheduledCommand showEditorCommand;
    private Command showBusMonitorCommand;

    private Scheduler.ScheduledCommand showViewerCommand;
    private Scheduler.ScheduledCommand showConfigCommand;

    @Override
    protected void onLoad() {
        ensureDebugId("cwMenuBar");

        addStyleName("appMenu");
        NavbarHeader navbarHeader = new NavbarHeader();
        NavbarBrand brand = new NavbarBrand();
        brand.setText("Control Center");
        navbarHeader.add(brand);
        add(navbarHeader);

        NavbarNav navbarNav = new NavbarNav();
        navbarNav.add(createLink("Viewer", showViewerCommand));
        navbarNav.add(createLink("Editor", showEditorCommand));
        navbarNav.add(createLink("Bus Monitor", showBusMonitorCommand));
        add(navbarNav);
        NavbarNav navbarNav2 = new NavbarNav();
        navbarNav2.setPull(Pull.RIGHT);
        navbarNav2.add(createLink("Configuration", showConfigCommand));
        add(navbarNav2);
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
