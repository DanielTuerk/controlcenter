package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;
import net.wbz.moba.controlcenter.web.shared.AbstractStateEvent;

import java.util.List;

/**
 * Abstract container panel for the specified {@link net.wbz.moba.controlcenter.web.shared.AbstractIdModel}.
 * <p/>
 * It contains an header panel with an collapse function to extend the details container.
 * Implementations can modify the header and show the details.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractItemPanel<Model extends AbstractIdModel, StateEvent extends AbstractStateEvent> extends FlowPanel {

    private Model model;

    private Panel collapseContentPanel;

    private Panel headerPanelContent;

    public AbstractItemPanel(Model model) {
        this.model = model;
        for (Class<StateEvent> stateEventClass : getStateEventClasses()) {
            EventReceiver.getInstance().addListener(stateEventClass, new RemoteEventListener() {
                @Override
                public void apply(Event event) {
                    updateItemData((StateEvent) event);
                }
            });
        }
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

        collapseContentPanel = createCollapseContentPanel();

        //header
        FlowPanel headerPanel = new FlowPanel();
        headerPanelContent = createHeaderPanel();
        headerPanelContent.addStyleName("abstractItemPanelHeaderContent");
        assert headerPanelContent != null;

        headerPanel.add(headerPanelContent);

        // collapse
        CollapseTrigger collapseCommands = new CollapseTrigger("commandsContainer");

        net.wbz.moba.controlcenter.web.client.widgets.ToggleButton btnCollapse = new net.wbz.moba.controlcenter.web.client.widgets.ToggleButton(">>");
        btnCollapse.setPixelSize(40, 40);
        collapseCommands.add(btnCollapse);
        headerPanel.add(btnCollapse);

        add(headerPanel);

        // details
        final Collapse detailsPanel = new Collapse();
        detailsPanel.setWidget(collapseContentPanel);

        btnCollapse.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                detailsPanel.toggle();
            }
        });

        add(detailsPanel);
    }

    public Model getModel() {
        return model;
    }

    public abstract Panel createCollapseContentPanel();
}
