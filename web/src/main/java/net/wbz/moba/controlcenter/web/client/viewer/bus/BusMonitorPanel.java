package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.WellSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The BusMonitor shows the current data of each address for bus 0 and 1. The
 * monitor appears when it`s connected to the bus otherwise an information will
 * be shown.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusMonitorPanel extends FlowPanel {
    /**
     * Store the {@link BusAddressItemPanel} for each address in bus.
     */
    private final Map<Integer, Map<Integer, BusAddressItemPanel>> addressItemMapping = Maps
            .newHashMap();
    private RemoteEventListener listener;
    private RemoteEventListener connectionListener;
    private List<Panel> busPanels = new ArrayList<>();
    final Well wellConnectionState = new Well();
    final Label lblConnectionState = new Label();

    /**
     * Constructs panels and initializes the widgets of a bus item panel.
     */
    public BusMonitorPanel() {
        super();

        getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);

        lblConnectionState.setText("Sorry, there is no connection. Please connect.");
        lblConnectionState.addStyleName("lbl-well");
        wellConnectionState.setSize(WellSize.LARGE);
        wellConnectionState.add(lblConnectionState);
        wellConnectionState.addStyleName("well-conn");

        setHeight("100%");
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

            Map<Integer, BusAddressItemPanel> addressItemPanelMap = Maps
                    .newHashMap();
            for (int j = 0; j < 112; j++) {

                BusAddressItemPanel busAddressItemPanel = new BusAddressItemPanel(
                        i, j);
                busAddressItemPanel.getElement().getStyle()
                        .setPaddingRight(5, Style.Unit.PX);
                busAddressItemPanel.getElement().getStyle()
                        .setPaddingBottom(5, Style.Unit.PX);
                panelBody.add(busAddressItemPanel);

                addressItemPanelMap.put(j, busAddressItemPanel);
            }
            addressItemMapping.put(i, addressItemPanelMap);
        }
        // listener processes events on user side of busData
        listener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                BusDataEvent busDataEvent = (BusDataEvent) anEvent;
                addressItemMapping.get(busDataEvent.getBus())
                        .get(busDataEvent.getAddress())
                        .updateData(busDataEvent.getData());
            }
        };
        // connectionListener processes events on user side of deviceInfo
        connectionListener = new RemoteEventListener() {
            // checks incoming event if device is connected or not
            // and handle another methods depending on connection
            @Override
            public void apply(Event event) {
                DeviceInfoEvent deviceInfoEvent = (DeviceInfoEvent) event;

                if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {

                    ServiceUtils.getBusService().startTrackingBus(
                            new EmptyCallback<Void>());
                    remove(wellConnectionState);
                    addBusPanels();

                } else if (deviceInfoEvent.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                    ServiceUtils.getBusService().stopTrackingBus(
                            new EmptyCallback<Void>());
                    removeBusPanels();
                    add(wellConnectionState);
                }
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, connectionListener);

        ServiceUtils.getBusService().isBusConnected(
                new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {

                            remove(wellConnectionState);
                            addBusPanels();

                        } else {
                            removeBusPanels();
                            add(wellConnectionState);
                        }
                    }
                });

        ServiceUtils.getBusService().startTrackingBus(new EmptyCallback<Void>());
        EventReceiver.getInstance().addListener(BusDataEvent.class, listener);

        wellConnectionState.getElement().getStyle().setMarginLeft(getParent().getOffsetWidth() / 2 - 130, Style.Unit.PX);
        wellConnectionState.getElement().getStyle().setMarginTop(getParent().getOffsetHeight() / 2 - 50, Style.Unit.PX);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        ServiceUtils.getBusService().stopTrackingBus(new EmptyCallback<Void>());
        EventReceiver.getInstance().removeListener(BusDataEvent.class, listener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, connectionListener);
        removeBusPanels();
    }

    /**
     * Removes busPanels from BusMonitor.
     */
    public void removeBusPanels() {
        for (Panel busPanel : busPanels) {
            remove(busPanel);
        }
    }

    /**
     * Adds busPanels to BusMonitor.
     */
    public void addBusPanels() {
        for (Panel busPanel : busPanels) {
            add(busPanel);
        }
    }
}
