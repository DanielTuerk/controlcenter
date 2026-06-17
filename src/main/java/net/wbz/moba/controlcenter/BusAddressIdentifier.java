package net.wbz.moba.controlcenter;

import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;

/**
 * Identifier for an address of a bus.
 *
 * @author Daniel Tuerk
 */
public record BusAddressIdentifier(int bus, int address) {

    public BusAddressIdentifier(BusDataConfiguration blockFunction) {
        this(blockFunction.getBus(), blockFunction.getAddress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BusAddressIdentifier that = (BusAddressIdentifier) o;

        if (address != that.address)
            return false;
        return bus == that.bus;

    }

}