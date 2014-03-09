package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
abstract public class AbstractItemPanel<Model extends AbstractIdModel> extends VerticalPanel {

    public static final String BREAK_LINE = "&#13;"; //13 - mac?
    private static final int MIN_LINES_EDIT_TEXTAREA = 5;
    private Model model;

    private final Panel collapseContentPanel = new SimplePanel();

    //edit
    private Panel commandListEditPanel;
    private TextArea txtCommandList;

    public AbstractItemPanel(Model model) {
        this.model = model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    abstract protected String getItemTitle();

    @Override
    protected void onLoad() {
        super.onLoad();
        HorizontalPanel titlePanel = new HorizontalPanel();

        titlePanel.add(new com.github.gwtbootstrap.client.ui.Label(getItemTitle()));

        CollapseTrigger collapseCommands = new CollapseTrigger("commandsContainer");
        ToggleButton btnCollapse = new ToggleButton(">>");

        collapseCommands.add(btnCollapse);
        final Collapse collapse = new Collapse();
        collapse.setWidget(collapseContentPanel);

        btnCollapse.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                collapse.toggle();
            }
        });

        titlePanel.add(collapseCommands);

        add(titlePanel);


        add(collapse);

    }

    public Model getModel() {
        return model;
    }

    public Panel getCollapseContentPanel() {
        return collapseContentPanel;
    }
}
