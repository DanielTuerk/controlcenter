package net.wbz.moba.controlcenter.web.client.train;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Map;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class TrainEditModalBody extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Train train;
    private final Map<TrainFunction, TrainFunctionFieldSet> trainFunctionFormGroups = Maps.newConcurrentMap();
    @UiField
    TextBox txtName;
    @UiField
    IntegerBox txtAddress;
    @UiField
    Panel functionsPanel;

    TrainEditModalBody(Train train) {
        this.train = train;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        txtName.setText(train.getName());
        txtAddress.setText(String.valueOf(train.getAddress()));

        functionsPanel.clear();
        for (TrainFunction trainFunction : train.getFunctions()) {
            functionsPanel.add(addFunctionFormGroup(trainFunction));
        }
    }

    @UiHandler("btnAddFunction")
    public void onClick(ClickEvent ignored) {
        TrainFunction trainFunction = new TrainFunction();
        trainFunction.setConfiguration(new BusDataConfiguration());
        // default alias
        trainFunction.setAlias("F" + (train.getFunctions().size() + 1));
        train.getFunctions().add(trainFunction);
        functionsPanel.add(addFunctionFormGroup(trainFunction));
    }

    private TrainFunctionFieldSet addFunctionFormGroup(final TrainFunction trainFunction) {
        final TrainFunctionFieldSet trainFunctionFieldSet = new TrainFunctionFieldSet(trainFunction) {
            @Override
            void onDelete() {
                TrainFunctionFieldSet functionContainer = trainFunctionFormGroups.get(trainFunction);
                functionsPanel.remove(functionContainer);
                trainFunctionFormGroups.remove(trainFunction);

                train.getFunctions().remove(trainFunction);
            }
        };
        trainFunctionFormGroups.put(trainFunction, trainFunctionFieldSet);
        return trainFunctionFieldSet;
    }

    Train getUpdatedModel() {
        train.setName(txtName.getText());
        train.setAddress(Integer.parseInt(txtAddress.getText()));

        for (TrainFunctionFieldSet trainFunctionFieldSet : trainFunctionFormGroups.values()) {
            trainFunctionFieldSet.applyChanges();
        }
        return train;
    }

    interface Binder extends UiBinder<Widget, TrainEditModalBody> {

    }
}
