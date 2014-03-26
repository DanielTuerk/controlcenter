package net.wbz.moba.controlcenter.communication.api;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusDataDispatcher implements BusDataReceiver {
    private static final Logger log = LoggerFactory.getLogger(BusDataDispatcher.class);

    private Map<Integer, byte[]> busData = Maps.newConcurrentMap();
    private Queue<BusDataConsumer> consumers = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService;

    public BusDataDispatcher() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("bus-data-dispatcher-%d").build();
        executorService = Executors.newCachedThreadPool(namedThreadFactory);
    }

    public byte[] getData(int busNr) {
        if (busData.containsKey(busNr)) {
            return busData.get(busNr);
        }
        throw new RuntimeException(String.format("no bus found for number '%d'", busNr));
    }

    public void registerConsumer(BusDataConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void recevied(int busNr, byte[] data) {
        if (busData.containsKey(busNr)) {
            byte[] oldData = busData.get(busNr);
            for (int i = 0; i < data.length; i++) {
                if (data[i] != oldData[i]) {
                    fireChange(busNr, i, data[i]);
                }
            }
        }
        busData.put(busNr, data);
    }

    private void fireChange(final int busNr, final int address, final int data) {
        log.debug("value changed: " + busNr + " - " + address + " >> " + data);
        for (final BusDataConsumer consumer : consumers) {
            if (consumer instanceof AllBusDataConsumer) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        ((AllBusDataConsumer)consumer).valueChanged(busNr,address,data);
                    }
                });
            } else if (consumer.getAddress() == address && consumer.getBus() == busNr) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        consumer.valueChanged(data);
                    }
                });
            }
        }
    }

}
