package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class AppMenu extends MenuBar {

    private Scheduler.ScheduledCommand showEditorCommand;



    public void setShowEditorCommand(Scheduler.ScheduledCommand showEditorCommand) {
        this.showEditorCommand = showEditorCommand;
    }

    public void setShowViewerCommand(Scheduler.ScheduledCommand showViewerCommand) {
        this.showViewerCommand = showViewerCommand;
    }

    private Scheduler.ScheduledCommand showViewerCommand;


    /**
     * Initialize this example.
     */
    @Override
    protected void onLoad() {
        // Create a command that will execute on menu item selection
        this.setAutoOpen(true);
        this.setWidth("100%");
        this.setHeight("100px");
        this.setAnimationEnabled(true);

        // Create a sub menu of recent documents
        MenuBar recentDocsMenu = new MenuBar(true);
        String[] recentDocs = {"foo", "bar", "foobar"};
        for (int i = 0; i < recentDocs.length; i++) {
            recentDocsMenu.addItem(recentDocs[i], new Command() {
                @Override
                public void execute() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }

        // Create the file menu
        MenuBar fileMenu = new MenuBar(true);
        fileMenu.setAnimationEnabled(true);
        this.addItem(new com.google.gwt.user.client.ui.MenuItem("FILE", fileMenu));
        String[] fileOptions = {"1", "2", "3"};
        for (int i = 0; i < fileOptions.length; i++) {
            if (i == 3) {
                fileMenu.addSeparator();
                fileMenu.addItem(fileOptions[i], recentDocsMenu);
                fileMenu.addSeparator();
            } else {
                fileMenu.addItem(fileOptions[i], recentDocsMenu);
            }
        }

        addItem(new MenuItem("Editor", false, showEditorCommand));
        addItem(new MenuItem("Viewer", false, showViewerCommand));

        // Return the menu
        this.ensureDebugId("cwMenuBar");
    }


}
