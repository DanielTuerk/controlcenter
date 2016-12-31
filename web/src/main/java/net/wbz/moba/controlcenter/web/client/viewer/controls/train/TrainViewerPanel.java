package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.train.*;
import org.gwtbootstrap3.client.ui.TextBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO: reload trains for configuration changes
 * Created by Daniel on 08.03.14.
 */
public class TrainViewerPanel extends AbstractItemViewerPanel<TrainItemPanel, TrainStateEvent> {

    @Override
    protected List<Class<TrainStateEvent>> getStateEventClasses() {
        List classes = new ArrayList<TrainStateEvent>();
        classes.add(TrainHornStateEvent.class);
        classes.add(TrainLightStateEvent.class);
        classes.add(TrainFunctionStateEvent.class);
        classes.add(TrainDrivingDirectionEvent.class);
        classes.add(TrainDrivingLevelEvent.class);
        return classes;
    }

    @Override
    protected ClickHandler getBtnNewClickHandler(final TextBox name) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Train train = new Train();
                train.setName(name.getText());
                RequestUtils.getInstance().getTrainEditorService().createTrain(train, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        name.setText("");
//                        loadData();
                    }
                });
            }
        };
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
