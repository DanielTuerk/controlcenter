package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackModelConstants;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractBlockSvgTrackWidget<T extends TrackPart> extends AbstractSvgTrackWidget<T>
        implements EditTrackWidgetHandler, BlockPart {

    private static final String ID_FORM_BIT = "formBit";
    private Select selectBit;
    private TextBox txtAddress;

    @Override
    public void freeBlock() {
        removeStyleName("unknownBlock");
        removeStyleName("usedBlock");
        addStyleName("freeBlock");
    }

    @Override
    public void unknownBlock() {
        addStyleName("unknownBlock");
        removeStyleName("usedBlock");
        removeStyleName("freeBlock");
    }

    @Override
    public void usedBlock() {
        removeStyleName("unknownBlock");
        addStyleName("usedBlock");
        removeStyleName("freeBlock");
    }

    @Override
    public Map<String, Configuration> getStoredWidgetFunctionConfigs() {
        Map<String, Configuration> functionConfigs = super.getStoredWidgetFunctionConfigs();
        if (getTrackPart().getDefaultBlockFunctionConfig() != null) {
            Configuration configuration = new Configuration();
            configuration.setBus(1); //TODO
            configuration.setAddress(getTrackPart().getDefaultBlockFunctionConfig().getAddress());
            configuration.setBit(getTrackPart().getDefaultBlockFunctionConfig().getBit());
            configuration.setBitState(true);
            functionConfigs.put(TrackModelConstants.DEFAULT_BLOCK_FUNCTION, configuration);
        }
        return functionConfigs;
    }

    @Override
    public void updateFunctionState(Configuration configuration, boolean state) {
        // update the SVG for the state of the {@link TrackPart#DEFAULT_TOGGLE_FUNCTION}
        Configuration blockFunctionConfig = getStoredWidgetFunctionConfigs().get(TrackModelConstants.DEFAULT_BLOCK_FUNCTION);
        if (blockFunctionConfig != null && blockFunctionConfig.equals(configuration)) {
            if (state == blockFunctionConfig.isBitState()) {
                usedBlock();
            } else {
                freeBlock();
            }
        }
    }

    @Override
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

        FieldSet fieldSet = new FieldSet();
        // module address
        FormGroup groupModuleAddress = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("Address");
        lblAddress.setFor(ID_FORM_ADDRESS);
        groupModuleAddress.add(lblAddress);

        txtAddress = new TextBox();
        txtAddress.setId(ID_FORM_ADDRESS);
        if (getTrackPart().getDefaultToggleFunctionConfig().getAddress() >= 0) {
            txtAddress.setText(String.valueOf(getTrackPart().getDefaultBlockFunctionConfig().getAddress()));
        }
        org.gwtbootstrap3.client.ui.gwt.FlowPanel flowPanel = new org.gwtbootstrap3.client.ui.gwt.FlowPanel();
        flowPanel.addStyleName(ColumnSize.MD_2.getCssName());
        flowPanel.add(txtAddress);
        groupModuleAddress.add(flowPanel);
        fieldSet.add(groupModuleAddress);

        // module bit
        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_FORM_BIT);
        lblBit.setText("Bit");
        groupBit.add(lblBit);

        selectBit = new Select();
        selectBit.setId(ID_FORM_BIT);
        for (int index = 1; index < 9; index++) {
            Option option = new Option();
            String value = String.valueOf(index);
            option.setValue(value);
            option.setText(value);
            selectBit.add(option);
            if (index == getTrackPart().getDefaultBlockFunctionConfig().getBit()) {
                selectBit.setValue(option);
            }
        }
        groupBit.add(selectBit);
        fieldSet.add(groupBit);


        addDialogContentTab("Block", fieldSet);

        return dialogContent;
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();
        // save block config
        getTrackPart().getDefaultBlockFunctionConfig().setBus(1);
        getTrackPart().getDefaultBlockFunctionConfig().setAddress(Integer.parseInt(txtAddress.getText()));
        getTrackPart().getDefaultBlockFunctionConfig().setBit(Integer.parseInt(selectBit.getValue()));
    }

}
