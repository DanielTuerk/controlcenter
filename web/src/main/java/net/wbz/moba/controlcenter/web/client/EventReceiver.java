package net.wbz.moba.controlcenter.web.client;

import com.google.common.collect.Maps;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class EventReceiver {

    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = Maps.newHashMap();
    private final RemoteEventService theRemoteEventService;

    private final static EventReceiver instance = new EventReceiver();

    public static EventReceiver getInstance() {
        return instance;
    }

    private EventReceiver() {
        theRemoteEventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();
    }

    public void addListener(Class<? extends Event> eventClazz, RemoteEventListener listener) {
        theRemoteEventService.addListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }
    public void removeListener(Class<? extends Event> eventClazz, RemoteEventListener listener ){
        theRemoteEventService.removeListener(DomainFactory.getDomain(eventClazz.getName()), listener);
    }
}
