package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.PanelCollapse;


/**
 * Container panel for the specified {@link Model}. It contains an header panel with an collapse function to extend the
 * details container. Implementations can modify the header and show the details.
 *
 * @author Daniel Tuerk
 */
public class BaseViewerItemPanel<Model extends AbstractDto> extends Composite {

    private static BaseViewerItemPanel.Binder uiBinder = GWT.create(BaseViewerItemPanel.Binder.class);
    private final Model model;
    @UiField
    Label lblName;
    @UiField
    Label lblState;
    @UiField
    Label lblStateDetails;
    @UiField
    PanelCollapse contentCollapse;
    @UiField
    Button btnCollapse;

    public BaseViewerItemPanel(AbstractViewerItemControlsComposite<Model> widget) {
        this.model = widget.getModel();
        initWidget(uiBinder.createAndBindUi(this));

        btnCollapse.setDataTargetWidget(contentCollapse);
        contentCollapse.add(widget);
    }

    public Model getModel() {
        return model;
    }

    protected Label getLblName() {
        return lblName;
    }

    protected Label getLblState() {
        return lblState;
    }

    protected Label getLblStateDetails() {
        return lblStateDetails;
    }

    interface Binder extends UiBinder<Widget, BaseViewerItemPanel> {

    }
}
