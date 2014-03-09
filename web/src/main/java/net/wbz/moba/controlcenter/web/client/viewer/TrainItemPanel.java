package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplevertical.SliderBarSimpleVertical;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

/**
 * TODO: poll state changes from server (like scenario)
 * <p/>
 * Created by Daniel on 08.03.14.
 */
public class TrainItemPanel extends AbstractItemPanel<Train> {

    private Panel contentPanel = new VerticalPanel();

    public TrainItemPanel(Train train) {
        super(train);
    }

    @Override
    protected String getItemTitle() {
        return getModel().getName();
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Button btnEditTrain = new Button("edit");
        btnEditTrain.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogBox editDialog = new TrainItemEditDialog(getModel());
                editDialog.center();
                editDialog.show();
            }
        });
        contentPanel.add(btnEditTrain);

        ButtonGroup btnGroupDirection = new ButtonGroup();
        btnGroupDirection.add(addDirectionButton(Train.DIRECTION.FORWARD));
        btnGroupDirection.add(addDirectionButton(Train.DIRECTION.BACKWARD));
        contentPanel.add(btnGroupDirection);

        SliderBarSimpleVertical sliderDrivingLevel = new SliderBarSimpleVertical(127, "100px", true);
        sliderDrivingLevel.addBarValueChangedHandler(new BarValueChangedHandler() {
            public void onBarValueChanged(BarValueChangedEvent event) {
                ServiceUtils.getTrainService().updateDrivingLevel(getModel().getId(), event.getValue(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }
                });
            }
        });
        contentPanel.add(sliderDrivingLevel);
//        sliderBar.drawMarks("white",6);
//        sliderBar.setMinMarkStep(3);
//        sliderBar.setValue(valueInt);
//        sliderBar.setMaxValue(valueInt);

        initFunctions();

        getCollapseContentPanel().add(contentPanel);

    }

    private void initFunctions() {
        for (final TrainFunction functionEntry : getModel().getFunctions()) {

            ToggleButton btnToggleFunction = new ToggleButton(functionEntry.getFunction().name());
            btnToggleFunction.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    ServiceUtils.getTrainService().setFunctionState(getModel().getId(), functionEntry.getFunction(), event.getValue(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(Void result) {
                        }
                    });
                }
            });
            contentPanel.add(btnToggleFunction);
        }
    }

    private Button addDirectionButton(final Train.DIRECTION direction) {
        Button btnDirection = new Button(direction == Train.DIRECTION.FORWARD ? "<<" : ">>");
        btnDirection.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getTrainService().toggleDrivingDirection(getModel().getId(), direction, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }
                });
            }
        });
        return btnDirection;
    }
}
