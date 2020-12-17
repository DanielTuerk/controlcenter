package net.wbz.moba.controlcenter.web.client.model.track.block;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import java.util.Map;
import java.util.Objects;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.track.TrackBlockRemoteListener;
import net.wbz.moba.controlcenter.web.client.model.track.AbstractSvgTrackWidget;
import net.wbz.moba.controlcenter.web.client.util.SvgTrackUtil;
import net.wbz.moba.controlcenter.web.shared.track.model.BlockStraight;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight.DIRECTION;
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
    BlockPart<BlockStraight>, EditTrackWidgetHandler, Feedbackable {

    static final float BOX_PADDING = 5f;
    private static final String ID_BLOCK_LENGTH = "blockLengthBox";
    private static final String FEEDBACK_FONT_SIZE = "8pt";
    private final IntegerBox txtLength;
    /**
     * Mapping of elements on the track part by each train. Used to add or remove an element for entering and exiting
     * the block of a train called by the {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent}.
     */
    private final Map<Train, OMSVGElement> trainElements = Maps.newConcurrentMap();
    private final FeedbackBlockRemoteListener feedbackBlockRemoteListener;
    private final TrackBlockRemoteListener blockEventListener;
    /**
     * Last painted SVG for the block to change the color for the actual occupied state.
     */
    private OMSVGRectElement lastPaintBlockSvg;
    private BlockStraightEditBlockFieldSet blockFieldSet;

    AbstractBlockStraightWidget() {
        txtLength = new IntegerBox();
        txtLength.setId(ID_BLOCK_LENGTH);

        blockEventListener = new BlockStateRemoteListener(this);
        feedbackBlockRemoteListener = new FeedbackBlockRemoteListener(this);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(blockEventListener, feedbackBlockRemoteListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(blockEventListener, feedbackBlockRemoteListener);
    }

    abstract public Straight.DIRECTION getStraightDirection();

    abstract protected BlockStraightEditBlockFieldSet createBlockFieldSet();

    @Override
    protected void addSvgContent(OMSVGDocument doc, OMSVGSVGElement svg, String color) {
        int width = getWidgetWidth();

        svg.appendChild(SvgTrackUtil.createRectangle(getSvgDocument(), 0f, 10f, width, 5f, color));

        lastPaintBlockSvg = SvgTrackUtil
            .createRectangle(getSvgDocument(), BOX_PADDING, 2f, width - 2 * BOX_PADDING, 21f, color);
        svg.appendChild(lastPaintBlockSvg);
    }

    void setLastPaintBlockSvg(OMSVGRectElement lastPaintBlockSvg) {
        this.lastPaintBlockSvg = lastPaintBlockSvg;
    }

    @Override
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

        addDialogContentTab("Length", createLengthFieldSet());
        blockFieldSet = createBlockFieldSet();
        addDialogContentTab("Block", blockFieldSet);

        return dialogContent;
    }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();
        // save block config
        getTrackPart().setLeftTrackBlock(blockFieldSet.getSelectedLeftBlock());
        getTrackPart().setMiddleTrackBlock(blockFieldSet.getSelecteMiddleBlock());
        getTrackPart().setRightTrackBlock(blockFieldSet.getSelectedRightBlock());

        getTrackPart().setBlockLength(txtLength.getValue());
        repaint();
    }

    @Override
    public String getTrackWidgetStyleName() {
        return null;
    }

    private FieldSet createLengthFieldSet() {
        FieldSet fieldSet = new FieldSet();

        FormGroup groupBit = new FormGroup();
        FormLabel lblBit = new FormLabel();
        lblBit.setFor(ID_BLOCK_LENGTH);
        lblBit.setText("Length");
        groupBit.add(lblBit);

        txtLength.setValue(getTrackPart().getGridLength());

        groupBit.add(txtLength);
        fieldSet.add(groupBit);
        return fieldSet;
    }

    @Override
    public boolean isRepresentationOf(BlockStraight trackPart) {
        return trackPart.getDirection() == getStraightDirection();
    }

    @Override
    public String getPaletteTitle() {
        return "Block";
    }

    @Override
    public BlockStraight getNewTrackPart() {
        BlockStraight blockStraight = new BlockStraight();
        blockStraight.setDirection(getStraightDirection());
        return blockStraight;
    }

    @Override
    public boolean hasBlock(BusDataConfiguration configAsIdentifier) {
        return getTrackPart().getAllTrackBlocks()
            .stream()
            .filter(Objects::nonNull).anyMatch(x -> configAsIdentifier.isSameConfig(x.getBlockFunction()));
    }

    @Override
    public void showTrainOnBlock(Train train) {
        if (!trainElements.containsKey(train)) {
            OMSVGTextElement trainTextElement = getSvgDocument()
                .createSVGTextElement(BOX_PADDING + 1f, getWidgetHeight() - BOX_PADDING - 1, (short) 1,
                    train.getName());
            if (getStraightDirection() == DIRECTION.VERTICAL) {
                trainTextElement.getStyle().setSVGProperty(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, "rotate(-90deg)");
            }
            trainTextElement.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, FEEDBACK_FONT_SIZE);
            trainElements.put(train, trainTextElement);
            getSvgRootElement().appendChild(trainTextElement);
        }
    }

    @Override
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
