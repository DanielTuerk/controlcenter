package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;

import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
abstract public class AbstractItemViewerPanel<ItemPanel extends AbstractItemPanel, EventType extends AbstractStateEvent> extends FlowPanel {

    private final Map<Long, ItemPanel> itemPanelByIdMap = Maps.newHashMap();
    private final VerticalPanel itemsContainerPanel = new VerticalPanel();

    public AbstractItemViewerPanel(Class<EventType> eventClass) {

        addStyleName("contentPanel");
        InputAddOn inputCreate = new InputAddOn();
        final TextBox txtName = new TextBox();
        inputCreate.add(txtName);
        Button btnNew = new Button("new");
        btnNew.addClickHandler(getBtnNewClickHandler(txtName));
        inputCreate.add(btnNew);

        add(inputCreate);
        add(itemsContainerPanel);

        /* Logic for GWTEventService starts here */
        //add a listener to the SERVER_MESSAGE_DOMAIN
        EventReceiver.getInstance().addListener(eventClass, new RemoteEventListener() {
            public void apply(Event anEvent) {
                //TODO
                EventType scenarioStateEvent = (EventType) anEvent;

                if (itemPanelByIdMap.containsKey(scenarioStateEvent.getItemId())) {
                    eventCallback(itemPanelByIdMap.get(scenarioStateEvent.getItemId()), scenarioStateEvent);
                } else {
                    net.wbz.moba.controlcenter.web.client.util.Log.console("event: can't find item " + scenarioStateEvent.getItemId());
                }
            }
        });
    }

    protected abstract void eventCallback(ItemPanel eventItem, EventType eventType);

    abstract protected ClickHandler getBtnNewClickHandler(TextBox name);

    @Override
    protected void onLoad() {
        super.onLoad();
        loadData();
    }

    protected void loadData() {
        itemsContainerPanel.clear();
        loadItems();
    }

    protected abstract void loadItems();

    public void addItemPanel(ItemPanel panel) {
        itemPanelByIdMap.put(panel.getModel().getId(), panel);
        itemsContainerPanel.add(panel);
    }

}
