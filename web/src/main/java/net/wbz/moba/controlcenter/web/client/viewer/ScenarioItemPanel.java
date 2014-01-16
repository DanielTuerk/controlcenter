package net.wbz.moba.controlcenter.web.client.viewer;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioCommand;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ScenarioItemPanel extends DockPanel {

    public static final String BREAK_LINE = "&#13;"; //13 - mac?
    private Scenario scenario;

    private final Button btnPlay = new Button("play");
    private final Button btnStop = new Button("stop");
    private final Button btnPause = new Button("pause");
    private final ToggleButton btnToggleRepeatMode = new ToggleButton("repeat");
    private final Panel contentPanel = new SimplePanel();

    //edit
    private Panel commandListEditPanel;
    private TextArea txtCommandList;

    public ScenarioItemPanel(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        HorizontalPanel titlePanel = new HorizontalPanel();
        titlePanel.add(new Label(scenario.getName()));
        Button btnEditScenario = new Button("edit");
        btnEditScenario.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                StringBuilder sb = new StringBuilder();
                for (ScenarioCommand command : scenario.getCommands()) {
                    sb.append(command.toText());
                    sb.append(BREAK_LINE);
                }
                showEditScenario(sb.toString());
            }
        });
        titlePanel.add(btnEditScenario);
        add(titlePanel, DockPanel.NORTH);

        HorizontalPanel btnControlGroupPanel = new HorizontalPanel();

        btnPlay.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ServiceUtils.getScenarioService().start(scenario.getId(), new AsyncCallback<Void>() {
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
                ServiceUtils.getScenarioService().pause(scenario.getId(), new AsyncCallback<Void>() {
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
                ServiceUtils.getScenarioService().stop(scenario.getId(), new AsyncCallback<Void>() {
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

        btnToggleRepeatMode.setValue(scenario.getMode() == Scenario.MODE.REPEAT);
        btnToggleRepeatMode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                ServiceUtils.getScenarioEditorService().updateScenarioRunMode(scenario.getId(),
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

        updateScenarioRunState(scenario.getRunState());

        add(btnControlGroupPanel, DockPanel.SOUTH);

        initEditPanel();
        showCommandList(scenario.getCommands());

        add(contentPanel, DockPanel.CENTER);
    }

    private void showCommandList(List<ScenarioCommand> commands) {
        contentPanel.clear();
        VerticalPanel commandListPanel = new VerticalPanel();
        for (ScenarioCommand scenarioCommand : commands) {
            commandListPanel.add(new Label(scenarioCommand.toText()));
        }
        contentPanel.add(commandListPanel);
    }

    private void initEditPanel() {
        commandListEditPanel = new HorizontalPanel();
        txtCommandList = new TextArea();
        commandListEditPanel.add(txtCommandList);
        Button btnSave = new Button("save");
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String[] commands = txtCommandList.getText().split("\n");
                ServiceUtils.getScenarioEditorService().updateScenarioCommands(scenario.getId(), commands,
                        new AsyncCallback<Scenario>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                //TODO
                            }

                            @Override
                            public void onSuccess(Scenario result) {
                                scenario = result;
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
                showCommandList(scenario.getCommands());
            }
        });
        btnCancel.setType(ButtonType.INVERSE);
        commandListEditPanel.add(btnCancel);
    }

    private void showEditScenario(String commandsText) {
        contentPanel.clear();
        txtCommandList.setValue(new HTML(commandsText).getHTML());
        contentPanel.add(commandListEditPanel);
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void updateScenarioRunState(Scenario.RUN_STATE runState) {
        switch (runState) {
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
