package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class WelcomePage extends HorizontalPanel {

    private TextBox txtCreateName = new TextBox();

    private final ClickHandler createConstructionClickHandler;
    private final ClickHandler loadedConstructionClickHandler;

    public WelcomePage(ClickHandler createConstructionClickHandler, ClickHandler loadedConstructionClickHandler) {
        this.createConstructionClickHandler = createConstructionClickHandler;
        this.loadedConstructionClickHandler = loadedConstructionClickHandler;
    }

    @Override
    protected void onLoad() {
        setSpacing(10);
        setSize("100%", "100%");

        DecoratorPanel createPanel = new DecoratorPanel();
        VerticalPanel createPanelContent = new VerticalPanel();
        createPanelContent.setSpacing(10);
        createPanelContent.add(new Label("Create Construction"));
        createPanelContent.add(txtCreateName);
        createPanelContent.add(new Button("Create", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Construction construction = new Construction();
                final String constructionName = txtCreateName.getText();
                construction.setName(constructionName);
                ServiceUtils.getConstrutionService().createConstruction(construction, new AsyncCallback<Construction>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Construction result) {
                        ServiceUtils.getConstrutionService().setCurrentConstruction(result, new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                            }

                            @Override
                            public void onSuccess(Void result) {
                                Settings.getInstance().getLastUsedConstruction().setValueAndSave(construction.getName());
                                createConstructionClickHandler.onClick(null);
                            }
                        });
                    }
                });
            }
        }));
        createPanel.add(createPanelContent);
        add(createPanel);

        DecoratorPanel loadPanel = new DecoratorPanel();
        final VerticalPanel loadPanelContent = new VerticalPanel();
        loadPanelContent.setSpacing(10);
        loadPanelContent.add(new Label("Load Existing Construction"));

        ServiceUtils.getConstrutionService().loadConstructions(new AsyncCallback<Construction[]>() {
            @Override
            public void onFailure(Throwable caught) {
                loadPanelContent.add(new Label("Error loading constructions"));
            }

            @Override
            public void onSuccess(Construction[] result) {
                for (final Construction construction : result) {
                    loadPanelContent.add(new Button(construction.getName(), new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            ServiceUtils.getConstrutionService().setCurrentConstruction(construction, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                }

                                @Override
                                public void onSuccess(Void result) {
                                    Settings.getInstance().getLastUsedConstruction().setValueAndSave(construction.getName());
                                    loadedConstructionClickHandler.onClick(null);
                                }
                            });

                        }
                    }));
                }
            }
        });
        loadPanel.add(loadPanelContent);
        add(loadPanel);

        setCellWidth(createPanel, "50%");
        setCellHeight(createPanel, "100%");
        setCellWidth(loadPanel, "50%");
        setCellHeight(loadPanel, "100%");
    }
}
