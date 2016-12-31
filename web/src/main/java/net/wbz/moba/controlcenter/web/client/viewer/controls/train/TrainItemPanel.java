package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.OnOffToggleButton;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.train.*;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.slider.client.ui.Slider;

import java.util.Map;

/**
 * @author Daniel Tuerk
 */
public class TrainItemPanel extends AbstractItemPanel<Train, TrainStateEvent> {

    /**
     * Maximum decimal value of the driving level. (Bit 1-5 is on)
     */
    public static final double DRIVING_LEVEL_MAX_VALUE = 31d;
    private PanelCollapse contentPanel;

    private Slider sliderDrivingLevel;

    private Button btnDirectionForward;
    private Button btnDirectionBackward;
    private Map<TrainFunction, OnOffToggleButton> functionButtons = Maps.newConcurrentMap();

    private Label lblName;
    private Label lblState;
    private Label lblStateDetails;

    private int lastSendSpeedValue = -1;
    private OnOffToggleButton btnHorn;
    private OnOffToggleButton btnLight;
    private Button btnStop;

    public TrainItemPanel(Train train) {
        super(train);
        assert getModel() != null;
        lblName = new Label(getModel().getName());
        lblState = new Label();
        lblStateDetails = new Label();
    }

    @Override
    protected void deviceConnectionChanged(boolean connected) {
        for (OnOffToggleButton button : functionButtons.values()) {
            button.setEnabled(connected);
        }
        btnHorn.setEnabled(connected);
        btnLight.setEnabled(connected);
        btnDirectionBackward.setEnabled(connected);
        btnDirectionForward.setEnabled(connected);
        btnStop.setEnabled(connected);
    }

    @Override
    public void updateItemData(TrainStateEvent event) {
        // TODO: slider and buttons need update for remote tracking of other device -> BUT will crash with fast input
        // and update delay
        if (event instanceof TrainHornStateEvent) {
            btnHorn.setValue(((TrainHornStateEvent) event).isState());
        } else if (event instanceof TrainLightStateEvent) {
            btnLight.setValue(((TrainLightStateEvent) event).isState());
        } else if (event instanceof TrainFunctionStateEvent) {
            TrainFunctionStateEvent functionStateEvent = (TrainFunctionStateEvent) event;
            functionButtons.get(functionStateEvent.getFunction()).setValue(functionStateEvent.isActive());
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
            final String text = "speed: " + drivingLevelEvent.getSpeed();
            lblStateDetails.setText(text);
        }
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
    public PanelCollapse createCollapseContentPanel() {
        contentPanel = new PanelCollapse();

        Button btnEditTrain = new Button();
        btnEditTrain.setIcon(IconType.PENCIL);
        btnEditTrain.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Modal editDialog = new TrainItemEditModal(getModel());
                editDialog.show();
            }
        });
        Row row = new Row();
        row.add(new Column(ColumnSize.MD_1, btnEditTrain));
        contentPanel.add(row);

        Row rowDrivingFunctions = new Row();

        ButtonGroup btnGroupDirection = new ButtonGroup();
        btnDirectionForward = createDirectionButton(true);
        btnGroupDirection.add(btnDirectionForward);
        btnDirectionBackward = createDirectionButton(false);
        btnGroupDirection.add(btnDirectionBackward);

        final Label lblSliderValue = new Label("0");
        lblSliderValue.getElement().getStyle().setMarginRight(15, Style.Unit.PX);
        lblSliderValue.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
//        sliderDrivingLevel = new Slider(0d, DRIVING_LEVEL_MAX_VALUE, 0d);
//        sliderDrivingLevel.addValueChangeHandler(
//                new ValueChangeHandler<Double>() {
//                    @Override
//                    public void onValueChange(ValueChangeEvent<Double> doubleValueChangeEvent) {
//                        lblSliderValue.setText(doubleValueChangeEvent.getValue().toString());
//                        int level = doubleValueChangeEvent.getValue().intValue();
//                        if (level != lastSendSpeedValue) {
//                            lastSendSpeedValue = level;
//                            RequestUtils.getInstance().getTrainService().updateDrivingLevel(
//                                    getModel().getId(), level);
//                        }
//                    }
//                });
        btnStop = new Button("Stop", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getTrainService().updateDrivingLevel(
                        getModel().getId(), 0, RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });

//        rowDrivingFunctions.add(new Column(ColumnSize.MD_12, btnGroupDirection, lblSliderValue, sliderDrivingLevel,btnStop));
        rowDrivingFunctions.add(new Column(ColumnSize.MD_12, btnGroupDirection, lblSliderValue, btnStop));

        contentPanel.add(rowDrivingFunctions);
//
        initFunctions();

//        // set the initial state for the train by using the event callback
//        if (getModel().getDrivingDirection() != null) {
//            updateItemData(new TrainDrivingDirectionEvent(getModel().getId(),
//                    TrainDrivingDirectionEvent.DRIVING_DIRECTION.valueOf(getModel().getDrivingDirection().name())));
//        }
//        updateItemData(new TrainDrivingLevelEvent(getModel().getId(), getModel().getDrivingLevel()));
//
//        if(getModel().getFunctions()!=null ) {
//            // TODO ... if null
//        for (TrainFunction trainFunction : getModel().getFunctions()) {
//            if (trainFunction.getFunction() == TrainFunction.FUNCTION.HORN) {
//                updateItemData(new TrainHornStateEvent(getModel().getId(), trainFunction.isActive()));
//            } else if (trainFunction.getFunction() == TrainFunction.FUNCTION.LIGHT) {
//                updateItemData(new TrainLightStateEvent(getModel().getId(), trainFunction.isActive()));
//            }
//        }
//            // TODO train functions
//
//        }
        return contentPanel;
    }

    private void initFunctions() {
        Column functionsColumn = new Column(ColumnSize.LG_1.getCssName());
        contentPanel.add(functionsColumn);

        btnHorn = new OnOffToggleButton("Horn", new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                RequestUtils.getInstance().getTrainService().toggleHorn(getModel().getId(),
                        event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
//        btnHorn = createFunctionButton("Horn");
//        btnHorn.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                RequestUtils.getInstance().getTrainService().toggleHorn(getModel().getId(),
//                        btnHorn.isActive(), RequestUtils.VOID_ASYNC_CALLBACK);
//    }
//        });
        functionsColumn.add(btnHorn);

        btnLight = new OnOffToggleButton("Light", new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                RequestUtils.getInstance().getTrainService().toggleLight(getModel().getId(),
                        event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        functionsColumn.add(btnLight);

        // extra functions
        if (getModel().getFunctions() != null) {
            for (final TrainFunction functionEntry : getModel().getFunctions()) {

                final OnOffToggleButton btnToggleFunction = new OnOffToggleButton(functionEntry.getAlias(), new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        RequestUtils.getInstance().getTrainService().toggleFunctionState(getModel().getId(),
                                functionEntry, event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK);
                    }
                });
//                final Button btnToggleFunction = createFunctionButton(functionEntry.getAlias());
                functionButtons.put(functionEntry, btnToggleFunction);
                functionsColumn.add(btnToggleFunction);
            }
        }

    }

//    private Button createFunctionButton(String title) {
//        final Button btnToggleFunction = new Button(title);
//        btnToggleFunction.setDataToggle(Toggle.BUTTON);
//        return btnToggleFunction;
//    }

    private Button createDirectionButton(final boolean forward) {
        Button btnDirection;
        if (forward) {
            btnDirection = new Button(">>");
        } else {
            btnDirection = new Button("<<");
        }
        btnDirection.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getTrainService().toggleDrivingDirection(getModel().getId(), forward, RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        return btnDirection;
    }
}
