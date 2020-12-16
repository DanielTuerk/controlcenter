package net.wbz.moba.controlcenter.web.shared.station;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_STATIONS_EDITOR)
public interface StationEditorService extends RemoteService {

    Collection<Station> getStations();

    void createStation(Station station);

    void updateStation(Station station);

    void deleteStation(long stationId);

}
