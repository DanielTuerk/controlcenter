package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * Dropdown for the name of the {@link net.wbz.moba.controlcenter.web.shared.constrution.Construction}s.
 *
 * @author Daniel Tuerk
 */
public class ConstructionSelectionConfigEntry extends SelectionConfigEntry {
    public ConstructionSelectionConfigEntry(STORAGE storageType, String group, String name) {
        super(storageType, group, name);
    }

    @Override
    protected void handleStorageRead(final String value) {
        // at first initialize value for direct usage
        ConstructionSelectionConfigEntry.super.handleStorageRead(value);
        // load available options for the select component
        ServiceUtils.getConstrutionService().loadConstructions(new AsyncCallback<Construction[]>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Construction[] result) {
                addOption(NOTHING_SELECTED);
                for (Construction construction : result) {
                    addOption(construction.getName());
                }

            }
        });
    }
}