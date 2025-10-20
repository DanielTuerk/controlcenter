package net.wbz.moba.controlcenter.web.server;

import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent.Type;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ExceptionLogger implements ApplicationEventListener, RequestEventListener {

    private static final Logger log = Logger.getLogger(ExceptionLogger.class);

    @Override
    public void onEvent(final ApplicationEvent applicationEvent) {
    }

    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        return this;
    }

    @Override
    public void onEvent(RequestEvent paramRequestEvent) {
        if (paramRequestEvent.getType() == Type.ON_EXCEPTION) {
            log.error("", paramRequestEvent.getException());
        }
    }
}