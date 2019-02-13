package net.wbz.moba.controlcenter.web.client.model.track.block;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.components.TrackBlockSelect;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.gwtbootstrap3.client.ui.FormLabel;

/**
 * Composite with {@link org.gwtbootstrap3.client.ui.FieldSet} to select the left, middle and right block.
 *
 * @author Daniel Tuerk
 */
class BlockStraightEditBlockFieldSet extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    @UiField
    FormLabel lblLeftBlock;
    @UiField
    TrackBlockSelect leftBlockSelect;
    @UiField
    TrackBlockSelect middleBlockSelect;
    @UiField
    FormLabel lblRightBlock;
    @UiField
    TrackBlockSelect rightBlockSelect;

    BlockStraightEditBlockFieldSet(String leftBlockText, String rightBlockText,
        BlockStraight trackPart) {
        initWidget(uiBinder.createAndBindUi(this));
        lblLeftBlock.setText(leftBlockText);
        lblRightBlock.setText(rightBlockText);

        leftBlockSelect.setSelectedTrackBlockValue(trackPart.getLeftTrackBlock());
        middleBlockSelect.setSelectedTrackBlockValue(trackPart.getMiddleTrackBlock());
        rightBlockSelect.setSelectedTrackBlockValue(trackPart.getRightTrackBlock());
    }

    TrackBlock getSelectedLeftBlock() {
        return leftBlockSelect.getSelectedTrackBlockValue();
    }

    TrackBlock getSelecteMiddleBlock() {
        return middleBlockSelect.getSelectedTrackBlockValue();
    }

    TrackBlock getSelectedRightBlock() {
        return rightBlockSelect.getSelectedTrackBlockValue();
    }

    interface Binder extends UiBinder<Widget, BlockStraightEditBlockFieldSet> {

    }
}
