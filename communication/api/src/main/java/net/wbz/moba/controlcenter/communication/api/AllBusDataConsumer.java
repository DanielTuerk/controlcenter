package net.wbz.moba.controlcenter.communication.api;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
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
