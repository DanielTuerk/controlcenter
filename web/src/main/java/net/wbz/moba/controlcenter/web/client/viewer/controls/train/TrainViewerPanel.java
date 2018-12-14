package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainLightStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainStateEvent;

/**
 * Viewer for the {@link Train}s.
 * TODO: reload trains for configuration changes
 * 
 * @author Daniel Tuerk
 */
public class TrainViewerPanel extends AbstractItemViewerPanel<TrainItemPanel, TrainStateEvent, TrainDataChangedEvent> {

    @Override
    protected List<Class<? extends TrainStateEvent>> getStateEventClasses() {
        List<Class<? extends TrainStateEvent>> classes = new ArrayList<>();
        classes.add(TrainHornStateEvent.class);
        classes.add(TrainLightStateEvent.class);
        classes.add(TrainFunctionStateEvent.class);
        classes.add(TrainDrivingDirectionEvent.class);
        classes.add(TrainDrivingLevelEvent.class);
        return classes;
    }

    @Override
    protected Class<TrainDataChangedEvent> getDataEventClass() {
        return TrainDataChangedEvent.class;
    }

    @Override
    protected void loadItems() {
        RequestUtils.getInstance().getTrainEditorService().getTrains(new AsyncCallback<Collection<Train>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Collection<Train> result) {
                for (Train train : result) {
                    addItemPanel(new TrainItemPanel(train));
                }
            }
        });
    }
}
