package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import java.util.Map;

/**
 * Modal to edit the {@link TrainEntity} data.
 *
 * @author Daniel Tuerk
 */
public class TrainItemEditModal extends Modal {

    private final Train train;
    private final Map<TrainFunction, TrainFunctionContainer> trainFunctionFormGroups = Maps.newConcurrentMap();
    private TextBox txtName;
    private TextBox txtAddress;
    private Form createForm;

    public TrainItemEditModal(Train train) {
        this.train = train;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        setTitle("Edit train: " + train.getName());

        ModalBody modalBody = new ModalBody();
        modalBody.add(createContent());
        add(modalBody);

        createFooter();

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        clear();
    }

    private Widget createContent() {
        createForm = new Form(FormType.HORIZONTAL);
        FormGroup nameGroup = new FormGroup();
        FormLabel lblName = new FormLabel();
        lblName.addStyleName("col-lg-2");
        lblName.setText("Name");
        nameGroup.add(lblName);
        FlowPanel namePanel = new FlowPanel();
        namePanel.addStyleName("col-lg-10");
        txtName = new TextBox();
        txtName.setText(train.getName());
        namePanel.add(txtName);
        nameGroup.add(namePanel);
        createForm.add(nameGroup);

        FormGroup addressGroup = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.addStyleName("col-lg-2");
        lblAddress.setText("Address");
        addressGroup.add(lblAddress);
        FlowPanel addressPanel = new FlowPanel();
        addressPanel.addStyleName("col-lg-10");
        txtAddress = new TextBox();
        txtAddress.setText(String.valueOf(train.getAddress()));
        addressPanel.add(txtAddress);
        addressGroup.add(addressPanel);
        createForm.add(addressGroup);

        Button btnAddFunction = new Button("add Function", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TrainFunction trainFunction = new TrainFunction();
                trainFunction.setConfiguration(new BusDataConfiguration());
                // default alias
                trainFunction.setAlias("F" + (train.getFunctions().size() + 1));
                train.getFunctions().add(trainFunction);
                createForm.add(addFunctionFormGroup(trainFunction));
            }
        });
        createForm.add(btnAddFunction);

        for (TrainFunction trainFunction : train.getFunctions()) {
            createForm.add(addFunctionFormGroup(trainFunction));
        }

        Button btnDelete = new Button("Delete TrainEntity", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                TrainItemEditModal.this.hide();

                RequestUtils.getInstance().getTrainEditorService().deleteTrain(train.getId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Notify.notify("", "Delete TrainEntity " + train.getName() + " Error: " + caught.getMessage(),
                                IconType.WARNING, NotifyType.DANGER);

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Notify.notify("", "TrainEntity " + train.getName() + " deleted", IconType.INFO);
                    }
                });
            }
        });
        btnDelete.setType(ButtonType.DANGER);
        createForm.add(btnDelete);

        return createForm;
    }


    private TrainFunctionContainer addFunctionFormGroup(final TrainFunction trainFunction) {
        final TrainFunctionContainer trainFunctionContainer = new TrainFunctionContainer(trainFunction, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TrainFunctionContainer functionContainer = trainFunctionFormGroups.get(trainFunction);
                createForm.remove(functionContainer);
                trainFunctionFormGroups.remove(trainFunction);

                train.getFunctions().remove(trainFunction);
            }
        });
        trainFunctionFormGroups.put(trainFunction, trainFunctionContainer);
        return trainFunctionContainer;
    }

    private void createFooter() {
        ModalFooter modalFooter = new ModalFooter();

        Button btnSave = new Button("Save", IconType.SAVE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!Strings.isNullOrEmpty(txtAddress.getValue())) {
                    train.setAddress(Integer.parseInt(txtAddress.getValue()));
                }
                train.setName(txtName.getValue());

                for (TrainFunctionContainer trainFunctionContainer : trainFunctionFormGroups.values()) {
                    trainFunctionContainer.updateTrainFunctionFromForm();
                }


                RequestUtils.getInstance().getTrainEditorService().updateTrain(train, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        TrainItemEditModal.this.hide();
                    }
                });
            }
        });
        btnSave.setType(ButtonType.SUCCESS);
        modalFooter.add(btnSave);

        Button btnCancel = new Button("Cancel");
        btnCancel.setType(ButtonType.WARNING);
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TrainItemEditModal.this.hide();
            }
        });
        modalFooter.add(btnCancel);
        add(modalFooter);
    }
}
