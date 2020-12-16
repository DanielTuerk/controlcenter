package net.wbz.moba.controlcenter.web.client.scenario.station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.shared.station.Station;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk
 */
public class StationEditModalBody extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Station station;
    private final List<StationPlatformPanel> foos = new ArrayList<>();
    @UiField
    TextBox txtName;
    @UiField
    Panel platformsPanel;

    StationEditModalBody(Station station) {
        this.station = station;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        txtName.setText(station.getName());
        initPlatforms();
    }

    private void initPlatforms() {
        platformsPanel.clear();
        foos.clear();

        for (StationPlatform platform : station.getPlatforms()) {
            platformsPanel.add(createPlatformPanel(platform));
        }
    }

    private StationPlatformPanel createPlatformPanel(StationPlatform platform) {
        StationPlatformPanel stationPlatformPanel = new StationPlatformPanel(platform) {

            @Override
            protected void delete() {
                station.getPlatforms().remove(platform);
                initPlatforms();
            }
        };
        foos.add(stationPlatformPanel);
        return stationPlatformPanel;
    }

    @UiHandler("btnAddPlatform")
    void onClickAddPlatform(ClickEvent ignored) {
        StationPlatform platform = new StationPlatform();
        station.getPlatforms().add(platform);
        platformsPanel.add(createPlatformPanel(platform));
    }

    Station getUpdatedModel() {
        station.setName(txtName.getText());
        foos.forEach(StationPlatformPanel::applyChanges);
        return station;
    }

    interface Binder extends UiBinder<Widget, StationEditModalBody> {

    }
}
