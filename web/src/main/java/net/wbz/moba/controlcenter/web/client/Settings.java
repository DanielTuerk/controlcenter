package net.wbz.moba.controlcenter.web.client;

import net.wbz.moba.controlcenter.web.client.viewer.settings.AbstractConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.BooleanConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.ConstructionSelectionConfigEntry;
import net.wbz.moba.controlcenter.web.client.viewer.settings.SelectionConfigEntry;

/**
 * @author Daniel Tuerk
 */
public class Settings {

    public static Settings getInstance() {
        return INSTANCE;
    }

    private static String GROUP_COMMON = "common";

    private static String GROUP_CONSTRUCTION = "construction";

    private static final Settings INSTANCE = new Settings();

    private SelectionConfigEntry lastUsedConstruction;

    private BooleanConfigEntry showWelcome;

    private Settings() {
        lastUsedConstruction = new ConstructionSelectionConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_CONSTRUCTION, "lastUsedConstruction");
        showWelcome = new BooleanConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_COMMON, "showWelcome", true);
    }

    public BooleanConfigEntry getShowWelcome() {
        return showWelcome;
    }

    public SelectionConfigEntry getLastUsedConstruction() {
        return lastUsedConstruction;
    }

}
