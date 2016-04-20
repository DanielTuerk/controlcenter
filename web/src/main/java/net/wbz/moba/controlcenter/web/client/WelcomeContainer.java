package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionProxy;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionRequest;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.TextBox;

import java.util.List;

/**
 * Container for the welcome landing page to load an existing {@link ConstructionProxy} or create a new one.
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
     * Callback to perform an action after the welcome page loaded a {@link ConstructionProxy}.
     */
    abstract void onCurrentConstructionLoaded();

    @Override
    protected void onLoad() {
        RequestUtils.getInstance().getConstructionRequest().loadConstructions().fire(
                new Receiver<List<ConstructionProxy>>() {
                    @Override
                    public void onSuccess(List<ConstructionProxy> response) {
                        for (final ConstructionProxy construction : response) {
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
                }
        );
    }

    @UiHandler("btnCreateConstruction")
    void clickCreateConstruction(ClickEvent event) {
        ConstructionRequest constructionService = RequestUtils.getInstance().getConstructionRequest();
        final ConstructionProxy construction = constructionService.create(ConstructionProxy.class);
        final String constructionName = txtCreateName.getText();
        construction.setName(constructionName);
        constructionService.createConstruction(construction).fire(new Receiver<Void>() {
            @Override
            public void onSuccess(Void response) {
                updateCurrentConstruction(construction);
            }
        });
    }

    /**
     * Set the current construction and store it in the local settings.
     *
     * @param construction {@link ConstructionProxy} to set
     */
    private void updateCurrentConstruction(final ConstructionProxy construction) {
        RequestUtils.getInstance().getConstructionRequest().setCurrentConstruction(
                construction).fire(new Receiver<Void>() {
            @Override
            public void onSuccess(Void response) {
                Settings.getInstance().getLastUsedConstruction().setValueAndSave(
                        construction.getName());
                onCurrentConstructionLoaded();
            }
        });
    }
}
