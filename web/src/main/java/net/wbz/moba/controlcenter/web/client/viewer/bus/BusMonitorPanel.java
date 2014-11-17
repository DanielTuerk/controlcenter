package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;

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
            	Label lbl_address = new Label("Address");
                BusAddressItemPanel busAddressItemPanel = new BusAddressItemPanel(i,j);
                busAddressItemPanel.add(lbl_address);
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
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        for (Panel busPanel : busPanels) {
            add(busPanel);
        }
        EventReceiver.getInstance().addListener(BusDataEvent.class, listener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(BusDataEvent.class, listener);
        for (Panel busPanel : busPanels) {
            remove(busPanel);
        }
    }
}
