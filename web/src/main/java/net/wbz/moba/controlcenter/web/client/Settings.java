package net.wbz.moba.controlcenter.web.client;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.viewer.settings.AbstractConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.BooleanConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConstructionSelectionConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.SelectionConfigEntry;

/**
 * @author Daniel Tuerk
 */
public class Settings {

    public static final String SHOW_WELCOME = "showWelcome";
    public static final String USE_3D_VIEWER = "use3dViewer";
    public static final String LAST_USED_CONSTRUCTION = "lastUsedConstruction";
    private static final Settings INSTANCE = new Settings();
    private Map<String, AbstractConfigEntry<?>> configEntriesByKey = Maps.newConcurrentMap();

    private Settings() {
        String GROUP_CONSTRUCTION = "construction";
        configEntriesByKey.put(LAST_USED_CONSTRUCTION,
            new ConstructionSelectionConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_CONSTRUCTION,
                LAST_USED_CONSTRUCTION));
        String GROUP_COMMON = "common";
        configEntriesByKey.put(SHOW_WELCOME,
            new BooleanConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_COMMON, SHOW_WELCOME, true));
        configEntriesByKey.put(USE_3D_VIEWER,
            new BooleanConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_COMMON, USE_3D_VIEWER, false));
    }

    public static Settings getInstance() {
        return INSTANCE;
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
