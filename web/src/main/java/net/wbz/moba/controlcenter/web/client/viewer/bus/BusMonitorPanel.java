package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusMonitorPanel extends FlowPanel {

    private final Map<Integer, Map<Integer, BusAddressItemPanel>> addressItemMapping = Maps.newHashMap();
    private RemoteEventListener listener;
    private RemoteEventListener connectionListener;
    private List<Panel> busPanels = new ArrayList<>();

    public BusMonitorPanel() {
        super();

        setHeight("100%");
        getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);

        for (int i = 0; i < 2; i++) {
            Panel panel = new org.gwtbootstrap3.client.ui.Panel();
            panel.setWidth("48%");
            panel.getElement().getStyle().setFloat(Style.Float.LEFT);
            panel.getElement().getStyle().setMargin(1, Style.Unit.PCT);

            PanelHeader panelHeader = new PanelHeader();
            panelHeader.setText("Bus " + i);
            PanelBody panelBody = new PanelBody();
            panel.add(panelHeader);
            panel.add(panelBody);
            busPanels.add(panel);

            Map<Integer, BusAddressItemPanel> addressItemPanelMap = Maps.newHashMap();

            for (int j = 0; j < 127; j++) {
                BusAddressItemPanel busAddressItemPanel = new BusAddressItemPanel(j);
                busAddressItemPanel.getElement().getStyle().setPaddingRight(5, Style.Unit.PX);
                busAddressItemPanel.getElement().getStyle().setPaddingBottom(5, Style.Unit.PX);
                panelBody.add(busAddressItemPanel);

                addressItemPanelMap.put(j, busAddressItemPanel);
            }
            addressItemMapping.put(i, addressItemPanelMap);
        }

        listener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                BusDataEvent busDataEvent = (BusDataEvent) anEvent;
                addressItemMapping.get(busDataEvent.getBus()).get(busDataEvent.getAddress()).updateData(busDataEvent.getData());
            }
        };
        connectionListener = new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                DeviceInfoEvent deviceInfoEvent = (DeviceInfoEvent) event;
                if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                    ServiceUtils.getBusService().startTrackingBus(new EmptyCallback<Void>());
                } else if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                    ServiceUtils.getBusService().stopTrackingBus(new EmptyCallback<Void>());
                }
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        for (Panel busPanel : busPanels) {
            add(busPanel);
        }
        ServiceUtils.getBusService().startTrackingBus(new EmptyCallback<Void>());
        EventReceiver.getInstance().addListener(BusDataEvent.class, listener);
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, connectionListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        ServiceUtils.getBusService().stopTrackingBus(new EmptyCallback<Void>());
        EventReceiver.getInstance().removeListener(BusDataEvent.class, listener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, connectionListener);
        for (Panel busPanel : busPanels) {
            remove(busPanel);
        }
    }
}
