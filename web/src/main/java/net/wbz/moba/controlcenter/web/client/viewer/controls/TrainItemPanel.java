package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;
import com.kiouri.sliderbar.client.view.SliderBar;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;

/**
 * TODO: poll state changes from server (like scenario)
 * <p/>
 * Created by Daniel on 08.03.14.
 */
public class TrainItemPanel extends AbstractItemPanel<Train> {

    private Panel contentPanel = new FluidContainer();

    public TrainItemPanel(Train train) {
        super(train);


        Button btnEditTrain = new Button("edit");
        btnEditTrain.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogBox editDialog = new TrainItemEditDialog(getModel());
                editDialog.center();
                editDialog.show();
            }
        });
        Row row = new Row();
        row.add(new Column(1, btnEditTrain));
        contentPanel.add(row);


        Row rowDrivingFunctions = new Row();

        ButtonGroup btnGroupDirection = new ButtonGroup();
        btnGroupDirection.add(addDirectionButton(Train.DIRECTION.FORWARD));
        btnGroupDirection.add(addDirectionButton(Train.DIRECTION.BACKWARD));
        rowDrivingFunctions.add(new Column(1, btnGroupDirection));


        contentPanel.add(rowDrivingFunctions);

        //TODO: generic driving level from train config
        sliderDrivingLevel = new SliderBarSimpleHorizontal(127, "150px", true);
        sliderDrivingLevel.drawMarks(Color.WHITE.toString(), 6);

//        sliderBar.drawMarks("white",6);
//        sliderBar.setMinMarkStep(3);
        sliderDrivingLevel.setValue(lastValueHotFixOfStupidValueChangeSliderEvent);
//        sliderBar.setMaxValue(valueInt);


        Row drivingRow = new Row();
        drivingRow.add(new Column(3, sliderDrivingLevel));
        contentPanel.add(drivingRow);

        initFunctions();

        getCollapseContentPanel().add(contentPanel);

    }

    private SliderBar sliderDrivingLevel;

    @Override
    protected String getItemTitle() {
        return getModel().getName();
    }

    int lastValueHotFixOfStupidValueChangeSliderEvent = 0;

    @Override
    protected void onLoad() {
        super.onLoad();
        sliderDrivingLevel.addBarValueChangedHandler(new BarValueChangedHandler() {
            public void onBarValueChanged(BarValueChangedEvent event) {
                if (event.getValue() != lastValueHotFixOfStupidValueChangeSliderEvent) {
                    lastValueHotFixOfStupidValueChangeSliderEvent = event.getValue();
                    ServiceUtils.getTrainService().updateDrivingLevel(getModel().getId(), event.getValue(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(Void result) {
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onUnload() {

        super.onUnload();
    }

    private void initFunctions() {
        int count = 0;
        Row row = new Row();
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

            if (count % 2 == 0) {
                row = new Row();
                contentPanel.add(row);
            }
            row.add(new Column(1, btnToggleFunction));

//            contentPanel.add(btnToggleFunction);
//        row.add(col);
//        contentPanel.add(row);
            count++;

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
