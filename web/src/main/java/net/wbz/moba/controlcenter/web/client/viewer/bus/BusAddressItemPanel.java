package net.wbz.moba.controlcenter.web.client.viewer.bus;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusAddressItemPanel extends
		org.gwtbootstrap3.client.ui.gwt.FlowPanel {
	private final FlowPanel dataPanel = new FlowPanel();
	private final Map<Integer, ItemPanelButton> bitButtons = new HashMap<>();
	public BusAddressItemPanel(final int busNr, final int address) {
		getElement().getStyle().setFloat(Style.Float.LEFT);
		Label lblAddress = new Label(String.valueOf(address));
		lblAddress.getElement().getStyle().setBackgroundColor("LightGray");
		lblAddress.getElement().getStyle().setFloat(Style.Float.LEFT);
		lblAddress.getElement().getStyle().setWidth(20, Unit.PX);
		lblAddress.getElement().getStyle().setHeight(48, Unit.PX);
		lblAddress.getElement().getStyle().setPaddingTop(20, Unit.PX);
		lblAddress.getElement().getStyle().setPaddingBottom(20, Unit.PX);
		lblAddress.getElement().getStyle().setPaddingRight(0.1, Unit.PX);
		lblAddress.getElement().getStyle().setPaddingLeft(0.1, Unit.PX);
		lblAddress.getElement().getStyle().setColor("DarkGray");
		lblAddress.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		lblAddress.getElement().getStyle().setBorderWidth(0.4, Unit.PX);

		add(lblAddress);
		for (int i = 8; i > 0; i--) {
			FlowPanel bitPanel = new FlowPanel();
			FlowPanel bitPanelLabel = new FlowPanel();
			FlowPanel bitPanelButton = new FlowPanel();
			bitPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
			bitPanel.add(bitPanelLabel);
			bitPanel.add(bitPanelButton);
			add(bitPanel);

			Label lbl = new Label(String.valueOf(i));
			bitPanelLabel.add(lbl);
			lbl.getElement().getStyle().setPaddingTop(5, Unit.PX);
			lbl.getElement().getStyle().setPaddingBottom(5, Unit.PX);
			lbl.getElement().getStyle().setPaddingLeft(8, Unit.PX);
			lbl.getElement().getStyle().setPaddingRight(8, Unit.PX);
			lbl.getElement().getStyle().setBackgroundColor("MediumPurple");

			final ItemPanelButton btns = new ItemPanelButton();
			btns.getElement().getStyle().setPaddingTop(3, Unit.PX);
			btns.getElement().getStyle().setPaddingBottom(3, Unit.PX);
			btns.getElement().getStyle().setPaddingLeft(6, Unit.PX);
			btns.getElement().getStyle().setPaddingRight(6, Unit.PX);
			bitButtons.put(i, btns);

			bitPanelButton.add(btns);

			final int bitNr = i;
			//change the state of buttons on a click
			btns.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					ServiceUtils.getBusService().sendBusData(busNr, address,
							bitNr, btns.getText().equals("0") ? true : false,
							new EmptyCallback<Void>());
				
				}
			});

		}
		add(dataPanel);
	

	}
   /**
   * Updates the definite components of GUI,nodes (the set of button) of Flow Pane
   * @param data  the actual data
   */
	  public void updateData(int data) {
	        BigInteger bits = new BigInteger(String.valueOf(data));
	        // check the bits of each address and show it in revert order for the
	        // widgets
	        for (int i = 0; i < 8; i++) {
	            String dataBtn = bitButtons.get(i + 1).getText();
	            bitButtons.get(i + 1).setText(
	                    String.valueOf(bits.testBit(i) ? 1 : 0));
	            if (!dataBtn.equals(bitButtons.get(i + 1).getText())) {

	                bitButtons.get(i + 1).setColor();
	            }
	        }
	    }
	
	
	}