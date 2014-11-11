package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Label;

import java.math.BigInteger;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusAddressItemPanel extends org.gwtbootstrap3.client.ui.gwt.FlowPanel {
    private final FlowPanel dataPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();

    public BusAddressItemPanel(int address) {
        getElement().getStyle().setFloat(Style.Float.LEFT);
        add(new Label(String.valueOf(address)));
        for (int i = 1; i < 9; i++) {
            dataPanel.add(new Label("0"));
        }
        add(dataPanel);
    }

    public void updateData(int data) {
        BigInteger bits = new BigInteger(String.valueOf(data));
        // check the bits of each address and show it in revert order for the widgets
        int bit=7;
        for (int i = 0; i < 8; i++) {
            Label label = (Label) dataPanel.getWidget(i);
            label.setText(String.valueOf(bits.testBit(bit) ? 1 : 0));
            bit--;
        }
    }
}
