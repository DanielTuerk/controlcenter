package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import net.wbz.moba.controlcenter.web.shared.BusService;
import net.wbz.moba.controlcenter.web.shared.BusServiceAsync;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstrutionService;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstrutionServiceAsync;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioService;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorServiceAsync;
import net.wbz.moba.controlcenter.web.shared.train.TrainService;
import net.wbz.moba.controlcenter.web.shared.train.TrainServiceAsync;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerService;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackViewerServiceAsync;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ServiceUtils {

    private static final ScenarioEditorServiceAsync scenarioEditorService = GWT.create(ScenarioEditorService.class);

    private static final BusServiceAsync busService = GWT.create(BusService.class);

    private static final TrackViewerServiceAsync trackViewerService = GWT.create(TrackViewerService.class);
    private static final TrackEditorServiceAsync trackEditorService = GWT.create(TrackEditorService.class);

    private static final ConstrutionServiceAsync construtionService = GWT.create(ConstrutionService.class);
    private static final ScenarioServiceAsync scenarioService = GWT.create(ScenarioService.class);

    private static final TrainEditorServiceAsync trainEditorService = GWT.create(TrainEditorService.class);
    private static final TrainServiceAsync trainService = GWT.create(TrainService.class);

    public static ConstrutionServiceAsync getConstrutionService() {
        return construtionService;
    }

    public static TrackEditorServiceAsync getTrackEditorService() {
        return trackEditorService;
    }

    public static TrackViewerServiceAsync getTrackViewerService() {
        return trackViewerService;
    }

    public static BusServiceAsync getBusService() {
        return busService;
    }

    public static ScenarioEditorServiceAsync getScenarioEditorService() {
        return scenarioEditorService;
    }

    public static ScenarioServiceAsync getScenarioService() {
        return scenarioService;
    }

    public static TrainEditorServiceAsync getTrainEditorService() {
        return trainEditorService;
    }

    public static TrainServiceAsync getTrainService() {
        return trainService;
    }
}
