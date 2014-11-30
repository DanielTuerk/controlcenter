package net.wbz.moba.controlcenter.web.shared.config;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("config")
public interface ConfigService extends RemoteService {

    public String loadValue(String configKey) throws ConfigNotAvailableException;
    public void saveValue(String configKey, String value);

//    public void save(ConfigEntryItem[] item);
//    public ConfigEntryItem[] load(String group);
//    public String getValue(ConfigEntryItem item);
//    public void setValue(ConfigEntryItem item, String value);
//    public void setValues(ConfigEntryItem[] item, String[] value);
}
