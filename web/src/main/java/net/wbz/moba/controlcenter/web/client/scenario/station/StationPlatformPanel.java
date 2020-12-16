package net.wbz.moba.controlcenter.web.client.scenario.station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.components.TrackBlockMultiSelect;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
abstract class StationPlatformPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);
    private final StationPlatform stationPlatform;

    @UiField
    TextBox txtName;
    @UiField
    TrackBlockMultiSelect trackBlockSelect;

    StationPlatformPanel(StationPlatform stationPlatform) {
        this.stationPlatform = stationPlatform;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        txtName.setText(stationPlatform.getName());
        trackBlockSelect.setSelected(stationPlatform.getTrackBlocks());
    }


    @UiHandler("btnDelete")
    void onClickDelete(ClickEvent ignored) {
        delete();
    }

    protected abstract void delete();

    void applyChanges() {
        stationPlatform.setName(txtName.getText());
        stationPlatform.setTrackBlocks(trackBlockSelect.getSelected());
    }

    interface Binder extends UiBinder<Widget, StationPlatformPanel> {

    }
}
