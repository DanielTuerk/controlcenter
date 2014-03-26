package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.BusDataEvent;

import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusMonitorPanel extends TabPanel {

    private final Map<Integer, Map<Integer, BusAddressItemPanel>> addressItemMapping = Maps.newHashMap();
    private final RemoteEventListener listener;

    public BusMonitorPanel() {
        super(Bootstrap.Tabs.LEFT);

        for (int i = 0; i < 2; i++) {
            Panel tabContent = new FlowPanel();



            Map<Integer, BusAddressItemPanel> addressItemPanelMap = Maps.newHashMap();

            Row row = null;
            for (int j = 0; j < 127; j++) {

                if (j % 6 == 0) {
                    row = new Row();
                    tabContent.add(row);
                }


                BusAddressItemPanel value = new BusAddressItemPanel(j);
                addressItemPanelMap.put(j, value);

                Column column = new Column(2, value);
                row.add(column);

            }
            addressItemMapping.put(i, addressItemPanelMap);

            TabPane tab = new TabPane();
            tab.setHeading("Bus " + i);
            tab.add(tabContent);
            add(tab);
        }

        selectTab(0);

        listener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                //TODO
                BusDataEvent busDataEvent = (BusDataEvent) anEvent;
                addressItemMapping.get(busDataEvent.getBus()).get(busDataEvent.getAddress()).updateData(busDataEvent.getData());
            }
        };


    }


    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(BusDataEvent.class, listener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(BusDataEvent.class, listener);
    }
}
