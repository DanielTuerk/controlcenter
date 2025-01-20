package net.wbz.moba.controlcenter.web;

import com.google.inject.AbstractModule;
import net.wbz.moba.controlcenter.web.server.ServerModule;

/**
 * @author Daniel Tuerk
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();
        install(new ServerModule());
    }

}
