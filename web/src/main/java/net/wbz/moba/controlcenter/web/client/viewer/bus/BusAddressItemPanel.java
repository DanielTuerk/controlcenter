package net.wbz.moba.controlcenter.web.client.viewer.bus;

import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusAddressItemPanel extends org.gwtbootstrap3.client.ui.gwt.FlowPanel {
    private final FlowPanel dataPanel = new FlowPanel();
    private final Map<Integer, Button> bitButtons = new HashMap<>();

    public BusAddressItemPanel(final int busNr,final int address) {
        getElement().getStyle().setFloat(Style.Float.LEFT);
        Label lblAddress = new Label(String.valueOf(address));
        lblAddress.getElement().getStyle().setColor("BLUE");
        lblAddress.getElement().getStyle().setFloat(Style.Float.LEFT);
		add(lblAddress);
        
        
        
        for (int i = 8; i >0; i--) {
        	 FlowPanel bitPanel = new FlowPanel();
        	 FlowPanel bitPanelLabel = new FlowPanel();
        	 FlowPanel bitPanelButton = new FlowPanel();
        	 bitPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        	 bitPanel.add(bitPanelLabel);
        	 bitPanel.add(bitPanelButton);
        	 add(bitPanel);
        	 
        	 Label lbl = new Label(String.valueOf(i));
        	 bitPanelLabel.add(lbl);
       
        	final Button btns = new Button("0");
        	btns.getElement().getStyle().setPaddingTop(3, Unit.PX);
        	btns.getElement().getStyle().setPaddingBottom(3, Unit.PX);
        	btns.getElement().getStyle().setPaddingLeft(6, Unit.PX);
        	btns.getElement().getStyle().setPaddingRight(6, Unit.PX);
        	bitButtons.put(i, btns);
        	 
             
        	bitPanelButton.add(btns); 
           
           final int bitNr=i;
           btns.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ServiceUtils.getBusService().sendBusData(busNr, address, bitNr, btns.getText().equals("0") ? true : false, new EmptyCallback<Void>());
				
				
			}
			});
           
           
        }
        add(dataPanel);
        
    }

    public void updateData(int data) {

        BigInteger bits = new BigInteger(String.valueOf(data));
        // check the bits of each address and show it in revert order for the widgets
        
        for (int i = 0; i < 8; i++) {
        	
        	bitButtons.get(i+1).setText(String.valueOf(bits.testBit(i) ? 1 : 0));
        	
        	// TODO set color of button
        	setStyleName("green-btn");
        }
    }
}
