package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import org.gwtbootstrap3.client.ui.Label;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * This panel represents the data of a single bus address in the
 * {@link BusMonitorPanel}. The bus address data can be manipulated by changing
 * the state of bits.
 * <p/>
 * Responding of incoming data by graphical behaviour.
 *
 * @author Daniel Tuerk
 */
public class BusAddressItemPanel extends FlowPanel {
    /**
     * Represents the  address value as 8 bits.
     */
    private final Map<Integer, ItemPanelButton> bitButtons = new HashMap<>();

    /**
     * Construct the panel and initialize the widgets of bus address.
     *
     * @param busNr   number of the bus
     * @param address an address of the bus
     */
    public BusAddressItemPanel(final int busNr, final int address) {
        getElement().getStyle().setFloat(Style.Float.LEFT);
        Label lblAddress = new Label(String.valueOf(address));
        lblAddress.addStyleName("busMonitor-address-lbl");

        add(lblAddress);
        for (int i = 8; i > 0; i--) {
            // FlowPanel is created to format buttons and labels of bits
            FlowPanel bitPanel = new FlowPanel();
            FlowPanel bitHeaderPanel = new FlowPanel();
            FlowPanel bitValuesPanel = new FlowPanel();
            bitPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
            bitPanel.add(bitHeaderPanel);
            bitPanel.add(bitValuesPanel);
            add(bitPanel);

            Label lbl = new Label(String.valueOf(i));
            bitHeaderPanel.add(lbl);
            lbl.addStyleName("busMonitor-lbl-bits");

            final ItemPanelButton itemPanelButton = new ItemPanelButton();
            bitButtons.put(i, itemPanelButton);

            bitValuesPanel.add(itemPanelButton);

            final int bitNr = i;
            // changes the state of buttons by click
            itemPanelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    RequestUtils.getInstance().getBusService().sendBusData(busNr, address,
                            bitNr, "0".equals(itemPanelButton.getText()),RequestUtils.VOID_ASYNC_CALLBACK);

                }
            });
        }
    }

    /**
     * Update the views of bus addresses for the giving data.
     *
     * @param data the actual data
     */
    public void updateData(int data) {
        BigInteger bits = new BigInteger(String.valueOf(data));
        // check the bits of each address and show it in revert order for the widgets
        for (int i = 0; i < 8; i++) {
            String dataBtn = bitButtons.get(i + 1).getText();
            bitButtons.get(i + 1).setText(
                    String.valueOf(bits.testBit(i) ? 1 : 0));
            if (!dataBtn.equals(bitButtons.get(i + 1).getText())) {

                bitButtons.get(i + 1).flashLight();
            }
        }
    }

}