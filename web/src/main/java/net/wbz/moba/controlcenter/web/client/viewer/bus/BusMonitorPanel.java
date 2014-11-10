package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.bus.BusDataEvent;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.TabPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusMonitorPanel extends TabPanel {

    private final Map<Integer, Map<Integer, BusAddressItemPanel>> addressItemMapping = Maps.newHashMap();
    private final RemoteEventListener listener;

    public BusMonitorPanel() {
        super();
        setTabPosition(TabPosition.LEFT);

        for (int i = 0; i < 2; i++) {
            Panel tabContent = new FlowPanel();


            Map<Integer, BusAddressItemPanel> addressItemPanelMap = Maps.newHashMap();

            Row row = null;
            List<BusAddressItemPanel> panelsInRow = new ArrayList<>();

            for (int j = 0; j < 127; j++) {
                BusAddressItemPanel value = new BusAddressItemPanel(j);
                addressItemPanelMap.put(j, value);
                panelsInRow.add(value);

                if (j % 6 == 0) {
                    row = new Row();
                    tabContent.add(row);
                    Widget firstItem = panelsInRow.get(0);
                    panelsInRow.remove(0);
                    Column column = new Column(ColumnSize.LG_2, firstItem, panelsInRow.toArray(new BusAddressItemPanel[panelsInRow.size()]));
                    row.add(column);
                    panelsInRow.clear();
                }

            }
            addressItemMapping.put(i, addressItemPanelMap);

            TabPane tab = new TabPane();
            tab.setTitle("Bus " + i);
            tab.add(new ScrollPanel(tabContent));
            tab.setActive(i == 0);
            add(tab);
        }

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
