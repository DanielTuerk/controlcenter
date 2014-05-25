package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractBlockImageTrackWidget<T extends TrackPart> extends AbstractImageTrackWidget<T>
        implements EditTrackWidgetHandler, BlockPart {

    private final ListBox selectBit = new ListBox();
    private final TextBox txtAddress = new TextBox();

    private int trackPartConfigAddress = -1;
    private int trackPartConfigBit = -1;

    public AbstractBlockImageTrackWidget() {
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


    public Configuration getStoredWidgetConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setAddress(trackPartConfigAddress);
        configuration.setOutput(trackPartConfigBit);
        return configuration;
    }

    @Override
    public VerticalPanel getDialogContent() {
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        FlexTable.FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

        // Add a title to the form
        layout.setHTML(0, 0, "Module Adress");
        cellFormatter.setColSpan(0, 0, 2);
        cellFormatter.setHorizontalAlignment(
                0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        if (trackPartConfigAddress >= 0) {
            txtAddress.setText(String.valueOf(trackPartConfigAddress));
        }

        for (int index = 0; index < 8; index++) {
            selectBit.addItem(String.valueOf(index + 1));
            if (index + 1 == trackPartConfigBit) {
                selectBit.setSelectedIndex(index);
            }
        }

        // Add some standard form options
        layout.setHTML(1, 0, "Address:");
        layout.setWidget(1, 1, txtAddress);
        layout.setHTML(2, 0, "Bit:");
        layout.setWidget(2, 1, selectBit);

        // Wrap the content in a DecoratorPanel
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.setWidget(layout);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(decPanel);

        return verticalPanel;
    }

    @Override
    public void onConfirmCallback() {
        trackPartConfigAddress = Integer.parseInt(txtAddress.getText());
        trackPartConfigBit = Integer.parseInt(selectBit.getItemText(selectBit.getSelectedIndex()));
    }

    @Override
    public void initFromTrackPart(T trackPart) {
        if (trackPart != null && trackPart.getConfiguration() != null) {
            trackPartConfigAddress = trackPart.getConfiguration().getAddress();
            trackPartConfigBit = trackPart.getConfiguration().getOutput();

            //TODO: why?
            setTitle(trackPart.getConfiguration().toString());
        }
    }


}
