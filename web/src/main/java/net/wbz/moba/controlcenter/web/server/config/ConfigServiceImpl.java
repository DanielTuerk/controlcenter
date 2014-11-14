package net.wbz.moba.controlcenter.web.server.config;

import com.db4o.ObjectSet;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.db.Database;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.web.shared.config.ConfigEntryItem;
import net.wbz.moba.controlcenter.web.shared.config.ConfigService;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

    private static final String CONFIG_DB_KEY = "config";
    private final Database configDatabase;

    private transient boolean configUpToDate = false;
    private final List<ConfigEntryItem> cachedConfig = new ArrayList<>();

    @Inject
    public ConfigServiceImpl(@Named("settings") DatabaseFactory databaseFactory) {
        configDatabase = databaseFactory.getOrCreateDatabase(CONFIG_DB_KEY);
    }

    @Override
    public void save(ConfigEntryItem[] items) {
        for (ConfigEntryItem item : items) {
            configDatabase.getObjectContainer().store(item);
            configUpToDate = false;
        }
    }

    @Override
    public ConfigEntryItem[] load(final String group) {
        cachedConfig.clear();
        ObjectSet<ConfigEntryItem> configItems = configDatabase.getObjectContainer().query(ConfigEntryItem.class);
        cachedConfig.addAll(configItems);

        Collection<ConfigEntryItem> result = Collections2.filter(cachedConfig, new Predicate<ConfigEntryItem>() {
            @Override
            public boolean apply(@Nullable ConfigEntryItem item) {
                return item.getKey().startsWith(group);
            }
        });
        return result.toArray(new ConfigEntryItem[result.size()]);
    }
}
