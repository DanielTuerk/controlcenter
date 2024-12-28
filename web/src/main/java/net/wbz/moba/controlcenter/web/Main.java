package net.wbz.moba.controlcenter.web;

import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.web.guice.FrontendServlet;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

/**
 * @author Daniel Tuerk
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        int port = 8080;
        log.info("starting server on port {}", port);

        Server server = new Server(port);
        ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

        root.addEventListener(new MyGuiceServletConfig());
        root.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        root.addServlet(FrontendServlet.class, "/*");

        try {
            server.start();
            log.info("server started on port {}", port);
        } catch (Exception e) {
            log.error("can't start server", e);
            throw new RuntimeException(e);
        }
    }
}
