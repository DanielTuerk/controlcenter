package net.wbz.moba.controlcenter.web.client.settings;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.client.settings.entry.AbstractConfigEntry;
import net.wbz.moba.controlcenter.web.client.settings.entry.BooleanConfigEntry;
import net.wbz.moba.controlcenter.web.client.settings.entry.ConstructionSelectionConfigEntry;
import net.wbz.moba.controlcenter.web.client.settings.entry.SelectionConfigEntry;

/**
 * @author Daniel Tuerk
 */
public class Settings {

    public enum GROUP {
        COMMON, CONSTRUCTION
    }

    public enum STORAGE {
        LOCAL, REMOTE
    }

    private static final String SHOW_WELCOME = "showWelcome";
    private static final String USE_3D_VIEWER = "use3dViewer";
    private static final Settings INSTANCE = new Settings();

    private Map<String, AbstractConfigEntry<?>> configEntriesByKey = Maps.newConcurrentMap();
    private static final String LAST_USED_CONSTRUCTION = "lastUsedConstruction";

    private Settings() {
        configEntriesByKey.put(LAST_USED_CONSTRUCTION,
            new ConstructionSelectionConfigEntry(STORAGE.LOCAL, GROUP.CONSTRUCTION,
                LAST_USED_CONSTRUCTION));
        configEntriesByKey.put(SHOW_WELCOME,
            new BooleanConfigEntry(STORAGE.LOCAL, GROUP.COMMON, SHOW_WELCOME, true));
        configEntriesByKey.put(USE_3D_VIEWER,
            new BooleanConfigEntry(STORAGE.LOCAL, GROUP.COMMON, USE_3D_VIEWER, false));
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    public Collection<AbstractConfigEntry<?>> getGroupEntries(GROUP group) {
        return getEntries().stream().filter(x -> x.getGroup().equals(group))
            .collect(Collectors.toList());
    }

    public Collection<AbstractConfigEntry<?>> getEntries() {
        return configEntriesByKey.values();
    }

    public BooleanConfigEntry getShowWelcome() {
        return (BooleanConfigEntry) configEntriesByKey.get(SHOW_WELCOME);
    }

    public SelectionConfigEntry getLastUsedConstruction() {
        return (SelectionConfigEntry) configEntriesByKey.get(LAST_USED_CONSTRUCTION);
    }

    public BooleanConfigEntry getUse3dViewer() {
        return (BooleanConfigEntry) configEntriesByKey.get(USE_3D_VIEWER);
    }


}
