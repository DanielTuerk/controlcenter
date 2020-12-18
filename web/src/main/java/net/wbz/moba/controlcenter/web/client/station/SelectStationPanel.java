package net.wbz.moba.controlcenter.web.client.station;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.wbz.moba.controlcenter.web.client.components.StationMultiSelect;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * <p>Panel to select a {@link Station} and submit.</p>
 * <p>State change is propagated to the added consumers {@link #addOpenStationConsumer(Consumer)}.</p>
 *
 * @author Daniel Tuerk
 */
public class SelectStationPanel extends Composite {

    private static final Binder UI_BINDER = GWT.create(Binder.class);
    private final List<Consumer<List<Station>>> consumers = new ArrayList<>();
    @UiField
    StationMultiSelect stationMultiSelect;

    public SelectStationPanel() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    void addOpenStationConsumer(Consumer<List<Station>> consumer) {
        consumers.add(consumer);
    }

    @UiHandler("btnOpenSingle")
    void onClick(ClickEvent ignored) {
        consumers.forEach(consumer -> consumer.accept(stationMultiSelect.getSelected()));
    }

    interface Binder extends UiBinder<Widget, SelectStationPanel> {

    }
}
