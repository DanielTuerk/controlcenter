package net.wbz.moba.controlcenter.web.client.viewer.controls.train;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ClickHandler;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

/**
 * @author Daniel Tuerk
 */
public class TrainFunctionContainer extends FieldSet {
    private final TrainFunction trainFunction;
    private final TextBox txtAlias;
    private final TextBox txtAddress;
    private final Select selectBit;

    public TrainFunctionContainer(TrainFunction trainFunction, ClickHandler deleteClickHandler) {
        this.trainFunction = trainFunction;

        // alias
        FormGroup groupAlias = new FormGroup();
        FormLabel lblAlias = new FormLabel();
        lblAlias.addStyleName("col-lg-2");
        lblAlias.setText("Alias");
        groupAlias.add(lblAlias);

        txtAlias = new TextBox();
        txtAlias.addStyleName("col-lg-10");
        txtAlias.setText(String.valueOf(trainFunction.getAlias()));
        groupAlias.add(txtAlias);
        add(groupAlias);

        // address
        FormGroup groupAddress = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.addStyleName("col-lg-2");
        lblAddress.setText("Address");
        groupAddress.add(lblAddress);

        txtAddress = new TextBox();
        txtAddress.addStyleName("col-lg-10");
        txtAddress.setText(String.valueOf(trainFunction.getConfiguration().getAddress()));
        groupAddress.add(txtAddress);
        add(groupAddress);

        // module bit
        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setText("Bit");
        groupBit.add(lblBit);

        selectBit = new Select();
        for (int index = 1; index < 9; index++) {
            Option option = new Option();
            String value = String.valueOf(index);
            option.setValue(value);
            option.setText(value);
            selectBit.add(option);
        }
        if (trainFunction.getConfiguration().getBit() > 0) {
            selectBit.setValue(String.valueOf(trainFunction.getConfiguration().getBit()));
        } else {
            selectBit.setValue("1");
        }

        groupBit.add(selectBit);

        add(groupBit);

        Button btnDelete = new Button("", IconType.TRASH, deleteClickHandler);
        add(btnDelete);

    }

    public void updateTrainFunctionFromForm() {
        trainFunction.setAlias(txtAlias.getText());

        String addressValueText = txtAddress.getValue();
        if (!Strings.isNullOrEmpty(addressValueText)) {
            trainFunction.getConfiguration().setAddress(Integer.valueOf(addressValueText));
        }

        trainFunction.getConfiguration().setBit(Integer.parseInt(selectBit.getValue()));
    }
}
