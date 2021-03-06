package net.wbz.moba.controlcenter.web.client.settings.entry;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.settings.Settings.GROUP;
import net.wbz.moba.controlcenter.web.client.settings.Settings.STORAGE;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * Dropdown for the name of the {@link net.wbz.moba.controlcenter.web.shared.constrution.Construction}s.
 *
 * @author Daniel Tuerk
 */
public class ConstructionSelectionConfigEntry extends SelectionConfigEntry {

    @Override
    public void setValueAndSave(String value) {
        super.setValueAndSave(value);
    }

    public ConstructionSelectionConfigEntry(STORAGE storageType, GROUP group, String name) {
        super(storageType, group, name);
    }

    @Override
    public void reset() {
        clear();
        RequestUtils.getInstance().getConstructionService().loadConstructions(
            new AsyncCallback<Collection<Construction>>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(Collection<Construction> result) {

                    addOption(NOTHING_SELECTED);
                    for (Construction construction : result) {
                        addOption(construction.getName());
                    }

                    ConstructionSelectionConfigEntry.super.reset();
                }
            });
    }
}
