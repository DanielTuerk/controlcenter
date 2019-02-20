package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.util.Log;

/**
 * Abstract container for the viewer elements in the track viewer. Load and reload the items in the container.
 *
 * @author Daniel Tuerk
 */
public abstract class AbstractViewerContainer extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    FlowPanel itemsContainer;
    private final List<Widget> lastWidgets = new ArrayList<>();

    public AbstractViewerContainer() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void addItemPanel(BaseViewerItemPanel<?> panel) {
        itemsContainer.add(panel);
        lastWidgets.add(panel);
    }

    abstract protected void loadItems();

    protected void reloadItems() {
        resetItems();
        loadItems();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadItems();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        resetItems();
    }

    private void resetItems() {
        lastWidgets.forEach(Widget::removeFromParent);
        lastWidgets.clear();
    }

    interface Binder extends UiBinder<Widget, AbstractViewerContainer> {

    }
}
