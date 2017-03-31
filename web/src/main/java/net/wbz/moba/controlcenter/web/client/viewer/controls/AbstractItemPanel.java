package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * Abstract container panel for the specified {@link net.wbz.moba.controlcenter.web.shared.AbstractIdModel}.
 * <p/>
 * It contains an header panel with an collapse function to extend the details container.
 * Implementations can modify the header and show the details.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractItemPanel<Model extends AbstractDto, StateEvent extends AbstractStateEvent>
        extends org.gwtbootstrap3.client.ui.Panel {

    private Model model;

    private PanelCollapse collapseContentPanel;

    private Panel headerPanelContent;
    private PanelBody panelBody;
    private PanelHeader panelHeader;
    private final RemoteEventListener deviceInfoEventListener;

    private Label lblName;
    private Label lblState;
    private Label lblStateDetails;

    public AbstractItemPanel(Model model, String title) {
        this.model = model;

        // add event receiver for the device connection state
        deviceInfoEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                        deviceConnectionChanged(true);
                    } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                        deviceConnectionChanged(false);
                    }
                }
            }
        };

        lblName = new Label(title);
        lblState = new Label();
        lblStateDetails = new Label();
    }

    protected abstract void deviceConnectionChanged(boolean connected);

    public void init() {
        collapseContentPanel = createCollapseContentPanel();

        // header
        panelHeader = new PanelHeader();
        headerPanelContent = createHeaderPanel();
        headerPanelContent.addStyleName("abstractItemPanelHeaderContent");
        assert headerPanelContent != null;
        panelHeader.add(headerPanelContent);

        // collapse
        String collapseContainerId = "collapse" + this.getClass().getSimpleName() + getModel().getId();
        Button btnCollapse = new Button(">>");
        panelHeader.add(btnCollapse);
        btnCollapse.setDataTarget("#" + collapseContainerId);
        btnCollapse.setDataToggle(Toggle.COLLAPSE);

        // details
        panelBody = new PanelBody();
        panelBody.getElement().getStyle().setPadding(0, Style.Unit.PX);
        collapseContentPanel = createCollapseContentPanel();
        collapseContentPanel.setId(collapseContainerId);
        panelBody.add(collapseContentPanel);

    }

    protected Panel createHeaderPanel() {
        Panel headerPanel = new FlowPanel();
        lblName.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        headerPanel.add(lblName);
        headerPanel.add(lblState);
        headerPanel.add(lblStateDetails);
        return headerPanel;
    }

    abstract public void updateItemData(StateEvent event);

    @Override
    protected void onLoad() {
        super.onLoad();
        add(panelHeader);
        add(panelBody);

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceInfoEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        remove(panelBody);
        remove(panelHeader);

        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceInfoEventListener);
    }

    public Model getModel() {
        return model;
    }

    /**
     * TODO Change to update model, or create a new item panel (scenario)
     *
     * @param model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    public abstract PanelCollapse createCollapseContentPanel();

    public Label getLblName() {
        return lblName;
    }

    public Label getLblState() {
        return lblState;
    }

    public Label getLblStateDetails() {
        return lblStateDetails;
    }
}
