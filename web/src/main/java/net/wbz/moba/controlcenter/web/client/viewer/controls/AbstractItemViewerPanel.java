package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.TextBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
abstract public class AbstractItemViewerPanel<ItemPanel extends AbstractItemPanel, EventType extends AbstractStateEvent> extends FlowPanel {

    private final Map<Long, ItemPanel> itemPanelByIdMap = Maps.newHashMap();
    private final FlowPanel itemsContainerPanel = new FlowPanel();
    private final Map<Class<EventType>, RemoteEventListener> eventListeners = new HashMap<>();

    private RemoteEventListener trainDataChangedEventListener;

    public AbstractItemViewerPanel() {

        addStyleName("contentPanel");

        InputGroup inputCreate = new InputGroup();
        inputCreate.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        final TextBox txtName = new TextBox();
        inputCreate.add(txtName);
        InputGroupButton groupButton = new InputGroupButton();
        Button btnNew = new Button("new");
        btnNew.addClickHandler(getBtnNewClickHandler(txtName));
        groupButton.add(btnNew);
        inputCreate.add(groupButton);

        add(inputCreate);

        // TODO: scrollable container
        add(itemsContainerPanel);

        for (Class<EventType> stateEventClass : getStateEventClasses()) {
            eventListeners.put(stateEventClass, new RemoteEventListener() {
                public void apply(Event anEvent) {
                    //TODO
                    EventType eventType = (EventType) anEvent;

                    if (itemPanelByIdMap.containsKey(eventType.getItemId())) {
                        itemPanelByIdMap.get(eventType.getItemId()).updateItemData(eventType);
                    } else {
                        net.wbz.moba.controlcenter.web.client.util.Log.info("event: can't find item " + eventType.getItemId());
                    }
                }
            });
        }
        trainDataChangedEventListener = new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                loadData();
            }
        };

    }

    abstract protected List<Class<EventType>> getStateEventClasses();

    abstract protected ClickHandler getBtnNewClickHandler(TextBox name);

    @Override
    protected void onLoad() {
        super.onLoad();


        EventReceiver.getInstance().addListener(TrainDataChangedEvent.class, trainDataChangedEventListener);

        for (Map.Entry<Class<EventType>, RemoteEventListener> eventListenerEntry : eventListeners.entrySet()) {
            EventReceiver.getInstance().addListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }

        loadData();
    }

    private void loadData() {
        resetItems();
        loadItems();
    }

    private void resetItems() {
        itemsContainerPanel.clear();
    }

    @Override
    protected void onUnload() {
        super.onUnload();


        for (Map.Entry<Class<EventType>, RemoteEventListener> eventListenerEntry : eventListeners.entrySet()) {
            EventReceiver.getInstance().removeListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }

        EventReceiver.getInstance().removeListener(TrainDataChangedEvent.class, trainDataChangedEventListener);


        resetItems();
    }

    protected abstract void loadItems();

    public void addItemPanel(ItemPanel panel) {
        panel.init();
        itemPanelByIdMap.put(panel.getModel().getId(), panel);
        itemsContainerPanel.add(panel);
    }

}
