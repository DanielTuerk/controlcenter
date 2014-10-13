package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.github.gwtbootstrap.client.ui.*;
import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;
import com.kiouri.sliderbar.client.view.SliderBar;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.train.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 08.03.14.
 */
public class TrainItemPanel extends AbstractItemPanel<Train, TrainStateEvent> {

    /**
     * Maximum decimal value of the driving level. (Bit 1-5 is on)
     */
    public static final int DRIVING_LEVEL_MAX_VALUE = 31;
    private Panel contentPanel;

    private SliderBar sliderDrivingLevel;

    private Button btnDirectionForward;
    private Button btnDirectionBackward;
    private Map<TrainFunction.FUNCTION, ToggleButton> functionButtons = Maps.newConcurrentMap();

    /**
     * Stores the last programmatic value of the driving level slider to avoid callback handling.
     */
    private int lastValueHotFixOfStupidValueChangeSliderEvent = 0;

    private final Label lblName;
    private final Label lblState;
    private final Label lblStateDetails;

    public TrainItemPanel(Train train) {
        super(train);
        lblName = new Label(getModel().getName());
        lblState = new Label("state");
        lblStateDetails = new Label("TODO state");
    }

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
    protected Panel createHeaderPanel() {
        Panel headerPanel = new FlowPanel();
        lblName.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        headerPanel.add(lblName);
        headerPanel.add(lblState);
        headerPanel.add(lblStateDetails);
        return headerPanel;
    }

    @Override
    public void updateItemData(TrainStateEvent event) {
        if (event instanceof TrainHornStateEvent) {
            functionButtons.get(TrainFunction.FUNCTION.HORN).setValue(((TrainHornStateEvent) event).isState());
        } else if (event instanceof TrainLightStateEvent) {
            functionButtons.get(TrainFunction.FUNCTION.LIGHT).setValue(((TrainLightStateEvent) event).isState());
        } else if (event instanceof TrainFunctionStateEvent) {
            TrainFunctionStateEvent functionStateEvent = (TrainFunctionStateEvent) event;
            TrainFunction.FUNCTION trainFunction;
            switch (functionStateEvent.getFunctionBit()) {
                case 1:
                    trainFunction = TrainFunction.FUNCTION.F1;
                    break;
                case 2:
                    trainFunction = TrainFunction.FUNCTION.F2;
                    break;
                case 3:
                    trainFunction = TrainFunction.FUNCTION.F3;
                    break;
                case 4:
                    trainFunction = TrainFunction.FUNCTION.F4;
                    break;
                case 5:
                    trainFunction = TrainFunction.FUNCTION.F5;
                    break;
                case 6:
                    trainFunction = TrainFunction.FUNCTION.F6;
                    break;
                case 7:
                    trainFunction = TrainFunction.FUNCTION.F7;
                    break;
                case 8:
                    trainFunction = TrainFunction.FUNCTION.F8;
                    break;
                default:
                    throw new RuntimeException("no function available for bit: " + functionStateEvent.getFunctionBit());
            }
            functionButtons.get(trainFunction).setValue(functionStateEvent.isActive(), false);
        } else if (event instanceof TrainDrivingDirectionEvent) {
            lblState.setText(((TrainDrivingDirectionEvent) event).getDirection().name());
            switch (((TrainDrivingDirectionEvent) event).getDirection()) {
                case BACKWARD:
                    btnDirectionBackward.setToggle(true);
                    btnDirectionForward.setToggle(false);
                    break;
                case FORWARD:
                    btnDirectionForward.setToggle(true);
                    btnDirectionBackward.setToggle(false);
                    break;
            }
        } else if (event instanceof TrainDrivingLevelEvent) {
            TrainDrivingLevelEvent drivingLevelEvent = (TrainDrivingLevelEvent) event;
            lastValueHotFixOfStupidValueChangeSliderEvent = drivingLevelEvent.getSpeed();
            sliderDrivingLevel.setValue(drivingLevelEvent.getSpeed());
            lblStateDetails.setText("speed: " + drivingLevelEvent.getSpeed());
        }
    }

    @Override
    public Panel createCollapseContentPanel() {
        contentPanel = new FluidContainer();

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
        btnDirectionForward = createDirectionButton(Train.DIRECTION.FORWARD);
        btnGroupDirection.add(btnDirectionForward);
        btnDirectionBackward = createDirectionButton(Train.DIRECTION.BACKWARD);
        btnGroupDirection.add(btnDirectionBackward);
        rowDrivingFunctions.add(new Column(1, btnGroupDirection));

        contentPanel.add(rowDrivingFunctions);

        sliderDrivingLevel = new SliderBarSimpleHorizontal(DRIVING_LEVEL_MAX_VALUE, "150px", true);
        sliderDrivingLevel.drawMarks(Color.WHITE.toString(), 6);
        sliderDrivingLevel.setValue(lastValueHotFixOfStupidValueChangeSliderEvent);
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

        Row drivingRow = new Row();
        drivingRow.add(new Column(3, sliderDrivingLevel));
        contentPanel.add(drivingRow);

        initFunctions();

        return contentPanel;
    }

    private void initFunctions() {
        int count = 0;
        Row row = new Row();
        for (final TrainFunction functionEntry : getModel().getFunctions()) {

            final ToggleButton btnToggleFunction = new ToggleButton(functionEntry.getFunction().name());
            functionButtons.put(functionEntry.getFunction(), btnToggleFunction);
            btnToggleFunction.addClickHandler(new ClickHandler() {
                                                  @Override
                                                  public void onClick(ClickEvent clickEvent) {
                                                      ServiceUtils.getTrainService().setFunctionState(getModel().getId(), functionEntry.getFunction(), btnToggleFunction.isDown(), new AsyncCallback<Void>() {
                                                          @Override
                                                          public void onFailure(Throwable caught) {
                                                          }

                                                          @Override
                                                          public void onSuccess(Void result) {
                                                          }
                                                      });
                                                  }
                                              }
            );

            if (count % 2 == 0) {
                row = new Row();
                contentPanel.add(row);
            }
            row.add(new Column(1, btnToggleFunction));

            count++;
        }
    }

    private Button createDirectionButton(final Train.DIRECTION direction) {
        Button btnDirection;
        switch (direction) {
            case BACKWARD:
                btnDirection = new Button(">>");
                break;
            case FORWARD:
                btnDirection = new Button("<<");
                break;
            default:
                throw new RuntimeException("unknown direction to create the direction button (" + direction.name() + ")");
        }
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
