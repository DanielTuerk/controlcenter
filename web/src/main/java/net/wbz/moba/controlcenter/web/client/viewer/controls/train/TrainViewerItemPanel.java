package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDrivingDirectionRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDrivingLevelRemoteListener;
import net.wbz.moba.controlcenter.web.client.viewer.controls.BaseViewerItemPanel;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent.DRIVING_DIRECTION;

/**
 * Item container for the {@link Train}.
 *
 * @author Daniel Tuerk
 */
public class TrainViewerItemPanel extends BaseViewerItemPanel<Train> {

    private final TrainDrivingLevelRemoteListener trainDrivingLevelRemoteListener;
    private final TrainDrivingDirectionRemoteListener trainDrivingDirectionRemoteListener;

    TrainViewerItemPanel(TrainItemControlsPanel trainItemControlsPanel) {
        super(trainItemControlsPanel);
        final Train train = trainItemControlsPanel.getModel();

        getLblName().setText(train.getName());

        trainDrivingDirectionRemoteListener = new TrainDrivingDirectionRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void drivingDirectionChanged(DRIVING_DIRECTION drivingDirection) {
                getLblState().setText(drivingDirection.name());
            }
        };
        trainDrivingLevelRemoteListener = new TrainDrivingLevelRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void drivingLevelChanged(int drivingLevel) {
                getLblStateDetails().setText(String.valueOf(drivingLevel));
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trainDrivingDirectionRemoteListener, trainDrivingLevelRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance()
            .removeListener(trainDrivingDirectionRemoteListener, trainDrivingLevelRemoteListener);
    }

}
