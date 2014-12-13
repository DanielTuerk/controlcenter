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

            // TODO: the initial state of the bit was queried -> should only received by consumer! CHECK THIS!
            registerConsumersForTrackPartConfigs(trackParts);

            log.info("return track parts");
            return trackParts;
        } else {
            log.warn("the current construction is empty");
            return new TrackPart[0];
        }
    }

    //    @PreDestroy
    public void cleanUp() {
        //TODO
    }

    /**
     * TODO: needed? since refactor no registration after creation
     *
     * @param trackParts
     */
    private void registerConsumersForTrackPartConfigs(TrackPart[] trackParts) {

        //unregister existing to create new ones from given track parts


//        busDataConsumersOfTheCurrentTrack.clear();
//
//        // create consumers for the configuration of the track parts
//        log.info("create consumers of track parts");
//
//        List<Configuration> uniqueTrackPartConfigs = Lists.newArrayList();
//
//        for (final TrackPart trackPart : trackParts) {
//            final Configuration trackPartConfiguration = trackPart.getConfiguration();
//
//            if (trackPartConfiguration != null && trackPartConfiguration.isValid()) {
//
//                if (connectedDevice != null) {
//                    try {
//                        //TODO: bus number from config (maybe by type)
//                        trackPart.setInitialState(connectedDevice.getBusAddress(1, (byte) trackPartConfiguration.getAddress()).
//                                getBitState(trackPartConfiguration.getBit()));
//                    } catch (DeviceAccessException e) {
//                    }
//                }
//                if (!uniqueTrackPartConfigs.contains(trackPartConfiguration)) {
//                    uniqueTrackPartConfigs.add(trackPartConfiguration);
//
//                    // TODO: fire initial data of the bus for all track parts; maybe register
//                    //TODO bus nr
//                    busDataConsumersOfTheCurrentTrack.add(new BusDataConsumer(1, trackPartConfiguration.getAddress()) {
//                        @Override
//                        public void valueChanged(int oldValue, int newValue) {
//                            if (BigInteger.valueOf(newValue).testBit(trackPartConfiguration.getBit() - 1) != BigInteger.valueOf(oldValue).testBit(trackPartConfiguration.getBit() - 1)) {
//                                eventBroadcaster.fireEvent(new TrackPartStateEvent(trackPartConfiguration,
//                                        BigInteger.valueOf(newValue).testBit(trackPartConfiguration.getBit() - 1)));
//                            }
//                        }
//                    });
//                }
//            }
//        }
//            // register consumers if an device is already connected TODO: really correct? widgets for event receiver doesn't exists atm
//            try {
//                deviceManager.getConnectedDevice().getBusDataDispatcher().unregisterConsumers(busDataConsumersOfTheCurrentTrack);
//                deviceManager.getConnectedDevice().getBusDataDispatcher().registerConsumers(busDataConsumersOfTheCurrentTrack);
//            } catch (DeviceAccessException e) {
//                //ignore
//            }


    }
}
