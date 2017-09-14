package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.wbz.moba.controlcenter.web.client.RequestUtils;
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
    protected void addContent() {
        InputGroup inputCreate = new InputGroup();
        inputCreate.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        final TextBox txtName = new TextBox();
        inputCreate.add(txtName);
        InputGroupButton groupButton = new InputGroupButton();
        Button btnNew = new Button("new");
        btnNew.addClickHandler(getBtnNewClickHandler(txtName));
        groupButton.add(btnNew);
        inputCreate.add(groupButton);

        add(inputCreate);

        super.addContent();
    }

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
                        // loadData();
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
