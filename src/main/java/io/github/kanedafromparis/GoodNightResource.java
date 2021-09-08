package io.github.kanedafromparis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/goodnight")
public class GoodNightResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        ReadinessResource.sleeping = Boolean.TRUE;
        return "Good night";

    }
}