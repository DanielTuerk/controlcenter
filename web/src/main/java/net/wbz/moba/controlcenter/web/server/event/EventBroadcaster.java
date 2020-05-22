package net.wbz.moba.controlcenter.web.server.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Inject;
import net.wbz.moba.controlcenter.web.client.event.StateEvent;
import net.wbz.moba.controlcenter.web.shared.EventCache;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Broadcaster for the events to throw by the {@link EventExecutorService}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class EventBroadcaster {

    private static final Logger LOG = LoggerFactory.getLogger(EventBroadcaster.class);

    private final EventExecutorService eventExecutorService;
    private final ExecutorService taskExecutor;
    private final EventCache eventCache;

    /**
     * Create broadcaster with singleton {@link EventExecutorService}.
     * @param eventCache {@link EventCache}
     */
    @Inject
    public EventBroadcaster(EventCache eventCache, DeviceManager deviceManager) {
        this.eventCache = eventCache;
        EventExecutorServiceFactory theSF = EventExecutorServiceFactory.getInstance();
        eventExecutorService = theSF.getEventExecutorService("event");

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d")
            .build();
        // TODO shutdown
        taskExecutor = Executors.newCachedThreadPool(namedThreadFactory);

        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {

            }

            @Override
            public void disconnected(Device device) {
                LOG.info("device disconnected, clear event cache");
               eventCache.clear();
            }
        });
    }

    /***
     * Fire the given event to client.
     *
     * @param event {@link Event}
     */
    public synchronized void fireEvent(Event event) {
        if (event.getClass() != BusDataEvent.class) {
            // avoid log spam
            LOG.debug("fire Event: " + event.toString());
        }
        sendEvent(event);

        saveLastSendEvent(event);
    }

    /**
     * Send again the last cached event of the given class to the clients.
     *
     * @param eventClazzName name of the event class
     */
    public synchronized void resendEvent(final String eventClazzName) {
        Collection<Event> stateEvents = eventCache.getEvents(eventClazzName);
        if (!stateEvents.isEmpty()) {
            taskExecutor.submit(new ResendEventRunnable(eventClazzName, stateEvents, eventExecutorService));
        }
    }

    private void sendEvent(Event event) {
        eventExecutorService.addEvent(DomainFactory.getDomain(event.getClass().getName()), event);
    }

    private synchronized void saveLastSendEvent(Event event) {
        if (event instanceof StateEvent) {
            eventCache.addEvent((StateEvent) event);
        }
    }

}