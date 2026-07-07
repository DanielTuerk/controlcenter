package net.wbz.moba.controlcenter.api.device;


import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.bus.BusService;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

@Slf4j
@Path("/api/devtool")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class DevToolResource {

    private final BusService busService;

    public DevToolResource(BusService busService) {
        this.busService = busService;
    }

    @APIResponseSchema(String.class)
    @GET
    @Path("/bus-dump")
    public Response createBusDump() {
        try {
            return Response.ok(busService.dumpBusData()).build();
        } catch (DeviceAccessException e) {
            log.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

}

