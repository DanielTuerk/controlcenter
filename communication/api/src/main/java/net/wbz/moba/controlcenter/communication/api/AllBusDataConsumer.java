package net.wbz.moba.controlcenter.communication.api;

/**
 * This consumer is informed by state changes of all addresses of each existing SX bus.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AllBusDataConsumer extends BusDataConsumer {
    protected AllBusDataConsumer() {
        super(-1, -1);
    }

    @Override
    public void valueChanged(int value) {
    }

    abstract public void valueChanged(int bus,int address, int value);
}
