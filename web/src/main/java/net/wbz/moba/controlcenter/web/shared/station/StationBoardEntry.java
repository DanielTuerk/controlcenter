package net.wbz.moba.controlcenter.web.shared.station;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public class StationBoardEntry {

    private long scenarioId;
    private String timeText;
    private String train;
    private String station;
    private String platform;
    private String information;
    private long createdTimestamp;
    private List<String> viaStations;

    public StationBoardEntry() {
    }

    public StationBoardEntry(Long scenarioId, String timeText, String train, String station, String platform,
        List<String> viaStations) {
        this.scenarioId = scenarioId;
        this.timeText = timeText;
        this.train = train;
        this.station = station;
        this.platform = platform;
        this.viaStations = viaStations;
        this.createdTimestamp = System.currentTimeMillis();
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public String getTimeText() {
        return timeText;
    }


    public String getTrain() {
        return train;
    }


    public String getStation() {
        return station;
    }


    public String getPlatform() {
        return platform;
    }


    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public List<String> getViaStations() {
        if (viaStations == null) {
            viaStations = new ArrayList<>();
        }
        return viaStations;
    }
}
