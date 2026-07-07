package net.wbz.moba.controlcenter.api.device;


import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.Workspace;
import net.wbz.moba.controlcenter.service.bus.BusService;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import java.util.List;

@Slf4j
@Path("/api/devtool")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class DevToolResource {

    private final BusService busService;
    private final Workspace workspace;

    public DevToolResource(BusService busService, Workspace workspace) {
        this.busService = busService;
        this.workspace = workspace;
    }

    @APIResponseSchema(String.class)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/bus-dump")
    public Response createBusDump() {
        try {
            return Response.ok(busService.dumpBusData()).build();
        } catch (DeviceAccessException e) {
            log.error("can't create bus dump", e);
            return Response.serverError().build();
        }
    }

    @APIResponseSchema(List.class)
    @GET
    @Path("/bus-dump/list")
    public Response listBusDumps() {
        return Response.ok(workspace.listBusDumps()).build();
    }

    @POST
    @Path("/bus-dump/load/{fileName}")
    public Response loadBusDataDump(@PathParam("fileName") String fileName) {
        try {
            busService.importBusDump(workspace.loadBusDump(fileName));
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            log.error("can't load bus dump: {}", fileName, e);
            return Response.serverError().build();
        }
    }

}

