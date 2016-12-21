package net.wbz.moba.controlcenter.web.shared.config;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("config")
public interface ConfigService extends RemoteService {

    String loadValue(String configKey) throws ConfigNotAvailableException;

    void saveValue(String configKey, String value);
}
