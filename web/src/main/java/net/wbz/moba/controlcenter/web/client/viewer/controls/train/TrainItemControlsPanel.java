package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.components.OnOffToggleButton;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDrivingDirectionRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.train.TrainDrivingLevelRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.train.TrainFunctionStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.train.TrainHornStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.train.TrainLightStateRemoteListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.client.train.TrainEditModal;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractViewerItemControlsComposite;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDrivingDirectionEvent.DRIVING_DIRECTION;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunctionStateEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.extras.slider.client.ui.Slider;

/**
 * @author Daniel Tuerk
 */
public class TrainItemControlsPanel extends AbstractViewerItemControlsComposite<Train> {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final TrainHornStateRemoteListener trainHornStateRemoteListener;
    private final TrainLightStateRemoteListener trainLightStateRemoteListener;
    private final TrainDrivingDirectionRemoteListener trainDrivingDirectionRemoteListener;
    private final TrainFunctionStateRemoteListener trainFunctionStateRemoteListener;
    private final TrainDrivingLevelRemoteListener trainDrivingLevelRemoteListener;
    @UiField
    Button btnDirectionBackward;
    @UiField
    Button btnDirectionForward;
    @UiField
    OnOffToggleButton btnHorn;
    @UiField
    OnOffToggleButton btnLight;
    @UiField
    Button btnStop;
    @UiField
    FlowPanel trainFunctionsPanel;
    @UiField
    Slider sliderDrivingLevel;

    private Map<TrainFunction, OnOffToggleButton> functionButtons = Maps.newConcurrentMap();
    private int lastSendSpeedValue = -1;

    TrainItemControlsPanel(Train train) {
        super(train);

        trainHornStateRemoteListener = new TrainHornStateRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void hornChanged(boolean state) {
                btnHorn.setValue(state, false);
            }
        };
        trainLightStateRemoteListener = new TrainLightStateRemoteListener() {

            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void lightChanged(boolean state) {
                btnLight.setValue(state, false);
            }
        };
        trainDrivingDirectionRemoteListener = new TrainDrivingDirectionRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void drivingDirectionChanged(DRIVING_DIRECTION drivingDirection) {
                switch (drivingDirection) {
                    case BACKWARD:
                        btnDirectionBackward.setActive(true);
                        btnDirectionForward.setActive(false);
                        break;
                    case FORWARD:
                        btnDirectionForward.setActive(true);
                        btnDirectionBackward.setActive(false);
                        break;
                }
            }
        };
        trainDrivingLevelRemoteListener = new TrainDrivingLevelRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void drivingLevelChanged(int drivingLevel) {
                sliderDrivingLevel.setValue((double) drivingLevel, false);
            }
        };
        trainFunctionStateRemoteListener = new TrainFunctionStateRemoteListener() {
            @Override
            public long getTrainId() {
                return train.getId();
            }

            @Override
            public void functionStateChanged(TrainFunctionStateEvent functionStateEvent) {
                if (functionButtons.containsKey(functionStateEvent.getFunction())) {
                    functionButtons.get(functionStateEvent.getFunction()).setValue(functionStateEvent.isActive());
                }
            }
        };

        initWidget(uiBinder.createAndBindUi(this));
        initExtraFunctions();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trainHornStateRemoteListener, trainLightStateRemoteListener,
            trainDrivingDirectionRemoteListener, trainDrivingLevelRemoteListener, trainFunctionStateRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(trainHornStateRemoteListener, trainLightStateRemoteListener,
            trainDrivingDirectionRemoteListener, trainDrivingLevelRemoteListener, trainFunctionStateRemoteListener);
    }

    @Override
    protected void deviceConnectionChanged(boolean connected) {
        boolean state = getModel().getAddress() != null && connected;
        for (OnOffToggleButton button : functionButtons.values()) {
            button.setEnabled(state);
        }
        btnHorn.setEnabled(state);
        btnLight.setEnabled(state);
        btnDirectionBackward.setEnabled(state);
        btnDirectionForward.setEnabled(state);
        btnStop.setEnabled(state);
    }

    @UiHandler("btnEditTrain")
    void btnEditTrainClicked(ClickEvent event) {
        Modal editDialog = new TrainEditModal(getModel());
        editDialog.show();
    }

    @UiHandler("btnStop")
    void btnStopClicked(ClickEvent event) {
        RequestUtils.getInstance().getTrainService().updateDrivingLevel(
            getModel().getId(), 0, RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @UiHandler("sliderDrivingLevel")
    void onSlide(ValueChangeEvent<Double> event) {
        int level = event.getValue().intValue();
        if (level != lastSendSpeedValue) {
            lastSendSpeedValue = level;
            RequestUtils.getInstance().getTrainService().updateDrivingLevel(
                getModel().getId(), level, RequestUtils.VOID_ASYNC_CALLBACK);
        }
    }

    @UiHandler("btnLight")
    void btnLightToggled(ValueChangeEvent<Boolean> event) {
        RequestUtils.getInstance().getTrainService().toggleLight(getModel().getId(),
            event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @UiHandler("btnHorn")
    void btnHornToggled(ValueChangeEvent<Boolean> event) {
        RequestUtils.getInstance().getTrainService().toggleHorn(getModel().getId(),
            event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @UiHandler("btnDirectionForward")
    void setBtnDirectionForwardClicked(ClickEvent event) {
        changeDirection(true);
    }

    @UiHandler("btnDirectionBackward")
    void setBtnDirectionBackwardClicked(ClickEvent event) {
        changeDirection(false);
    }

    private void initExtraFunctions() {
        if (getModel().getFunctions() != null) {
            for (final TrainFunction functionEntry : getModel().getFunctions()) {

                final OnOffToggleButton btnToggleFunction = new OnOffToggleButton(functionEntry.getAlias(),
                    event -> RequestUtils.getInstance().getTrainService().toggleFunctionState(getModel().getId(),
                        functionEntry, event.getValue(), RequestUtils.VOID_ASYNC_CALLBACK));
                functionButtons.put(functionEntry, btnToggleFunction);
                trainFunctionsPanel.add(btnToggleFunction);
            }
        }

    }

    private void changeDirection(boolean forward) {
        RequestUtils.getInstance().getTrainService().toggleDrivingDirection(getModel().getId(), forward,
            RequestUtils.VOID_ASYNC_CALLBACK);
    }

    interface Binder extends UiBinder<Widget, TrainItemControlsPanel> {

    }

}
