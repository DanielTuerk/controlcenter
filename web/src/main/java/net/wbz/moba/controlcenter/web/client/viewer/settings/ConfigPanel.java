package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.Settings;
import net.wbz.moba.controlcenter.web.client.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.viewer.settings.entry.AbstractConfigEntry;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

/**
 * @author Daniel Tuerk
 */
public class ConfigPanel extends Composite {

    private static Binder uiBinder = GWT.create(ConfigPanel.Binder.class);
    private final List<AbstractConfigEntry> configEntries = new ArrayList<>();

    @UiField
    Form contentContainer;

    public ConfigPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        for (GROUP group : GROUP.values()) {
            final Collection<AbstractConfigEntry<?>> groupEntries = Settings.getInstance().getGroupEntries(group);
            configEntries.addAll(groupEntries);
            contentContainer.add(new ConfigGroupPanel(group, groupEntries));
        }
    }

    @UiHandler("btnApply")
    public void onBtnApplyClicked(ClickEvent clickEvent) {
        configEntries.forEach(AbstractConfigEntry::save);
        Notify.notify("Settings saved!");
    }

    @UiHandler("btnReset")
    public void onBtnResetClicked(ClickEvent clickEvent) {
        configEntries.forEach(AbstractConfigEntry::reset);
    }

    interface Binder extends UiBinder<Widget, ConfigPanel> {

    }
}
