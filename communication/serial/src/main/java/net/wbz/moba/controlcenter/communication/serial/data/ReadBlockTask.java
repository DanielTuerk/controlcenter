package net.wbz.moba.controlcenter.communication.serial.data;

import net.wbz.moba.controlcenter.communication.api.BusDataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class
        ReadBlockTask extends AbstractSerialAccessTask<Void> {
    private static final Logger log = LoggerFactory.getLogger(ReadBlockTask.class);

    private final BusDataReceiver receiver;

    public ReadBlockTask(InputStream inputStream, OutputStream outputStream, BusDataReceiver receiver) {
        super(inputStream, outputStream);
        this.receiver = receiver;
    }

    @Override
    public Void call() throws Exception {
        byte[] replyBus0 = new byte[226];
        readBlock(120, 3, replyBus0);

        receiver.received(0, Arrays.copyOfRange(replyBus0, 0, 112));
        receiver.received(1, Arrays.copyOfRange(replyBus0, 113, 225));
//        byte[] replyBus1 = new byte[192];
//        readBlock(120, 120, replyBus1);
//        receiver.received(0, replyBus1);
        return null;
    }

    private void readBlock(int address, int data, byte[] reply) throws IOException {

        getOutputStream().write(new byte[]{(byte) address, (byte) data});
        getOutputStream().flush();

//        long start = System.currentTimeMillis();
//        log.info("READ ALL - start");

//
        int lenght = getInputStream().read(reply);
        if (lenght != reply.length) {
            throw new RuntimeException("block length invalid ("+lenght+")");
        }
//        int foo = getInputStream().read(reply);
//        log.info("READ ALL - end: " + (System.currentTimeMillis() - start) + " - "+foo);
//        for(byte value : reply) {
//            log.info("bus reply: " + (int)value);
//        }

    }
}
