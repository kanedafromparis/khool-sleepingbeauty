package io.github.kanedafromparis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/wakeup")
public class WakeUpResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        ReadinessResource.sleeping = Boolean.FALSE;
        return "yawn...";
    }
}