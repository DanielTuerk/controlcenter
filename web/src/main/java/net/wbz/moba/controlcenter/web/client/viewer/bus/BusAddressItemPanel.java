package net.wbz.moba.controlcenter.web.client.viewer.bus;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.FlowPanel;

import java.math.BigInteger;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusAddressItemPanel extends FlowPanel {
    private final FlowPanel dataPanel = new FlowPanel();

    public BusAddressItemPanel(int address) {
//        getElement().getStyle().setFloat(Style.Float.LEFT);
        add(new Label(String.valueOf(address)));
        for (int i = 1; i < 9; i++) {
            dataPanel.add(new Label());
        }
        add(dataPanel);
    }

    public void updateData(int data) {
        BigInteger bits = new BigInteger(String.valueOf(data));
        for (int i = 0; i < 8; i++) {
            Label label = (Label) dataPanel.getWidget(i);
            label.setText(String.valueOf(bits.testBit(i) ? 1 : 0));
        }
    }
}
