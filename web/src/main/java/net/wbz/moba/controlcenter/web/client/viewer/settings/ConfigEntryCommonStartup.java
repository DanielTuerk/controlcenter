package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.shared.config.ConfigEntryItem;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.constants.FormType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class ConfigEntryCommonStartup extends AbstractCommonConfigEntry {

    private Form form;


    @Override
    public String getName() {
        return "Startup";
    }




    @Override
    protected Panel getContentPanel() {

        registerConfigEntry("showWelcome", ConfigEntryItem.ItemType.BOOLEAN);


        ServiceUtils.getConfigService().load(getConfigGroupKey(), new AsyncCallback<ConfigEntryItem[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.error("can't load config of group: " + getConfigGroupKey() + " - " + throwable.getMessage());
            }

            @Override
            public void onSuccess(ConfigEntryItem[] configEntryItems) {

                for (ConfigEntryItem configEntryItem : configEntryItems) {
                    for (ConfigEntryItem registeredConfigEntryItem : ConfigEntryCommonStartup.this.configEntryItems) {
                        updateConfigEntry(configEntryItem);
                    }
                }
            }
        });


    }





    @Override
    protected void save() {

    }

    @Override
    protected void reset() {

    }
}
