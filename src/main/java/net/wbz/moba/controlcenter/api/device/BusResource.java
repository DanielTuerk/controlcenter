package net.wbz.moba.controlcenter.api.device;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.wbz.moba.controlcenter.service.bus.BusService;

@Path("/api/bus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    // TODO recorder
    // TODO player

}

