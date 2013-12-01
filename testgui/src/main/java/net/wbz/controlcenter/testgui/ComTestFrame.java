package net.wbz.controlcenter.testgui;

import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.manager.DeviceManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ComTestFrame extends JFrame {

    private static final String DEVICE_ID = "";
    private Device device;
    private final JTextField txtAdress = new JTextField("80", 3);

    public ComTestFrame() {
        DeviceManager dm = new DeviceManager();
        device = dm.registerDevice(DeviceManager.DEVICE_TYPE.COM1, DEVICE_ID);
        device.connect();
        setTitle("Test COM 1");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (device != null) {
                    device.disconnect();
                }
            }
        });


        add(txtAdress);

        final JToggleButton btn1 = new JToggleButton("1");
        btn1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_1, btn1.isSelected());
            }
        });
        add(btn1);

        final JToggleButton btn2 = new JToggleButton("2");
        btn2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_2, btn2.isSelected());
            }
        });
        add(btn2);
        final JToggleButton btn3 = new JToggleButton("3");
        btn3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_3, btn3.isSelected());
            }
        });
        add(btn3);
        final JToggleButton btn4 = new JToggleButton("4");
        btn4.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_4, btn4.isSelected());
            }
        });
        add(btn4);
        final JToggleButton btn5 = new JToggleButton("5");
        btn5.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_5, btn5.isSelected());
            }
        });
        add(btn5);
        final JToggleButton btn6 = new JToggleButton("6");
        btn6.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_6, btn6.isSelected());
            }
        });
        add(btn6);
        final JToggleButton btn7 = new JToggleButton("7");
        btn7.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_7, btn7.isSelected());
            }
        });
        add(btn7);

        final JToggleButton btn8 = new JToggleButton("8");
        btn8.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(Device.BIT.BIT_8, btn8.isSelected());
            }
        });
        add(btn8);

        pack();
    }

    private void send(Device.BIT bit, boolean state) {
        try {
            device.getOutputModule((byte) Integer.parseInt(txtAdress.getText())).setBit(bit, state);
        } catch (DeviceAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        new ComTestFrame().setVisible(true);
    }
}
