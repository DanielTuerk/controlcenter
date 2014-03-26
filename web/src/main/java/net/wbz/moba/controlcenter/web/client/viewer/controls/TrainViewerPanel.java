package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainStateEvent;

import java.util.List;

/**
 * TODO: reload trains for configuration changes
 *
 * Created by Daniel on 08.03.14.
 */
public class TrainViewerPanel extends AbstractItemViewerPanel<TrainItemPanel, TrainStateEvent> {

    public TrainViewerPanel() {
        super(TrainStateEvent.class);
    }

    @Override
    protected void eventCallback(TrainItemPanel eventItem, TrainStateEvent trainStateEvent) {

    }

    @Override
    protected ClickHandler getBtnNewClickHandler(final TextBox name) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getTrainEditorService().createTrain(name.getText(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.severe("", "", caught);     //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        name.setText("");
                        loadData();
                    }
                });
            }
        };
    }

    @Override
    protected void loadItems() {
        ServiceUtils.getTrainEditorService().getTrains(new AsyncCallback<List<Train>>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.severe("", "", caught);     //TODO
            }

            @Override
            public void onSuccess(List<Train> result) {
                for (Train train : result) {
                    addItemPanel(new TrainItemPanel(train));
                }
            }
        });
    }
}
