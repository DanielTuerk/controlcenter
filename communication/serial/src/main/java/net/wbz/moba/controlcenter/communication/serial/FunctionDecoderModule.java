package net.wbz.moba.controlcenter.communication.serial;

import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.OutputModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class FunctionDecoderModule implements OutputModule {

    private static final Logger LOG = LoggerFactory.getLogger(FunctionDecoderModule.class);

    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private final byte sxBus;
    private final byte address;
    private final boolean[] bits = {false, false, false, false, false, false, false, false};

    public FunctionDecoderModule(byte address) {
        this((byte) 1, address);
    }

    public FunctionDecoderModule(byte sxBus, byte address) {
        this.sxBus = sxBus;
        this.address = address;
    }

    @Override
    public OutputModule setBit(Device.BIT bit, boolean state) {
        switch (bit) {
            case BIT_1:
                bits[0] = state;
                break;
            case BIT_2:
                bits[1] = state;
                break;
            case BIT_3:
                bits[2] = state;
                break;
            case BIT_4:
                bits[3] = state;
                break;
            case BIT_5:
                bits[4] = state;
                break;
            case BIT_6:
                bits[5] = state;
                break;
            case BIT_7:
                bits[6] = state;
                break;
            case BIT_8:
                bits[7] = state;
                break;
        }
        return this;
    }

    private int getValue() {
        int data = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                data += (int) Math.pow(2, i);
            }
        }
        return data;
    }

    @Override
    public void sendData() {
        send((byte) getValue());
    }

    private void send(byte data) {
        try {
            // to write data to the address you need BIT 8 at '1' (+128)
            outputStream.write(new byte[]{sxBus, (byte) ((int) address + 128), data});
            outputStream.flush();
            LOG.debug("write bit of " + this.address + ": " + data);
        } catch (IOException e) {
            LOG.error(String.format("can't send data(%sd) to module %d", address, data), e);
        }
    }

    @Override
    public boolean getBitState(Device.BIT bit) {
        switch (bit) {
            case BIT_1:
                return bits[0];
            case BIT_2:
                return bits[1];
            case BIT_3:
                return bits[2];
            case BIT_4:
                return bits[3];
            case BIT_5:
                return bits[4];
            case BIT_6:
                return bits[5];
            case BIT_7:
                return bits[6];
            case BIT_8:
                return bits[7];
        }
        return false;
    }

    private void read() {
        try {
            LOG.debug(Thread.currentThread().getId() + " - " + this.toString() + " - start read of " + address);
            outputStream.write(new byte[]{sxBus, address, 0});
            outputStream.flush();
            int result = inputStream.read();
            LOG.debug(Thread.currentThread().getId() + " - " + this.toString() + " - bit of " + address + ": " + result);
            BigInteger byteResult = BigInteger.valueOf((long) result);
            for (int i = 0; i < bits.length; i++) {
                bits[i] = byteResult.testBit(i);
            }
        } catch (IOException e) {
            LOG.error(String.format("can't read data of module %d", address));
        }
    }

    @Override
    public void initialize(OutputStream outputStream, InputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        // read state from bus
        read();
        // reset to standard
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //ignore
        }
        int actualValue = getValue();
        if (actualValue > 0) {
            send((byte) 0);
        } else {
            send((byte) 1);
        }
        // write state to bus to change
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //ignore
        }
        sendData();
    }
}
