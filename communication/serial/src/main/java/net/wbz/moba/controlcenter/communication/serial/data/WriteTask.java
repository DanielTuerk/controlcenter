package net.wbz.moba.controlcenter.communication.serial.data;

import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.tree.ByteSignature;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class WriteTask extends AbstractSerialAccessTask<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(WriteTask.class);

    private final BusData busData;

    public WriteTask(InputStream inputStream, OutputStream outputStream, BusData busData) {
        super(inputStream, outputStream);
        this.busData = busData;
    }

    @Override
    public Boolean call() throws Exception {
//        assert busData.getAddress() >= 0 && busData.getAddress() <= 108;
        byte address;
//        if (busData.getAddress() < 109) {
//            address =BigInteger.valueOf(busData.getAddress()).or(BigInteger.valueOf(128)).byteValue();
//        } else {
//            address = (byte) busData.getAddress();
//        }

        address= (byte) (busData.getAddress() +128);
        getOutputStream().write(new byte[]{(byte) busData.getBus(), address, (byte) busData.getData()});
        getOutputStream().flush();
        byte[] b = new byte[1];
//        assert getInputStream().read(b) == 0;
        log.debug("write reply: " + getInputStream().read());
        return null;
    }
}
