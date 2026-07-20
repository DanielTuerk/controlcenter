package io.github.danieltuerk.controlcenter.api.device;


import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.Workspace;
import io.github.danieltuerk.controlcenter.service.bus.BusService;
import io.github.danieltuerk.controlcenter.shared.EventCache;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
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
    private final EventCache eventCache;
    private final EventBroadcaster eventBroadcaster;

    public DevToolResource(BusService busService, Workspace workspace, EventCache eventCache,
                           EventBroadcaster eventBroadcaster) {
        this.busService = busService;
        this.workspace = workspace;
        this.eventCache = eventCache;
        this.eventBroadcaster = eventBroadcaster;
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

    @APIResponseSchema(String.class)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/event-dump")
    public Response createEventDump() {
        return Response.ok(workspace.createEventDump(eventCache.getCachedEvents())).build();
    }

    @APIResponseSchema(List.class)
    @GET
    @Path("/event-dump/list")
    public Response listEventDumps() {
        return Response.ok(workspace.listEventDumps()).build();
    }

    @POST
    @Path("/event-dump/load/{fileName}")
    public Response loadEventDump(@PathParam("fileName") String fileName) {
        workspace.loadEventDump(fileName).entries()
                .forEach(entry ->
                        entry.events().values().forEach(eventBroadcaster::fireEvent));
        return Response.ok().build();
    }

}

