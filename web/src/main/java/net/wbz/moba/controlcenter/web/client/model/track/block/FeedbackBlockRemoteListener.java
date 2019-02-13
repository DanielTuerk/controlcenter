package net.wbz.moba.controlcenter.web.client.model.track.block;

import net.wbz.moba.controlcenter.web.client.event.track.FeedbackTrackBlockRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * @author Daniel Tuerk
 */
class FeedbackBlockRemoteListener implements FeedbackTrackBlockRemoteListener {

    private final Feedbackable feedbackable;

    FeedbackBlockRemoteListener(Feedbackable feedbackable) {
        this.feedbackable = feedbackable;
    }

    @Override
    public void feedbackTrackBlockStateChanged(FeedbackBlockEvent event) {
        updateTrainOnTrack(event.getAddress(), event.getBlock(), event.getTrain(),
            event.getState());
    }

    /**
     * Show train label on the given block.
     *
     * @param address address of the block
     * @param block number of the block
     * @param trainAddress address of the train
     * @param state {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent.STATE} enter or exit the block
     */
    private void updateTrainOnTrack(final int address, final int block, final int trainAddress,
        final FeedbackBlockEvent.STATE state) {

        BusDataConfiguration configAsIdentifier = new BusDataConfiguration(1, address, block, true);
        if (feedbackable.hasBlock(configAsIdentifier)) {
            RequestUtils.getInstance().getTrainEditorService()
                .getTrain(trainAddress, new OnlySuccessAsyncCallback<Train>() {
                    @Override
                    public void onSuccess(Train train) {
                        switch (state) {
                            case ENTER:
                                feedbackable.showTrainOnBlock(train);
                                break;
                            case EXIT:
                                feedbackable.removeTrainOnBlock(train);
                                break;
                        }
                    }
                });
        }
    }
}
