package net.wbz.moba.controlcenter.web.shared.station;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.Collection;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * Service for the arrival and departure board of {@link Station}s.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_STATIONS_BOARD)
public interface StationsBoardService extends RemoteService {

    Collection<StationBoardEntry> loadArrivalBoard(long stationId);

    Collection<StationBoardEntry> loadDepartureBoard(long stationId);
}
