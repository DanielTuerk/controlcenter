package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.train.*;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.extras.slider.client.ui.Slider;

import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class TrainItemPanel extends AbstractItemPanel<Train, TrainStateEvent> {

    /**
     * Maximum decimal value of the driving level. (Bit 1-5 is on)
     */
    public static final int DRIVING_LEVEL_MAX_VALUE = 31;
    private PanelCollapse contentPanel;

    private Slider sliderDrivingLevel;

    private Button btnDirectionForward;
    private Button btnDirectionBackward;
    private Map<TrainFunction.FUNCTION, Button> functionButtons = Maps.newConcurrentMap();

    private Label lblName;
    private Label lblState;
    private Label lblStateDetails;

    private int lastSendSpeedValue = -1;

    public TrainItemPanel(Train train) {
        super(train);
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
            functionButtons.get(TrainFunction.FUNCTION.HORN).setActive(((TrainHornStateEvent) event).isState());
        } else if (event instanceof TrainLightStateEvent) {
            functionButtons.get(TrainFunction.FUNCTION.LIGHT).setActive(((TrainLightStateEvent) event).isState());
        } else if (event instanceof TrainFunctionStateEvent) {
            TrainFunctionStateEvent functionStateEvent = (TrainFunctionStateEvent) event;
            functionButtons.get(functionStateEvent.getFunction()).setActive(functionStateEvent.isActive());
        } else if (event instanceof TrainDrivingDirectionEvent) {
            lblState.setText(((TrainDrivingDirectionEvent) event).getDirection().name());
            switch (((TrainDrivingDirectionEvent) event).getDirection()) {
                case BACKWARD:
                    btnDirectionBackward.setActive(true);
                    btnDirectionForward.setActive(false);
                    break;
                case FORWARD:
                    btnDirectionForward.setActive(true);
                    btnDirectionBackward.setActive(false);
                    break;
            }
        } else if (event instanceof TrainDrivingLevelEvent) {
            TrainDrivingLevelEvent drivingLevelEvent = (TrainDrivingLevelEvent) event;
//            sliderDrivingLevel.setValue((double) drivingLevelEvent.getSpeed());
            lblStateDetails.setText("speed: " + drivingLevelEvent.getSpeed());
        }
    }

    @Override
    public PanelCollapse createCollapseContentPanel() {
        contentPanel = new PanelCollapse();

        lblName = new Label(getModel().getName());
        lblState = new Label();
        lblStateDetails = new Label();

        Button btnEditTrain = new Button();
        btnEditTrain.setIcon(IconType.PENCIL);
        btnEditTrain.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogBox editDialog = new TrainItemEditDialog(getModel());
                editDialog.center();
                editDialog.show();
            }
        });
        Row row = new Row();
        row.add(new Column(ColumnSize.MD_1, btnEditTrain));
        contentPanel.add(row);

        Row rowDrivingFunctions = new Row();

        ButtonGroup btnGroupDirection = new ButtonGroup();
        btnDirectionForward = createDirectionButton(Train.DIRECTION.FORWARD);
        btnGroupDirection.add(btnDirectionForward);
        btnDirectionBackward = createDirectionButton(Train.DIRECTION.BACKWARD);
        btnGroupDirection.add(btnDirectionBackward);

        final Label lblSliderValue = new Label("0");
        lblSliderValue.getElement().getStyle().setMarginRight(15, Style.Unit.PX);
        lblSliderValue.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        sliderDrivingLevel = new Slider(0, DRIVING_LEVEL_MAX_VALUE, 0);
        sliderDrivingLevel.addValueChangeHandler(
                new ValueChangeHandler<Double>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Double> doubleValueChangeEvent) {
                        lblSliderValue.setText(doubleValueChangeEvent.getValue().toString());
                        int level = doubleValueChangeEvent.getValue().intValue();
                        if (level != lastSendSpeedValue) {
                            lastSendSpeedValue = level;
                            ServiceUtils.getTrainService().updateDrivingLevel(
                                    getModel().getId(), level, new EmptyCallback<Void>());
                        }
                    }
                }
        );

        rowDrivingFunctions.add(new Column(ColumnSize.MD_12, btnGroupDirection, lblSliderValue, sliderDrivingLevel));

        contentPanel.add(rowDrivingFunctions);

        initFunctions();

        // set the initial state for the train by using the event callback
        if (getModel().getDrivingDirection() != null) {
            updateItemData(new TrainDrivingDirectionEvent(getModel().getId(),
                    TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(getModel().getDrivingDirection().name())));
        }
        updateItemData(new TrainDrivingLevelEvent(getModel().getId(), getModel().getDrivingLevel()));

        for (TrainFunction trainFunction : getModel().getFunctions()) {
            if (trainFunction.getFunction() == TrainFunction.FUNCTION.HORN) {
                updateItemData(new TrainHornStateEvent(getModel().getId(), trainFunction.isState()));
            } else if (trainFunction.getFunction() == TrainFunction.FUNCTION.LIGHT) {
                updateItemData(new TrainLightStateEvent(getModel().getId(), trainFunction.isState()));
            }
            // TODO train functions

        }

        return contentPanel;
    }

    private void initFunctions() {

        Column functionsColumn = new Column(ColumnSize.LG_1.getCssName());
        contentPanel.add(functionsColumn);
        for (final TrainFunction functionEntry : getModel().getFunctions()) {
            final Button btnToggleFunction = new Button(functionEntry.getFunction().name());
            btnToggleFunction.setDataToggle(Toggle.BUTTON);
            btnToggleFunction.addClickHandler(new ClickHandler() {
                                                  @Override
                                                  public void onClick(ClickEvent clickEvent) {
                                                      ServiceUtils.getTrainService().setFunctionState(getModel().getId(),
                                                              functionEntry.getFunction(), !btnToggleFunction
                                                                      .isActive(), new EmptyCallback<Void>());
                                                  }
                                              }
            );
            functionButtons.put(functionEntry.getFunction(), btnToggleFunction);
            functionsColumn.add(btnToggleFunction);
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
