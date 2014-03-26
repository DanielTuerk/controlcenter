package net.wbz.moba.controlcenter.communication.serial.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
        getOutputStream().write(new byte[]{(byte) busData.getBus(), (byte) (busData.getAddress()+ 128), (byte) busData.getData()});
        getOutputStream().flush();
//        log.debug("write reply: " + getInputStream().read());
        return null;
    }
}
