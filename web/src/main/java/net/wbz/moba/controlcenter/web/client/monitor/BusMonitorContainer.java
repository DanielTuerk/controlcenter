package net.wbz.moba.controlcenter.web.client.monitor;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.HashMap;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;

/**
 * @author Daniel Tuerk
 */
 class BusMonitorContainer extends Composite {

    private static final int ADDRESSES_COUNT = 112;
    private static Binder uiBinder = GWT.create(Binder.class);
    /**
     * Store the {@link BusAddressItem} for each address in bus.
     */
    private final Map<Integer, Map<Integer, BusAddressItem>> addressItemMapping = new HashMap<>();
    private final RemoteEventListener listener;
    @UiField
    HTMLPanel bus0;
    @UiField
    HTMLPanel bus1;

     BusMonitorContainer() {
        initWidget(uiBinder.createAndBindUi(this));

        // listener processes events on user side of busData
        listener = anEvent -> {
            BusDataEvent busDataEvent = (BusDataEvent) anEvent;
            Map<Integer, BusAddressItem> busMapping = addressItemMapping.get(busDataEvent.getBus());
            if (busMapping.containsKey(busDataEvent.getAddress())) {
                busMapping.get(busDataEvent.getAddress()).updateData(busDataEvent.getData());
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addBus(0, bus0);
        addBus(1, bus1);

        RequestUtils.getInstance().getBusService().startTrackingBus(RequestUtils.VOID_ASYNC_CALLBACK);
        EventReceiver.getInstance().addListener(BusDataEvent.class, listener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        RequestUtils.getInstance().getBusService().stopTrackingBus(RequestUtils.VOID_ASYNC_CALLBACK);
        EventReceiver.getInstance().removeListener(BusDataEvent.class, listener);
    }

    private void addBus(int i, HasWidgets panelBody) {
        Map<Integer, BusAddressItem> addressItemPanelMap = Maps.newHashMap();
        for (int j = 0; j < ADDRESSES_COUNT; j++) {

            BusAddressItem busAddressItemPanel = new BusAddressItem(i, j);
            panelBody.add(busAddressItemPanel);

            addressItemPanelMap.put(j, busAddressItemPanel);
        }
        addressItemMapping.put(i, addressItemPanelMap);
    }

    interface Binder extends UiBinder<Widget, BusMonitorContainer> {

    }

}
