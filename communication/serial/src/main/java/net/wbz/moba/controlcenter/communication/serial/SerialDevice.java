package net.wbz.moba.controlcenter.communication.serial;

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
public class SerialDevice implements Device {
    private static final Logger log = LoggerFactory.getLogger(SerialDevice.class);
    private static final int DEFAULT_BAUD_RATE_FCC = 230400;
    private static final int DEFAULT_BAUD_RATE_STAERZ_INTERFACE = 19200;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private Map<Byte, OutputModule> modules = new HashMap<Byte, OutputModule>();
    private Map<Byte, OutputModule> trainModules = new HashMap<Byte, OutputModule>();
    private Map<Byte, InputModule> inputModules = new HashMap<Byte, InputModule>();

    private SerialPort serialPort = null;

    private final String deviceId;
    private final int baudRate;

    public SerialDevice(String deviceId) {
        this(deviceId, DEFAULT_BAUD_RATE_FCC);
    }

    public SerialDevice(String deviceId, int baudRate) {
        this.deviceId = deviceId;
        this.baudRate = baudRate;
    }

    @Override
    public boolean getRailVoltage() throws IOException {
        //TODO: FIXME doesn't work -> in FCC action at 109 bit 8 (but also bit 6 is set)
        outputStream.write(new byte[]{(byte)0, (byte)127, (byte)1});
        outputStream.flush();
        int result =inputStream.read();
        return result != 0;
    }

    @Override
    public void setRailVoltage(boolean state) throws IOException {
        outputStream.write(new byte[]{0, (byte) 255, (byte) (state ? 1 : 0)});
        outputStream.flush();
    }

    @Override
    public void connect() {
        log.info("connect COM");
        try {
            System.setProperty("gnu.io.rxtx.SerialPorts", deviceId);
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
                try {
                    serialPort = (SerialPort) portId.open("net.wbz.moba.controlcenter", 2000);
                    outputStream = serialPort.getOutputStream();
                    inputStream = serialPort.getInputStream();
                    serialPort.setSerialPortParams(baudRate,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    log.info("connected to COM");

                    return;
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

    @Override
    public void disconnect() {
        log.info("disconnecting COM device");
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
            log.info("COM device disconnected");
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
            OutputModule module = new FunctionDecoderModule((byte) 1, address);
            module.initialize(outputStream, inputStream);
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

    @Override
    public synchronized OutputModule getTrainModule(byte address) throws DeviceAccessException {
        if (!trainModules.containsKey(address)) {
            if (outputStream == null || inputStream == null) {
                throw new DeviceAccessException("serial device not connected");
            }
            OutputModule module = new FunctionDecoderModule((byte) 0, address);
            module.initialize(outputStream, inputStream);
            trainModules.put(address, module);
        }
        return trainModules.get(address);
    }

}
