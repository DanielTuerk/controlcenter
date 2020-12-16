package net.wbz.moba.controlcenter.web.shared.station;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniel Tuerk
 */
public class StationBoardEntry implements IsSerializable {

    private long scenarioId;
    private String timeText;
    private String train;
    private String station;
    private String platform;
    private String information;

    public StationBoardEntry() {
    }

    public StationBoardEntry(Long scenarioId, String timeText, String train, String station, String platform) {
        this.scenarioId = scenarioId;
        this.timeText = timeText;
        this.train = train;
        this.station = station;
        this.platform = platform;
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
}
