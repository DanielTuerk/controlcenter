package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.viewer.settings.entry.AbstractConfigEntry;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Legend;

/**
 * @author Daniel Tuerk
 */
class ConfigGroupPanel extends Composite {

    private static Binder uiBinder = GWT.create(ConfigGroupPanel.Binder.class);
    private final GROUP group;
    @UiField
    Legend groupHeader;
    @UiField
    FieldSet groupContainer;

    ConfigGroupPanel(GROUP group, Collection<AbstractConfigEntry<?>> groupEntries) {
        this.group = group;
        initWidget(uiBinder.createAndBindUi(this));
        groupEntries.forEach(configEntry -> groupContainer.add(new ConfigEntryFormGroup(configEntry)));
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        groupHeader.setText(group.name());
    }

    interface Binder extends UiBinder<Widget, ConfigGroupPanel> {

    }
}
