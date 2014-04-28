package net.wbz.moba.controlcenter.web.client.model.track;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.device.BusConnection;
import net.wbz.moba.controlcenter.web.client.device.BusConnectionListener;
import net.wbz.moba.controlcenter.web.client.editor.track.ClickActionViewerWidgetHandler;
import net.wbz.moba.controlcenter.web.client.editor.track.EditTrackWidgetHandler;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;
import net.wbz.moba.controlcenter.web.shared.track.model.Straight;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;
import sun.org.mozilla.javascript.internal.ast.Block;

/**
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractBlockImageTrackWidget<T extends TrackPart> extends AbstractImageTrackWidget<T>
        implements EditTrackWidgetHandler, BlockPart {

    private final ListBox selectBit = new ListBox();
    private final TextBox txtAdress = new TextBox();

    private int trackPartConfigAdress = -1;
    private int trackPartConfigBit = -1;

    private boolean trackPartState = true;

    public AbstractBlockImageTrackWidget() {
    }

    @Override
    public void freeBlock() {
        removeStyleName("unnknownBlock");
        removeStyleName("usedBlock");
        addStyleName("freeBlock");
    }

    @Override
    public void unknownBlock() {
        addStyleName("unnknownBlock");
        removeStyleName("usedBlock");
        removeStyleName("freeBlock");
    }

    @Override
    public void usedBlock() {
        removeStyleName("unnknownBlock");
        addStyleName("usedBlock");
        removeStyleName("freeBlock");
    }


    public Configuration getStoredWidgetConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setAddress(trackPartConfigAdress);
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

        if (trackPartConfigAdress >= 0) {
            txtAdress.setText(String.valueOf(trackPartConfigAdress));
        }

        for (int index = 0; index < 8; index++) {
            selectBit.addItem(String.valueOf(index + 1));
            if (index + 1 == trackPartConfigBit) {
                selectBit.setSelectedIndex(index);
            }
        }

        // Add some standard form options
        layout.setHTML(1, 0, "Adress:");
        layout.setWidget(1, 1, txtAdress);
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
        trackPartConfigAdress = Integer.parseInt(txtAdress.getText());
        trackPartConfigBit = Integer.parseInt(selectBit.getItemText(selectBit.getSelectedIndex()));
    }

    @Override
    public void initFromTrackPart(T trackPart) {
        if (trackPart != null && trackPart.getConfiguration() != null) {
            trackPartConfigAdress = trackPart.getConfiguration().getAddress();
            trackPartConfigBit = trackPart.getConfiguration().getOutput();

            //
            setTitle(trackPart.getConfiguration().toString());
        }
    }


}
