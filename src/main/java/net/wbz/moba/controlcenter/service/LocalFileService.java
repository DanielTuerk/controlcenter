package net.wbz.moba.controlcenter.service;

import jakarta.inject.Singleton;
import java.io.File;
import java.nio.file.Path;
import net.wbz.moba.controlcenter.service.constrution.ConstructionManager;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class LocalFileService {

    private static final Logger LOG = Logger.getLogger(ConstructionManager.class);
    private static final String ROOT_FOLDER = "/.moba/";

    private final String userHome;

    public LocalFileService() {
        userHome = absolutePath(System.getProperty("user.home") + ROOT_FOLDER);
    }

    public Path getDir(String path) {
        // TODO ugly
        return Path.of(absolutePath(Path.of(userHome, path).toFile().getAbsolutePath()));
    }

    private String absolutePath(String pathname) {
        File configPath = new File(pathname);
        if (!configPath.exists()) {
            if (!configPath.mkdirs()) {
                throw new RuntimeException("can't create the path: " + configPath.getAbsolutePath());
            }
        }
        return configPath.getAbsolutePath();
    }
}
