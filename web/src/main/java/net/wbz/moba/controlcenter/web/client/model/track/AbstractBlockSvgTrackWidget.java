package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.Map;

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

    private static final String ID_FORM_ADDRESS = "formAddress_block";
    private static final String ID_FORM_BIT = "formBit_block";
    private Select selectBit;
    private TextBox txtAddress;

    /**
     * Mapping of elements on the track part by each train.
     * Used to add or remove an element for entering and exiting the block of a train
     * called by the {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent}.
     */
    private Map<Train, OMSVGElement> trainElements = Maps.newConcurrentMap();

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
        // module address
        FormGroup groupModuleAddress = new FormGroup();
        FormLabel lblAddress = new FormLabel();
        lblAddress.setText("Address");
        lblAddress.setFor(ID_FORM_ADDRESS);
        groupModuleAddress.add(lblAddress);

        txtAddress = new TextBox();
        txtAddress.setId(ID_FORM_ADDRESS);
        if (getTrackPart().getBlockFunction() != null) {
            txtAddress.setText(String.valueOf(getTrackPart().getBlockFunction().getAddress()));
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
            if (getTrackPart().getBlockFunction() != null) {
                if (index == getTrackPart().getBlockFunction().getBit()) {
                    selectBit.setValue(String.valueOf(index));
                }
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
        if (Strings.isNullOrEmpty(txtAddress.getText())) {
            getTrackPart().setBlockFunction(null);
        } else {
            if (getTrackPart().getBlockFunction() == null) {
                getTrackPart().setBlockFunction(new BusDataConfiguration());
            }
            getTrackPart().getBlockFunction().setBus(1);
            getTrackPart().getBlockFunction().setAddress(Integer.parseInt(txtAddress.getText()));
            getTrackPart().getBlockFunction().setBit(Integer.parseInt(selectBit.getValue()));
            getTrackPart().getBlockFunction().setBitState(true);
        }
    }

    /**
     * Show the information of the given {@link net.wbz.moba.controlcenter.web.shared.train.Train} on this block element.
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
}
