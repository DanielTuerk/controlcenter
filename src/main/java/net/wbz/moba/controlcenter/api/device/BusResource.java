package net.wbz.moba.controlcenter.api.device;


import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.bus.BusService;
import net.wbz.selectrix4java.device.Device.SYSTEM_FORMAT;
import net.wbz.selectrix4java.device.DeviceAccessException;

@Slf4j
@Path("/api/bus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class BusResource {

    @Inject
    BusService busService;

    @GET
    @Path("/railvoltage")
    public boolean fetchRailvoltageState() {
        return busService.getRailVoltage();
    }

    @POST
    @Path("/railvoltage")
    public Response updateRailVoltage() {
        busService.toggleRailVoltage();
        return Response.ok().build();
    }

    @GET
    @Path("/system-format")
    public SYSTEM_FORMAT fetchSystemFormatState() {
        return busService.getSystemFormat();
    }

    @POST
    @Path("/system-format")
    public Response switchSystemFormat() {
        busService.switchSystemFormat();
        return Response.ok().build();
    }

    @POST
    @Path("/bus-data")
    public Response busData(BusDataDto busData) {
        busService.sendBusData(busData.bus(), busData.address(), busData.value());
        return Response.ok().build();
    }

    @POST
    @Path("/bus-bit")
    public Response busData(BusBitDto busBit) {
        busService.sendBusData(busBit.bus(), busBit.address(), busBit.bit(), busBit.state());
        return Response.ok().build();
    }


    @POST
    @Path("/start-tracking-bus")
    public Response startTrackingBus(WsClient wsClient) {
        try {
            busService.startTrackingBus(wsClient.clientId());
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            log.error("Failed to start tracking bus", e);
            return Response.status(Status.UNAVAILABLE_FOR_LEGAL_REASONS).build();
        }
    }

    @POST
    @Path("/stop-tracking-bus")
    public Response stopTrackingBus(WsClient wsClient) {
        try {
            busService.stopTrackingBus(wsClient.clientId());
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            log.error("Failed to stop tracking bus", e);
            return Response.status(Status.UNAVAILABLE_FOR_LEGAL_REASONS).build();
        }
    }

    // TODO recorder
    // TODO player

}

