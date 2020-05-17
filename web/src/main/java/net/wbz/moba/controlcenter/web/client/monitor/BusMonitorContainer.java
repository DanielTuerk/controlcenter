package net.wbz.moba.controlcenter.web.client.monitor;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.event.bus.BusDataRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;

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
    private final BusDataRemoteListener listener;
    @UiField
    HTMLPanel bus0;
    @UiField
    HTMLPanel bus1;

     BusMonitorContainer() {
        initWidget(uiBinder.createAndBindUi(this));

        // listener processes events on user side of busData
         listener = busDataEvent -> {
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
        EventReceiver.getInstance().addListener(listener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        RequestUtils.getInstance().getBusService().stopTrackingBus(RequestUtils.VOID_ASYNC_CALLBACK);
        EventReceiver.getInstance().removeListener(listener);
    }

    private void addBus(int busNr, HasWidgets panelBody) {
        Map<Integer, BusAddressItem> addressItemPanelMap = Maps.newHashMap();
        for (int address = 0; address < ADDRESSES_COUNT; address++) {

            BusAddressItem busAddressItemPanel = new BusAddressItem(busNr, address);
            panelBody.add(busAddressItemPanel);

            addressItemPanelMap.put(address, busAddressItemPanel);
        }
        addressItemMapping.put(busNr, addressItemPanelMap);
    }

    interface Binder extends UiBinder<Widget, BusMonitorContainer> {

    }

}
