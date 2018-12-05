package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.viewer.settings.entry.AbstractConfigEntry;
import org.gwtbootstrap3.client.ui.FormLabel;

/**
 * @author Daniel Tuerk
 */
class ConfigEntryFormGroup extends Composite {

    private static Binder uiBinder = GWT.create(ConfigEntryFormGroup.Binder.class);
    @UiField
    FormLabel label;
    @UiField
    FlowPanel valuePanel;
    private AbstractConfigEntry<?> abstractConfigEntry;

    ConfigEntryFormGroup(AbstractConfigEntry<?> abstractConfigEntry) {
        this.abstractConfigEntry = abstractConfigEntry;
        initWidget(uiBinder.createAndBindUi(this));
        valuePanel.add(abstractConfigEntry.createConfigEntryWidget());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        label.setText(abstractConfigEntry.getName());
        abstractConfigEntry.reset();
    }

    interface Binder extends UiBinder<Widget, ConfigEntryFormGroup> {

    }
}
