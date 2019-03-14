package net.wbz.moba.controlcenter.web.server;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.web.WebModule;

/**
 * @author Daniel Tuerk
 */
public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new WebModule());
    }
}
