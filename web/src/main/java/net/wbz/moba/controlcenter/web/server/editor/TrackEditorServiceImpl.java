package net.wbz.moba.controlcenter.web.server.editor;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.db.model.DataContainer;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;
import net.wbz.selectrix4java.bus.BusDataConsumer;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import net.wbz.selectrix4java.device.DeviceConnectionListener;
import net.wbz.selectrix4java.device.DeviceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@Singleton
public class TrackEditorServiceImpl extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final ConstructionServiceImpl constructionService;

    private final List<BusDataConsumer> busDataConsumersOfTheCurrentTrack = Lists.newArrayList();
    private final DeviceManager deviceManager;
    private final EventBroadcaster eventBroadcaster;

    @Inject
    public TrackEditorServiceImpl(ConstructionServiceImpl constructionService, DeviceManager deviceManager, EventBroadcaster eventBroadcaster) {
        this.constructionService = constructionService;
        this.eventBroadcaster = eventBroadcaster;
        this.deviceManager = deviceManager;
        deviceManager.addDeviceConnectionListener(new DeviceConnectionListener() {
            @Override
            public void connected(Device device) {
                device.getBusDataDispatcher().registerConsumers(busDataConsumersOfTheCurrentTrack);
            }

            @Override
            public void disconnected(Device device) {
                device.getBusDataDispatcher().unregisterConsumers(busDataConsumersOfTheCurrentTrack);
            }
        });
    }

    @Override
    public void saveTrack(TrackPart[] trackParts) {
        ObjectContainer database = constructionService.getObjectContainerOfCurrentConstruction();
        DataContainer dataContainer = new DataContainer(DateTime.now().getMillis(), trackParts);
        database.store(dataContainer);
        database.commit();
    }


    @Override
    public TrackPart[] loadTrack() {
        log.info("load db");
        ObjectContainer database = constructionService.getObjectContainerOfCurrentConstruction();

        log.info("query db");
        Query query = database.query();
        query.constrain(DataContainer.class);
        query.descend("dateTime").orderDescending();
        ObjectSet<DataContainer> result = query.execute();

        if (!result.isEmpty()) {
            TrackPart[] trackParts = (TrackPart[]) result.get(0).getData();
            log.info("return track parts");
            return trackParts;
        } else {
            log.warn("the current construction is empty");
            return new TrackPart[0];
        }
    }


    /**
     * Register the {@link net.wbz.selectrix4java.bus.BusDataConsumer}s for each address of the given
     * {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s.
     *
     * @param trackParts {@link net.wbz.moba.controlcenter.web.shared.track.model.TrackPart}s to register the
     *                   containing {@link net.wbz.moba.controlcenter.web.shared.track.model.Configuration}
     */
    public void registerConsumersByConnectedDeviceForTrackParts(TrackPart[] trackParts) {

        //unregister existing to create new ones from given track parts
        try {
            deviceManager.getConnectedDevice().getBusDataDispatcher().unregisterConsumers(busDataConsumersOfTheCurrentTrack);
        } catch (DeviceAccessException e) {
            //ignore
        }
        busDataConsumersOfTheCurrentTrack.clear();

        // create consumers for the configuration of the track parts
        log.info("create consumers of track parts");

        List<Configuration> uniqueTrackPartConfigs = Lists.newArrayList();

        for (final TrackPart trackPart : trackParts) {
            for (final Configuration trackPartConfiguration : trackPart.getFunctionConfigs().values()) {

                if (trackPartConfiguration != null && trackPartConfiguration.isValid()) {

                    //TODO bus nr - remove quick hack!
                    trackPartConfiguration.setBus(1);

                    if (!uniqueTrackPartConfigs.contains(trackPartConfiguration)) {
                        uniqueTrackPartConfigs.add(trackPartConfiguration);

                        //TODO bus nr
                        busDataConsumersOfTheCurrentTrack.add(new BusDataConsumer(1, trackPartConfiguration.getAddress()) {
                            @Override
                            public void valueChanged(int oldValue, int newValue) {

                                // TODO: remove quick hack for unset bus nr of old stored widgets
                                trackPartConfiguration.setBus(1);

                                boolean initialState = oldValue == 0 && newValue == 0;
                                // fire event for changed bit state of the bus address
                                boolean bitStateChanged = BigInteger.valueOf(newValue).testBit(
                                        trackPartConfiguration.getBit() - 1)
                                        != BigInteger.valueOf(oldValue).testBit(trackPartConfiguration.getBit() - 1);

                                if (initialState || bitStateChanged) {
                                    eventBroadcaster.fireEvent(new TrackPartStateEvent(trackPartConfiguration,
                                            BigInteger.valueOf(newValue).testBit(trackPartConfiguration.getBit() - 1)));
                                }
                            }
                        });
                    }
                }
            }
        }
        // register consumers if an device is already connected
        try {
            deviceManager.getConnectedDevice().getBusDataDispatcher().registerConsumers(busDataConsumersOfTheCurrentTrack);
        } catch (DeviceAccessException e) {
            //ignore
        }
    }
}
