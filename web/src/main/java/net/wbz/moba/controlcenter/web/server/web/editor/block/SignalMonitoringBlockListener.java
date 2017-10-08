package net.wbz.moba.controlcenter.web.server.web.editor.block;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Signal.FUNCTION;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;

/**
 * TODO
 * <p>
 * Only react to a free track after a timeout of {@see TIME_TO_WAIT_FOR_FREE_TRACK} to avoid multiple calls.
 * That calls could happen if a train has waggons with power contact wheels. The wheel will create contact of two
 * successive blocks. At this moment the second block will also detect the train as entering and immediately as
 * leaving train while the wheels rolling over the seperation of the blocks and create a contact of both.
 * </p>
 * <p>
 * Only the exit of the block for calling a free track, the entering of the block will be immediately.
 * </p>
 * 
 * @author Daniel Tuerk
 */
abstract class SignalMonitoringBlockListener extends AbstractSignalBlockListener {

    /**
     * TODO
     */
    private static final int TIME_TO_WAIT_FOR_FREE_TRACK = 10000;
    private static final Logger log = LoggerFactory.getLogger(SignalMonitoringBlockListener.class);
    private final SignalBlock signalBlock;
    private final TrackViewerService trackViewerService;
    private final TrainManager trainManager;
    private final ExecutorService executorService;
    private volatile LastBlockState lastReceivedBlockStateWasFree = null;
    private Integer blockNrToMonitore;

    SignalMonitoringBlockListener(SignalBlock signalBlock, TrackViewerService trackViewerService,
            TrainManager trainManager) {
        super(signalBlock.getSignal().getMonitoringBlock(), trainManager);
        this.signalBlock = signalBlock;
        this.trackViewerService = trackViewerService;
        this.trainManager = trainManager;

        this.blockNrToMonitore = getMonitoringBlockFunction().getBit();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(
                this.getClass().getSimpleName() + "-signal-monitoring-block-" + signalBlock.getSignal()
                        .getMonitoringBlock().getBlockFunction().getBit() + "_%d").build();
        // TODO synchronous or asynchronous?
        this.executorService = Executors.newSingleThreadExecutor(namedThreadFactory);
    }

    public abstract void trackClear();

    @Override
    public void trainEnterBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == blockNrToMonitore) {
        // blockOccupiedByTrain(blockNumber);
        //
        // log.debug("signal monitoring block {} - train enter {} ", new Object[] { blockNumber, trainAddress });
        // signalBlock.setTrainInMonitoringBlock(trainManager.getTrain(trainAddress));
        // }
    }

    @Override
    public void trainLeaveBlock(int blockNumber, int trainAddress, boolean forward) {
        // if (blockNumber == blockNrToMonitore) {
        // log.debug("signal: {} monitoring block {} - train leave {}", new Object[] { signalBlock.getSignal().getId(),
        // blockNumber, trainAddress });
        //
        // // TODO maybe wait for duplicates?
        // signalBlock.setTrainInMonitoringBlock(null);
        // }

    }

    @Override
    public void blockOccupied(int blockNumber) {
        if (blockNumber == blockNrToMonitore) {
            trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP0);
            // blockOccupiedByTrain(blockNumber);
        }
    }

    // private void blockOccupiedByTrain(int blockNr) {
    // log.debug("signal monitoring block {} - occupied (signal {})", blockNr, signalBlock
    // .getSignal().getSignalConfigRed1());
    //
    // lastReceivedBlockStateWasFree = new LastBlockState(false);
    //
    // signalBlock.setMonitoringBlockFree(false);
    // trackViewerService.switchSignal(signalBlock.getSignal(), FUNCTION.HP0);
    // }

    @Override
    public void blockFreed(int blockNr) {
        // if (blockNr == blockNrToMonitore) {
        // lastReceivedBlockStateWasFree = new LastBlockState(true);
        //
        // executorService.submit(new WaitAndSetBlockToFree(lastReceivedBlockStateWasFree.getId()));
        // }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("signalBlock", signalBlock)
                .toString();
    }

    private BusDataConfiguration getMonitoringBlockFunction() {
        return signalBlock.getSignal().getMonitoringBlock().getBlockFunction();
    }

    protected SignalBlock getSignalBlock() {
        return signalBlock;
    }

    /**
     * Model for the last state of a block.
     */
    private final class LastBlockState implements Serializable {
        private final long id;
        private final Boolean state;
        // TODO remove?
        private final DateTime time;

        private LastBlockState(Boolean state) {
            this.state = state;
            this.time = DateTime.now();
            this.id = System.nanoTime();
        }

        long getId() {
            return id;
        }

        Boolean getState() {
            return state;
        }

        DateTime getTime() {
            return time;
        }
    }

    /**
     * Runnable to wait {@see TIME_TO_WAIT_FOR_FREE_TRACK} millis and check the last block state.
     * The id of the {@link LastBlockState} is checked for the {@link WaitAndSetBlockToFree#idToCheck} that this
     * thread is responsible for the {@link LastBlockState} of the monitoring block.
     * After that the callbacks for the monitoring blocks are called.
     */
    private final class WaitAndSetBlockToFree implements Runnable {
        private final long idToCheck;

        private WaitAndSetBlockToFree(long idToCheck) {
            this.idToCheck = idToCheck;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(TIME_TO_WAIT_FOR_FREE_TRACK);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (lastReceivedBlockStateWasFree != null) {
                if (lastReceivedBlockStateWasFree.getId() == idToCheck
                        && lastReceivedBlockStateWasFree.getState()) {
                    log.debug("signal monitoring (block {}) - freed (signal: {})", blockNrToMonitore, signalBlock
                            .getSignal().getSignalConfigRed1());

                    trackClear();
                }
            }
        }
    }
}
