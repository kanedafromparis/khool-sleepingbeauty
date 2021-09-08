package io.github.kanedafromparis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ready")
public class ReadinessResource {

    public static Boolean sleeping = Boolean.FALSE;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ready() {
        if(!sleeping){
            return "OK";
        }else{
            throw new UnsupportedOperationException("not ready");
        }
    }
}