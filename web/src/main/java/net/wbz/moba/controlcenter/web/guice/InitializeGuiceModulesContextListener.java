package net.wbz.moba.controlcenter.web.guice;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import java.util.Locale;
import javax.servlet.ServletContextEvent;
import net.wbz.moba.controlcenter.web.Main;

/**
 * @author Daniel Tuerk
 */
public class InitializeGuiceModulesContextListener extends GuiceServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Locale.setDefault(Locale.ENGLISH);
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Main.getGuiceInjector();
    }

}
