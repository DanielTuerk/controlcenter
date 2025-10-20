package net.wbz.moba.controlcenter.web.jersey;


import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import net.wbz.moba.controlcenter.web.guice.InitializeGuiceModulesContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author Daniel Tuerk
 */
public class JerseyBootstrapper {

    @Inject
    @Named("serverPort")
    private int port;

    private Server jettyServer;

    public JerseyBootstrapper(Injector injector) {
        injector.injectMembers(this);
    }

    /**
     * Init jersey server from code instead of a web.xml
     */
    public void setupServer() {
        this.jettyServer = new Server(this.port);

        HandlerCollection handlerCollection = new HandlerCollection();

        // WebApp handler
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(this.jettyServer);

        // Guice filter
        webAppContext.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("javax.ws.rs.Application", JerseyConfiguration.class.getCanonicalName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setResourceBase("/");
        webAppContext.setContextPath("/");
        webAppContext.addEventListener(new InitializeGuiceModulesContextListener());

        // Add all handlers
        handlerCollection.addHandler(webAppContext);

        // Set all handlers to jetty
        this.jettyServer.setHandler(handlerCollection);
    }

    public void startServer() throws Exception {
        this.jettyServer.start();
        this.jettyServer.join();
    }

    public void stopServer() throws Exception {
        this.jettyServer.stop();
    }

    public void destroyServer() {
        this.jettyServer.destroy();
    }
}