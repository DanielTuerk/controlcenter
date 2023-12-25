package net.wbz.moba.controlcenter.web.client.editor.track;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock.DRIVING_LEVEL_ADJUST_TYPE;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Legend;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class BlockEditBody extends Composite {

    private final TrackBlock trackBlock;

    interface BlockEditBodyBinder extends UiBinder<Widget, BlockEditBody> {
    }

    private static BlockEditBodyBinder uiBinder = GWT.create(BlockEditBodyBinder.class);

    @UiField
    Legend legend;

    @UiField
    TextBox txtName;
    @UiField
    TextBox txtAddress;
    @UiField
    TextBox txtBit;

    @UiField
    ListBox selectDrivingLevelAdjustType;
    @UiField
    TextBox txtForwardTargetDrivingLevel;
    @UiField
    TextBox txtBackwardTargetDrivingLevel;
    @UiField
    CheckBox cbxFeedback;

    public BlockEditBody(TrackBlock trackBlock) {
        this.trackBlock = trackBlock;
        initWidget(uiBinder.createAndBindUi(this));

        for (DRIVING_LEVEL_ADJUST_TYPE adjustType : DRIVING_LEVEL_ADJUST_TYPE.values()) {
            selectDrivingLevelAdjustType.addItem(adjustType.name());
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        legend.setText("Edit Block: " + trackBlock.getId());

        txtName.setText(trackBlock.getName());

        if (trackBlock.getBlockFunction() != null) {
            txtAddress.setText(getStringValue(trackBlock.getBlockFunction().getAddress()));
        }
        if (trackBlock.getBlockFunction() != null) {
            txtBit.setText(getStringValue(trackBlock.getBlockFunction().getBit()));
        }

        selectDrivingLevelAdjustType.setSelectedIndex(trackBlock.getDrivingLevelAdjustType().ordinal());

        txtForwardTargetDrivingLevel.setText(getStringValue(trackBlock.getForwardTargetDrivingLevel()));
        txtBackwardTargetDrivingLevel.setText(getStringValue(trackBlock.getBackwardTargetDrivingLevel()));
        cbxFeedback.setValue(trackBlock.getFeedback());
    }

    void applyChanges() {
        trackBlock.setName(txtName.getText());
        if (trackBlock.getBlockFunction() == null) {
            BusDataConfiguration blockFunction = new BusDataConfiguration();
            blockFunction.setBus(1);
            trackBlock.setBlockFunction(blockFunction);
        }
        trackBlock.getBlockFunction().setAddress(getIntegerValue(txtAddress.getText()));
        trackBlock.getBlockFunction().setBit(getIntegerValue(txtBit.getText()));
        // driving level
        trackBlock.setDrivingLevelAdjustType(DRIVING_LEVEL_ADJUST_TYPE.valueOf(
                selectDrivingLevelAdjustType.getSelectedValue()));
        trackBlock.setForwardTargetDrivingLevel(getIntegerValue(txtForwardTargetDrivingLevel.getText()));
        trackBlock.setBackwardTargetDrivingLevel(getIntegerValue(txtBackwardTargetDrivingLevel.getText()));
        trackBlock.setFeedback(cbxFeedback.getValue());
    }

    private String getStringValue(Number bit) {
        return bit == null ? "" : String.valueOf(bit);
    }

    private Integer getIntegerValue(String text) {
        return Strings.isNullOrEmpty(text) ? null : Integer.valueOf(text);
    }
}
