package net.wbz.moba.controlcenter.api.device;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import net.wbz.moba.controlcenter.service.bus.DeviceManager;
import net.wbz.moba.controlcenter.service.bus.DeviceService;
import net.wbz.moba.controlcenter.shared.bus.DeviceInfo;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.jboss.logging.Logger;

@Path("/api/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceResource {

    private static final Logger LOGGER = Logger.getLogger(DeviceService.class);

    @Inject
    DeviceManager deviceManager;
    @Inject
    DeviceService deviceService;

    @GET
    public List<DeviceInfo> listAll() {
        return deviceManager.load();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var byId = deviceManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @Transactional
    public Response create(DeviceInfo created) {
        var deviceInfo = deviceManager.create(created);
        return Response.status(Response.Status.CREATED)
            .entity(deviceInfo)
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, DeviceInfo updated) {
        if (!deviceManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        deviceManager.update(id, updated);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = deviceManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{id}/connect")
    public Response create(@PathParam("id") Long id) {
        var byId = deviceManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        deviceService.changeDevice(byId.get());
        try {
            deviceService.connect();
            return Response.ok().build();
        } catch (DeviceAccessException e) {
            var message = "can't connect active device: %d".formatted(id);
            LOGGER.error(message, e);
            return Response.status(500, message).build();
        }
    }

    @POST
    @Path("/disconnect")
    public Response create() {
        deviceService.disconnect();
        return Response.ok().build();
    }

}

