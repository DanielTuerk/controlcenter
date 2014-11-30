package net.wbz.moba.controlcenter.web.client;

import net.wbz.moba.controlcenter.web.client.viewer.settings.*;

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

    private SelectionConfigEntry lastUsedConstruction = new ConstructionSelectionConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_CONSTRUCTION, "lastUsedConstruction");

    private BooleanConfigEntry showWelcome = new BooleanConfigEntry(AbstractConfigEntry.STORAGE.LOCAL, GROUP_COMMON, "showWelcome", true);

    public BooleanConfigEntry getShowWelcome() {
        return showWelcome;
    }

    public SelectionConfigEntry getLastUsedConstruction() {
        return lastUsedConstruction;
    }

}
