package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import net.wbz.moba.controlcenter.web.shared.AbstractIdModel;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractItemPanel<Model extends AbstractIdModel> extends FluidContainer {

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
        Row row = new Row();
        Column titleColumn = new Column(2);
        titleColumn.add(new Label(getItemTitle()));
        row.add(titleColumn);

//        HorizontalPanel titlePanel = new HorizontalPanel();

//        titlePanel.add();

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
        row.add(new Column(1, btnCollapse));


//        Column editColumn = new Column(1);
//        editColumn.add(btnCollapse);
//        row.add(editColumn);

        add(row);
//        titlePanel.add(collapseCommands);
//
//        add(titlePanel);

        Row collapseRow = new Row();
        collapseRow.add(new Column(3, collapse.asWidget()));

        add(collapseRow);

    }

    public Model getModel() {
        return model;
    }

    public Panel getCollapseContentPanel() {
        return collapseContentPanel;
    }
}
