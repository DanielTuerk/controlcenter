package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import java.util.*;

/**
 * Abstract container panel for the specified {@link net.wbz.moba.controlcenter.web.shared.AbstractIdModel}.
 * <p/>
 * It contains an header panel with an collapse function to extend the details container.
 * Implementations can modify the header and show the details.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractItemPanel<Model extends AbstractIdModel, StateEvent extends AbstractStateEvent> extends org.gwtbootstrap3.client.ui.Panel {

    private Model model;

    private PanelCollapse collapseContentPanel;

    private Panel headerPanelContent;
    private PanelBody panelBody;
    private PanelHeader panelHeader;

    private Map<Class<StateEvent>,RemoteEventListener> eventListeners = new HashMap<>();

    public AbstractItemPanel(Model model) {
        this.model = model;

    }

    public void init() {
        for (Class<StateEvent> stateEventClass : getStateEventClasses()) {
            eventListeners.put(stateEventClass, new RemoteEventListener() {
                @Override
                public void apply(Event event) {
                    updateItemData((StateEvent) event);
                }
            });
        }

        collapseContentPanel = createCollapseContentPanel();

        //header
        panelHeader = new PanelHeader();
        headerPanelContent = createHeaderPanel();
        headerPanelContent.addStyleName("abstractItemPanelHeaderContent");
        assert headerPanelContent != null;
        panelHeader.add(headerPanelContent);

        // collapse
        String collapseContainerId = "collapse" + getModel().getId();
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

    abstract protected List<Class<StateEvent>> getStateEventClasses();

    /**
     * TODO Change to update model, or create a new item panel (scenario)
     *
     * @param model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    abstract protected Panel createHeaderPanel();

    abstract public void updateItemData(StateEvent event);

    @Override
    protected void onLoad() {
        super.onLoad();

        add(panelHeader);
        add(panelBody);

        for (Map.Entry<Class<StateEvent>,RemoteEventListener> eventListenerEntry : eventListeners.entrySet()) {
            EventReceiver.getInstance().addListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        for (Map.Entry<Class<StateEvent>,RemoteEventListener> eventListenerEntry : eventListeners.entrySet()) {
            EventReceiver.getInstance().removeListener(eventListenerEntry.getKey(), eventListenerEntry.getValue());
        }

        remove(panelBody);
        remove(panelHeader);
    }

    public Model getModel() {
        return model;
    }

    public abstract PanelCollapse createCollapseContentPanel();
}
