package net.wbz.moba.controlcenter.communication.api;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The dispatcher stores all values for each address of the SX bus channels.
 * To receive the values it implements {@link net.wbz.moba.controlcenter.communication.api.BusDataReceiver}.
 *
 * For each value which has changed, the registered {@link net.wbz.moba.controlcenter.communication.api.BusDataConsumer}s are informed.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
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

    /**
     * Return the current values of all addresses for the given SX bus.
     *
     * @param busNr number of the SX bus (e.g.0 or 1)
     * @return byte array which represents the address as index with the byte value of the address.
     */
    public byte[] getData(int busNr) {
        if (busData.containsKey(busNr)) {
            return busData.get(busNr);
        }
        throw new RuntimeException(String.format("no bus found for number '%d'", busNr));
    }

    /**
     * Register an new consumer to get state changed of for the values of an address from an SX bus.
     * To get the values of all addresses use the {@link net.wbz.moba.controlcenter.communication.api.AllBusDataConsumer}.
     *
     * @param consumer {@link net.wbz.moba.controlcenter.communication.api.BusDataConsumer}
     */
    public void registerConsumer(BusDataConsumer consumer) {
        consumers.add(consumer);
    }

    public void registerConsumers(List<BusDataConsumer> consumers) {
        this.consumers.addAll(consumers);
    }

    public void unregisterConsumer(BusDataConsumer consumer) {
        consumers.remove(consumer);
    }
    public void unregisterConsumers(List<BusDataConsumer> consumers) {
        this.consumers.removeAll(consumers);
    }

    @Override
    public void recevied(int busNr, byte[] data) {
        if (busData.containsKey(busNr)) {
            byte[] oldData = busData.get(busNr);
            for (int i = 0; i < data.length; i++) {
                if (Byte.compare(data[i],oldData[i])!=0) {
                    fireChange(busNr, i, data[i]);
                }
            }
        }
        busData.put(busNr, data);
    }

    private void fireChange(final int busNr, final int address, final int data) {
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
