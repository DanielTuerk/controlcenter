package net.wbz.moba.controlcenter.web.client.model.track;

import java.util.Map;

import net.wbz.moba.controlcenter.web.client.components.TrackBlockSelect;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import net.wbz.moba.controlcenter.web.shared.train.Train;

/**
 * Abstract track widget for {@link AbstractTrackPart} which has a block function to change the style of the element
 * by the state of the block.
 * The widget change for the {@link BlockPart}:
 * <ul>
 * <li>{@link BlockPart#freeBlock()} - block function is {@code 0}</li>
 * <li>{@link BlockPart#usedBlock()} - block function is {@code 1}</li>
 * <li>{@link BlockPart#unknownBlock()} - block function is unknown</li>
 * </ul>
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractBlockSvgTrackWidget<T extends AbstractTrackPart> extends AbstractSvgTrackWidget<T>
        implements EditTrackWidgetHandler, BlockPart {

    private static final String ID_FORM_BLOCK = "formBit_block";
    private TrackBlockSelect selectBlock;
    /**
     * Mapping of elements on the track part by each train.
     * Used to add or remove an element for entering and exiting the block of a train
     * called by the {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent}.
     */
    private Map<Train, OMSVGElement> trainElements = Maps.newConcurrentMap();

    @Override
    protected void onLoad() {
        super.onLoad();

        selectBlock = new TrackBlockSelect();
        selectBlock.setId(ID_FORM_BLOCK);
    }

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
    public Widget getDialogContent() {
        Widget dialogContent = super.getDialogContent();

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

        addDialogContentTab("Block", fieldSet);
        return dialogContent;
    }

    // private void loadTrackBlocks() {
    // RequestUtils.getInstance().getTrackEditorService().loadTrackBlocks(new AsyncCallback<Collection<TrackBlock>>() {
    // @Override
    // public void onFailure(Throwable throwable) {
    //
    // }
    //
    // @Override
    // public void onSuccess(Collection<TrackBlock> trackBlocks) {
    // selectBlock.add(new DeleteOption());
    //
    // for (TrackBlock trackBlock : trackBlocks) {
    //
    // if (trackBlock.getId() != null) {
    // Option option = new Option();
    // String value = String.valueOf(trackBlock.getId());
    // option.setValue(value);
    // option.setText(trackBlock.getDisplayValue());
    // selectBlock.add(option);
    // trackBlockSelectOptions.put(value, trackBlock);
    //
    // if (getTrackPart().getTrackBlock() != null) {
    // if (getTrackPart().getTrackBlock().getId().equals(trackBlock.getId())) {
    // selectBlock.setValue(value);
    // }
    // }
    // }
    // }
    // selectBlock.refresh();
    // }
    // });
    //
    // }

    @Override
    public void onConfirmCallback() {
        super.onConfirmCallback();
        // save block config
        TrackBlock trackBlock = selectBlock.getSelectedTrackBlockValue();
        // if (!Strings.isNullOrEmpty(selectBlock.getValue())) {
        // trackBlock = trackBlockSelectOptions.get(selectBlock.getValue());
        // }
        getTrackPart().setTrackBlock(trackBlock);
    }

    /**
     * Show the information of the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} on this block
     * element.
     *
     * @param train {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     */
    public void showTrainOnBlock(Train train) {
        OMSVGTextElement trainTextElement = getSvgDocument().createSVGTextElement(1f, 24f, (short) 1, train.getName());
        trainTextElement.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "8pt");
        trainElements.put(train, trainTextElement);
        getSvgRootElement().appendChild(trainTextElement);
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

    @Override
    public String getConfigurationInfo() {
        return super.getConfigurationInfo() + "block: " + getTrackPart().getBlockFunction() + "; ";
    }

    public void updateBlockState(BusDataConfiguration configuration, boolean state) {
        // update the SVG for the state of the block
        BusDataConfiguration blockFunctionConfig = getTrackPart().getBlockFunction();
        if (blockFunctionConfig != null && blockFunctionConfig.equals(configuration)) {
            if (state == blockFunctionConfig.getBitState()) {
                usedBlock();
            } else {
                freeBlock();
            }
        }
    }

    private class DeleteOption extends Option {
        public DeleteOption() {
            setValue(null);
            setText("-- none --");
        }
    }
}
