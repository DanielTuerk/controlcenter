package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.NavbarLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class ConfigPanel extends FlowPanel {

    private final Map<String, List<AbstractConfigEntry>> configEntries = new HashMap<>();
    private final List<AnchorListItem> navLinks = new ArrayList<>();

    public ConfigPanel() {
        registerEntry(new ConfigEntryConstructionManage());
        registerEntry(new ConfigEntryCommonStartup());
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
        addStyleName("configPanel");

        final FlowPanel configEntryContainer = new FlowPanel();
        configEntryContainer.addStyleName("configPanel_entry");

        NavPills navList = new NavPills();
        navList.setWidth("200px");
//        navList.getElement().getStyle().setFloat(Style.Float.LEFT);
        navList.setStacked(true);

//        boolean isFirst = true;
        for (Map.Entry<String, List<AbstractConfigEntry>> configEntriesEntrySet : configEntries.entrySet()) {

//            if (!isFirst) {
//                navList.add(new Divider());
//            }

//            navList.add(new NavHeader(configEntriesEntrySet.getKey()));

            for (final AbstractConfigEntry configEntry : configEntriesEntrySet.getValue()) {
                final AnchorListItem navLink = new AnchorListItem(configEntry.getName());
                navLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        for (AnchorListItem existingNavLink : navLinks) {
                            existingNavLink.setActive(false);
                        }
                        configEntryContainer.clear();
                        configEntryContainer.add(configEntry.getContentPanel());

                        navLink.setActive(true);
                    }
                });
                navList.add(navLink);
                navLinks.add(navLink);
            }

//            isFirst = false;
        }

        add(navList);
        add(configEntryContainer);
    }
}
