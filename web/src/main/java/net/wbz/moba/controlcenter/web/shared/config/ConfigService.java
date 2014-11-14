package net.wbz.moba.controlcenter.web.shared.config;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("config")
public interface ConfigService extends RemoteService {

    static ConfigEntryItem showWelcome = new ConfigEntryItem("manage.startup.showWelcome", ConfigEntryItem.ItemType.BOOLEAN);
    public static String MANAGE_STARTUP_WELCOME=showWelcome.getKey();

    public void save(ConfigEntryItem[] item);
    public ConfigEntryItem[] load(String group);
    public ConfigEntryItem getValue(String key);
}
