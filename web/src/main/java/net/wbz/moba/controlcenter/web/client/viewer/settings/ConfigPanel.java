package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Pull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class ConfigPanel extends Panel {

    private final Map<String, List<AbstractConfigEntry>> configEntries = new HashMap<>();
    private final List<AnchorListItem> navLinks = new ArrayList<>();
    private PanelBody configBody;
    private PanelHeader configHeader;
    private PanelFooter configFooter;

    private HandlerRegistration resizeListenerHandle;

    public ConfigPanel() {
        registerEntry(new ConfigEntryConstructionManage());
        registerEntry(new ConfigEntryCommonStartup());

        addStyleName("configPanel");

        getElement().getStyle().setHeight(100, Style.Unit.PCT);

        configHeader = new PanelHeader();
        configHeader.getElement().getStyle().setHeight(60, Style.Unit.PX);
        NavPills navList = new NavPills();
        navList.getElement().getStyle().setWidth(20, Style.Unit.PCT);
        navList.getElement().getStyle().setFloat(Style.Float.LEFT);
        configHeader.add(navList);

        configBody = new PanelBody();
        configBody.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);

        final FlowPanel configEntryContainer = new FlowPanel();
        configEntryContainer.addStyleName("configPanel_entry");

        for (Map.Entry<String, List<AbstractConfigEntry>> configEntriesEntrySet : configEntries.entrySet()) {
            for (final AbstractConfigEntry configEntry : configEntriesEntrySet.getValue()) {
                final AnchorListItem navLink = new AnchorListItem(configEntry.getName());
                navLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        for (AnchorListItem existingNavLink : navLinks) {
                            existingNavLink.setActive(false);
                        }
                        configEntryContainer.clear();
                        Panel panel = new Panel();
                        PanelHeader panelHeader = new PanelHeader();
                        panelHeader.setText(configEntry.getGroup());
                        PanelBody panelBody = new PanelBody();
                        panelBody.add(configEntry.getContentPanel());
                        panel.add(panelHeader);
                        panel.add(panelBody);
                        configEntryContainer.add(panel);
                        navLink.setActive(true);
                    }
                });
                navList.add(navLink);
                navLinks.add(navLink);
            }
        }
        configBody.add(configEntryContainer);

        configFooter = new PanelFooter();

        Button btnSave = new Button("Save");
        btnSave.setType(ButtonType.SUCCESS);
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                for(List<AbstractConfigEntry> configEntryList: configEntries.values()) {
                    for(AbstractConfigEntry configEntry:configEntryList){
                        configEntry.save();
                        configEntry.reset();
                    }
                }
            }
        });
        configFooter.add(btnSave);

        Button btnCancel = new Button("Cancel");
        btnCancel.setType(ButtonType.DANGER);
        btnCancel.setPull(Pull.RIGHT);
        configFooter.add(btnCancel);
    }

    private void registerEntry(AbstractConfigEntry configEntry) {
        if (!configEntries.containsKey(configEntry.getGroup())) {
            configEntries.put(configEntry.getGroup(), new ArrayList<AbstractConfigEntry>());
        }
        configEntries.get(configEntry.getGroup()).add(configEntry);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        add(configHeader);
        add(configBody);
        add(configFooter);

        updatePanelHeight();

        resizeListenerHandle = Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                Scheduler.get().scheduleDeferred(
                        new Scheduler.ScheduledCommand() {
                            public void execute() {
                                updatePanelHeight();
                            }
                        });
            }
        });
    }

    private void updatePanelHeight() {
        configBody.setHeight(getParent().getOffsetHeight() -
                (configHeader.getOffsetHeight() + configFooter.getOffsetHeight()) + "px");
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        resizeListenerHandle.removeHandler();
        remove(configHeader);
        remove(configBody);
        remove(configFooter);
    }
}
