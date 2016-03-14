package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import java.util.ArrayList;
import java.util.List;

import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemViewerPanel;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingLevelEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainHornStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainLightStateEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainProxy;
import net.wbz.moba.controlcenter.web.shared.train.TrainStateEvent;

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.requestfactory.shared.Receiver;

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
                ServiceUtils.getInstance().getTrainEditorService().createTrain(name.getText()).fire(
                        new Receiver<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        name.setText("");
                        loadData();
                    }
                });
            }
        };
    }

    @Override
    protected void loadItems() {
        ServiceUtils.getInstance().getTrainEditorService().getTrains().fire(new Receiver<List<TrainProxy>>() {
            @Override
            public void onSuccess(List<TrainProxy> response) {
                for (TrainProxy train : response) {
                    addItemPanel(new TrainItemPanel(train));
                }
            }
        });
    }
}
