package net.wbz.moba.controlcenter.web.client.viewer.controls.scenario;

import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.viewer.controls.AbstractItemPanel;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioCommand;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import java.util.List;

/**
 * TODO: it's broken and need to refactor
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class ScenarioItemPanel extends AbstractItemPanel<Scenario, ScenarioStateEvent> {

    public static final String BREAK_LINE = "&#13;"; //13 - mac?
    private static final int MIN_LINES_EDIT_TEXTAREA = 5;


    private ToggleSwitch btnToggleRepeatMode;

    //edit
    private PanelCollapse commandListEditPanel;
    private TextArea txtCommandList;
    private Button btnPlay;
    private Button btnStop;
    private Button btnPause;
    private Button btnEditScenario;

    public ScenarioItemPanel(Scenario scenario) {
        super(scenario);
    }

    @Override
    protected Panel createHeaderPanel() {
        Panel headerPanel = new FlowPanel();
        headerPanel.add(new Label(getModel().getName() + " [" + getModel().getId() + "]"));
        return headerPanel;
    }

    @Override
    public PanelCollapse createCollapseContentPanel() {
        btnPlay = new Button("play");
        btnStop = new Button("stop");
        btnPause = new Button("pause");
        btnEditScenario = new Button("edit");
        btnEditScenario.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                StringBuilder sb = new StringBuilder();
                for (ScenarioCommand command : getModel().getCommands()) {
                    sb.append(command.toText());
                    sb.append(BREAK_LINE);
                }
                showEditScenario(sb.toString());
            }
        });


        HorizontalPanel btnControlGroupPanel = new HorizontalPanel();
        btnPlay.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioService().start(getModel().getId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        //TODO
                    }
                });
            }
        });
        btnControlGroupPanel.add(btnPlay);
        btnPause.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioService().pause(getModel().getId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        //TODO
                    }
                });
            }
        });
        btnControlGroupPanel.add(btnPause);
        btnStop.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioService().stop(getModel().getId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(Void result) {
                        //TODO
                    }
                });
            }
        });
        btnControlGroupPanel.add(btnStop);

        btnToggleRepeatMode = new ToggleSwitch();
        btnToggleRepeatMode.setLabelText("repeat");
        btnToggleRepeatMode.setValue(getModel().getMode() == Scenario.MODE.REPEAT);
        btnToggleRepeatMode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                ServiceUtils.getScenarioEditorService().updateScenarioRunMode(getModel().getId(),
                        event.getValue() ? Scenario.MODE.REPEAT : Scenario.MODE.SINGLE,
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                //TODO
                            }

                            @Override
                            public void onSuccess(Void result) {
                                //TODO
                            }
                        }
                );
            }
        });
        btnControlGroupPanel.add(btnToggleRepeatMode);
        add(btnControlGroupPanel);

        initEditPanel();
        showCommandList(getModel().getCommands());

        return commandListEditPanel;
    }


    private void showCommandList(List<ScenarioCommand> commands) {
        commandListEditPanel.clear();
        FlowPanel commandListPanel = new FlowPanel();
        commandListPanel.add(btnEditScenario);
        for (ScenarioCommand scenarioCommand : commands) {
            commandListPanel.add(new Label(scenarioCommand.toText()));
        }
        commandListEditPanel.add(commandListPanel);
    }

    private void initEditPanel() {
        commandListEditPanel = new PanelCollapse();
        txtCommandList = new TextArea();

        int showLines = MIN_LINES_EDIT_TEXTAREA;
        if (getModel().getCommands().size() > MIN_LINES_EDIT_TEXTAREA) {
            showLines = getModel().getCommands().size();
        }
        txtCommandList.setVisibleLines(showLines);

        commandListEditPanel.add(txtCommandList);
        Button btnSave = new Button("save");
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String[] commands = txtCommandList.getText().split("\n");
                ServiceUtils.getScenarioEditorService().updateScenarioCommands(getModel().getId(), commands,
                        new AsyncCallback<Scenario>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                //TODO
                            }

                            @Override
                            public void onSuccess(Scenario result) {
                                setModel(result);
                                showCommandList(result.getCommands());
                            }
                        }
                );
            }
        });
        commandListEditPanel.add(btnSave);
        Button btnCancel = new Button("cancel");
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showCommandList(getModel().getCommands());
            }
        });
        btnCancel.setType(ButtonType.WARNING);
        commandListEditPanel.add(btnCancel);
    }

    private void showEditScenario(String commandsText) {
        createCollapseContentPanel().clear();
        txtCommandList.setValue(new HTML(commandsText).getHTML());
        createCollapseContentPanel().add(commandListEditPanel);
    }

    @Override
    public void updateItemData(ScenarioStateEvent event) {
        switch (event.getState()) {
            case RUNNING:
                btnPlay.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
                break;
            case IDLE:
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
                break;
            case PAUSED:
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(true);
                break;
        }
    }
}
