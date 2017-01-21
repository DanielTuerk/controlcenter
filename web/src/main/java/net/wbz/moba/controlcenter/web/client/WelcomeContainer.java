package net.wbz.moba.controlcenter.web.client;

import java.util.Collection;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * Container for the welcome landing page to load an existing {@link Construction} or create a new one.
 *
 * @author Daniel Tuerk
 */
abstract class WelcomeContainer extends Composite {

    @UiField
    TextBox txtCreateName;
    @UiField
    Button btnCreateConstruction;
    @UiField
    LinkedGroup listGroupConstructions;

    interface LoginUiBinder extends UiBinder<Widget, WelcomeContainer> {
    }

    private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class);

    public WelcomeContainer() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Callback to perform an action after the welcome page loaded a {@link Construction}.
     */
    abstract void onCurrentConstructionLoaded();

    @Override
    protected void onLoad() {
        loadConstructions();
    }

    private void loadConstructions() {
        listGroupConstructions.clear();
        RequestUtils.getInstance().getConstructionService().loadConstructions(
                new AsyncCallback<Collection<Construction>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Collection<Construction> result) {
                        for (final Construction construction : result) {
                            Button btnConstructionEntry = new Button();
                            btnConstructionEntry.setText(construction.getName());
                            btnConstructionEntry.addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    updateCurrentConstruction(construction);
                                }
                            });
                            listGroupConstructions.add(btnConstructionEntry);
                        }
                    }
                });
    }

    @UiHandler("btnCreateConstruction")
    void clickCreateConstruction(ClickEvent event) {
        final Construction construction = new Construction();
        final String constructionName = txtCreateName.getText();
        construction.setName(constructionName);
        RequestUtils.getInstance().getConstructionService().createConstruction(construction, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {
                txtCreateName.clear();
                loadConstructions();
                // updateCurrentConstruction(construction);
            }
        });
    }

    /**
     * Set the current construction and store it in the local settings.
     *
     * @param construction {@link Construction} to set
     */
    private void updateCurrentConstruction(final Construction construction) {
        RequestUtils.getInstance().getConstructionService().setCurrentConstruction(
                construction, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Settings.getInstance().getLastUsedConstruction().setValueAndSave(construction.getName());
                        onCurrentConstructionLoaded();
                    }
                });
    }
}
