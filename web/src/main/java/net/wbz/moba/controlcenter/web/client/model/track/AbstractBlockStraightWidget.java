package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import java.util.Map;
import net.wbz.moba.controlcenter.web.client.components.TrackBlockSelect;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Abstract track widget for {@link BlockStraight} which has a block function to change the style of the element by the
 * state of the block.The widget change for the {@link BlockPart}:
 * <ul>
 * <li>{@link BlockPart#freeBlock()} - block function is {@code 0}</li>
 * <li>{@link BlockPart#usedBlock()} - block function is {@code 1}</li>
 * <li>{@link BlockPart#unknownBlock()} - block function is unknown</li>
 * </ul>
 * Blocks have dynamic width defined by the length of the block.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractBlockStraightWidget extends AbstractSvgTrackWidget<BlockStraight> implements
    BlockPart, EditTrackWidgetHandler {

    private static final float BOX_PADDING = 5f;
    private static final String ID_BLOCK_LENGTH = "blockLengthBox";
    private static final String ID_FORM_BLOCK = "formBit_block";
    private static final String FEEDBACK_FONT_SIZE = "8pt";
    private IntegerBox txtLength;
    /**
     * Mapping of elements on the track part by each train. Used to add or remove an element for entering and exiting
     * the block of a train called by the {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent}.
     */
    private Map<Train, OMSVGElement> trainElements = Maps.newConcurrentMap();
    private TrackBlockSelect selectBlock;
    private OMSVGRectElement lastPaintBlockSvg;

    AbstractBlockStraightWidget() {
        selectBlock = new TrackBlockSelect();
        selectBlock.setId(ID_FORM_BLOCK);

        txtLength = new IntegerBox();
        txtLength.setId(ID_BLOCK_LENGTH);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

    }

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        float width = getWidgetWidth();
        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 0f, 10f, width, 5f, color));
        lastPaintBlockSvg = SvgTrackUtil
            .createRectangle(getSvgDocument(), BOX_PADDING, 2f, width - 2 * BOX_PADDING, 21f, color);
        svg.appendChild(lastPaintBlockSvg);
    }

    @Override
    protected int getWidgetWidth() {
        int blockLength = getTrackPart().getBlockLength();
        return AbstractSvgTrackWidget.WIDGET_WIDTH * (blockLength > 0 ? blockLength : 1);
    }

    @Override
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

        addDialogContentTab("Length", createLengthFieldSet());
        addDialogContentTab("Block", createBlockFieldSet());

        return dialogContent;
    }

    private FieldSet createLengthFieldSet() {
        FieldSet fieldSet = new FieldSet();

        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_BLOCK_LENGTH);
        lblBit.setText("Length");
        groupBit.add(lblBit);

        txtLength.setValue(getTrackPart().getBlockLength());

        groupBit.add(txtLength);
        fieldSet.add(groupBit);
        return fieldSet;
    }

    private FieldSet createBlockFieldSet() {
        FieldSet fieldSet = new FieldSet();
        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_FORM_BLOCK);
        lblBit.setText("Block");
        groupBit.add(lblBit);

        // TODO to early?
        selectBlock.setSelectedTrackBlockValue(getTrackPart().getTrackBlock());
        // selectBlock.refresh();
        // loadTrackBlocks();
        groupBit.add(selectBlock);
        fieldSet.add(groupBit);
        return fieldSet;
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();
        // save block config
        TrackBlock trackBlock = selectBlock.getSelectedTrackBlockValue();
        getTrackPart().setTrackBlock(trackBlock);

        getTrackPart().setBlockLength(txtLength.getValue());
        repaint();
    }

    @Override
    public boolean isRepresentationOf(BlockStraight trackPart) {
        return trackPart.getDirection() == getStraightDirection();
    }

    @Override
    public String getPaletteTitle() {
        return "Block";
    }

    abstract public Straight.DIRECTION getStraightDirection();

    @Override
    public BlockStraight getNewTrackPart() {
        BlockStraight blockStraight = new BlockStraight();
        blockStraight.setDirection(getStraightDirection());
        return blockStraight;
    }

    /**
     * Show the information of the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} on this block
     * element.
     *
     * @param train {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    public void showTrainOnBlock(Train train) {
        if (!trainElements.containsKey(train)) {
            OMSVGTextElement trainTextElement = getSvgDocument()
                .createSVGTextElement(BOX_PADDING + 1f, getWidgetWidth() - 2 * BOX_PADDING - 1, (short) 1,
                    train.getName());
            trainTextElement.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, FEEDBACK_FONT_SIZE);
            trainElements.put(train, trainTextElement);
            getSvgRootElement().appendChild(trainTextElement);
        }
    }

    /**
     * Remove the element for the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} from this block.
     *
     * @param train {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    public void removeTrainOnBlock(Train train) {
        if (trainElements.containsKey(train)) {
            getSvgRootElement().removeChild(trainElements.get(train));
            trainElements.remove(train);
        }
    }

    /**
     * Reset the block to unknown state without train information.
     */
    public void resetBlock() {
        // TODO activate asap the block state is reloaded on server side for railvoltage toggle
        // unknownBlock();

        for (Train train : trainElements.keySet()) {
            removeTrainOnBlock(train);
        }
    }

    @Override
    public void freeBlock() {
        updateBlockColor("green");
    }

    @Override
    public void unknownBlock() {
        updateBlockColor("orange");
    }

    @Override
    public void usedBlock() {
        updateBlockColor("red");
    }

    private void updateBlockColor(String color) {
        if (lastPaintBlockSvg != null) {
            lastPaintBlockSvg.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
        }
    }
}
