package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.FlowPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;

/**
 * Abstract viewer panel for the items.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractItemViewerPanel<ItemPanel extends AbstractItemPanel, EventType extends AbstractStateEvent, DataEventType extends Event>
        extends FlowPanel {

    private final Map<Long, ItemPanel> itemPanelByIdMap = Maps.newHashMap();
    private final FlowPanel itemsContainerPanel = new FlowPanel();
    private final Map<Class<? extends EventType>, RemoteEventListener> eventListeners = new HashMap<>();
    private final RemoteEventListener deviceInfoEventListener;
    private RemoteEventListener dataChangeEventListener;

    public AbstractItemViewerPanel() {

        addStyleName("abstract-item-viewer-panel");

        addContent();

        registerItemEventListeners();

        registerDataListener();

        // add event receiver for the device connection state
        deviceInfoEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    for (ItemPanel itemPanel : itemPanelByIdMap.values()) {
                        if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                            itemPanel.deviceConnectionChanged(true);
                        } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                            itemPanel.deviceConnectionChanged(false);
                        }
                    }
                }
            }
        };

    }

    private void registerDataListener() {
        dataChangeEventListener = event -> loadData();
    }

    private void registerItemEventListeners() {
        for (Class<? extends EventType> stateEventClass : getStateEventClasses()) {
            eventListeners.put(stateEventClass, new RemoteEventListener() {
                public void apply(Event anEvent) {
                    // TODO
                    EventType eventType = (EventType) anEvent;

                    if (itemPanelByIdMap.containsKey(eventType.getItemId())) {
                        itemPanelByIdMap.get(eventType.getItemId()).updateItemData(eventType);
                    } else {
                        net.wbz.moba.controlcenter.web.client.util.Log.info("event: can't find item " + eventType
                                .getItemId());
                        // reload all if no specific entry was updated
//                        loadData();
                    }
                }
            });
        }
    }

    protected void addContent() {
        add(itemsContainerPanel);
    }

    abstract protected List<Class<? extends EventType>> getStateEventClasses();

    abstract protected Class<DataEventType> getDataEventClass();

    @Override
    protected void onLoad() {
        super.onLoad();

        EventReceiver.getInstance().addListener(getDataEventClass(), dataChangeEventListener);

        addListeners();

        loadData();

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceInfoEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(getDataEventClass(), dataChangeEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceInfoEventListener);

        removeListeners();

        resetItems();
    }

    protected void addListeners() {
        for (Map.Entry<Class<? extends EventType>, RemoteEventListener> eventListenerEntry : eventListeners
                .entrySet()) {
            EventReceiver.getInstance().addListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }
    }

    private void loadData() {
        resetItems();
        loadItems();
    }

    protected void resetItems() {
        itemsContainerPanel.clear();
    }

    protected void removeListeners() {
        for (Map.Entry<Class<? extends EventType>, RemoteEventListener> eventListenerEntry : eventListeners
                .entrySet()) {
            EventReceiver.getInstance().removeListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }
    }

    protected abstract void loadItems();

    public void addItemPanel(ItemPanel panel) {
        panel.init();
        itemPanelByIdMap.put(panel.getModel().getId(), panel);
        itemsContainerPanel.add(panel);
    }

}
