package net.wbz.moba.controlcenter.web.client.monitor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.CheckBoxButton;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;

/**
 * This panel represents the data of a single bus address in the {@link BusMonitorPanel}. The bus address data can be
 * manipulated by changing the state of bits. Responding of incoming data by graphical behaviour.
 *
 * @author Daniel Tuerk
 */
class BusAddressItem extends Composite {

    private static final int BIT_SIZE = 8;
    private static final int ADDRESS_LENGTH = 3;
    private static final String COLOR_ACTIVE = "#BDFA00";
    private static BusAddressItem.Binder uiBinder = GWT.create(BusAddressItem.Binder.class);
    /**
     * Represents the address value as 8 bits.
     */
    private final Map<Integer, FlashLightButton> bitButtons = new HashMap<>();

    @UiField
    Label lblAddress;
    @UiField
    ButtonGroup btnGroup;

    /**
     * Construct the panel and initialize the widgets of bus address.
     *
     * @param busNr number of the bus
     * @param address an address of the bus
     */
    BusAddressItem(final int busNr, final int address) {
        initWidget(uiBinder.createAndBindUi(this));

        lblAddress.setText(fillLeadingWithZero(String.valueOf(address)));

        for (int i = BIT_SIZE; i > 0; i--) {
            final FlashLightButton checkBoxButton = new FlashLightButton(String.valueOf(i));
            checkBoxButton.setSize(ButtonSize.SMALL);
            btnGroup.add(checkBoxButton);
            bitButtons.put(i, checkBoxButton);

            final int bitNr = i;
            // changes the state of buttons by click
            checkBoxButton.addClickHandler(
                event -> {
                    boolean newState = !checkBoxButton.isActive();
                    RequestUtils.getInstance().getBusService().sendBusData(busNr, address,
                        bitNr, newState, RequestUtils.VOID_ASYNC_CALLBACK);
                });
        }
    }

    private static String fillLeadingWithZero(String string) {
        final StringBuilder sb = new StringBuilder();
        for (int i = ADDRESS_LENGTH; i > 0; i--) {
            if (string.length() < i) {
                sb.append("0");
            } else {
                sb.append(string);
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Update the views of bus addresses for the giving data.
     *
     * @param data the actual data
     */
    void updateData(int data) {
        final BigInteger bits = BigInteger.valueOf(data);
        // check the bits of each address and show it in revert order for the widgets
        for (int i = 1; i <= BIT_SIZE; i++) {
            bitButtons.get(i).updateFlashLight(bits.testBit(i - 1));
        }
    }

    interface Binder extends UiBinder<Widget, BusAddressItem> {

    }

    private final class FlashLightButton extends CheckBoxButton {

        private volatile boolean running = false;
        private boolean oldState = false;

        private FlashLightButton(String label) {
            super(label);
        }

        /**
         * Update the value and make background color animation.
         *
         * @param state new state
         */
        void updateFlashLight(boolean state) {
            setActive(state);
            if (state != oldState) {
                flashLight();
            }
            oldState = state;
        }

        /**
         * Schedules a timer to set color on changing state of button and delay for 2 seconds to switch back to original
         * color.
         */
        private synchronized void flashLight() {
            if (running) {
                return;
            }
            running = true;
            final String originalColor = this.getElement().getStyle().getBackgroundColor();
            this.getElement().getStyle().setBackgroundColor(COLOR_ACTIVE);
            Timer timer = new Timer() {
                @Override
                public void run() {
                    FlashLightButton.this.getElement().getStyle().setBackgroundColor(originalColor);
                    running = false;
                }

            };
            timer.schedule(2000);
        }
    }

}