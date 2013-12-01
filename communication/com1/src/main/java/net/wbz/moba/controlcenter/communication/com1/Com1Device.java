package net.wbz.moba.controlcenter.communication.com1;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.api.InputModule;
import net.wbz.moba.controlcenter.communication.api.OutputModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Com1Device implements Device {
    private static final Logger log = LoggerFactory.getLogger(Com1Device.class);

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private Map<Byte, OutputModule> modules = new HashMap<Byte, OutputModule>();
    private Map<Byte, InputModule> inputModules = new HashMap<Byte, InputModule>();

    private SerialPort serialPort = null;

    private final String deviceId;

    public Com1Device(String deviceId) {

        this.deviceId = deviceId;
    }

    @Override
    public void connect() {
        log.info("connect COM1");
        try {
//            System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyUSB0");
//            System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/tty.usbserial-FTG3FYGN");
            System.setProperty("gnu.io.rxtx.SerialPorts", deviceId);
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
                log.info("port id: " + portId.getName());
                try {
                    serialPort = (SerialPort) portId.open("net.wbz.moba.controlcenter", 2000);

                    outputStream = serialPort.getOutputStream();
                    inputStream = serialPort.getInputStream();
                    serialPort.setSerialPortParams(19200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    log.info("connected to COM1");


                } catch (PortInUseException e) {
                    log.error("COM1 in use", e);
                } catch (UnsupportedCommOperationException e) {
                    log.error("can't connect COM1", e);
                } catch (IOException e) {
                    log.error("I/O error", e);
                }
            }
        } catch (Exception e) {
            log.error("can't load port list", e);
        }
    }

    public static void main(String[] args) {
        Com1Device com1 = new Com1Device("/dev/tty.usbserial-FTG3FYGN");
        com1.connect();

        try {
            com1.getInputModule((byte) 50);
        } catch (DeviceAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        com1.disconnect();
    }

    @Override
    public void disconnect() {
        if (serialPort != null) {
            try {
                serialPort.getOutputStream().close();
            } catch (IOException e) {
                log.error("can't close output stream", e);
            }
            try {
                serialPort.getInputStream().close();
            } catch (IOException e) {
                log.error("can't close input stream", e);
            }
            outputStream = null;
            inputStream = null;
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    @Override
    public boolean isConnected() {
        return outputStream != null;
    }

    @Override
    public synchronized OutputModule getOutputModule(byte address) throws DeviceAccessException {
        if (!modules.containsKey(address)) {
            if (outputStream == null || inputStream == null) {
                throw new DeviceAccessException("COM1 device not connected");
            }
            OutputModule module = new FunctionDecoderModule(address);
            module.initialize(outputStream,inputStream);
            modules.put(address, module);
        }
        return modules.get(address);
    }

    @Override
    public synchronized InputModule getInputModule(byte address) throws DeviceAccessException {
        if (!inputModules.containsKey(address)) {
            if (outputStream == null || inputStream == null) {
                throw new DeviceAccessException("COM1 device not connected");
            }
            InputModule module = new InputEncoderModule(address);
            module.setOutputStream(outputStream);
            module.setInputStream(inputStream);
            inputModules.put(address, module);
        }
        return inputModules.get(address);
    }

}
