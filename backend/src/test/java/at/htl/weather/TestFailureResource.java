package at.htl.weather;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test/fail")
public class TestFailureResource {

    @GET
    public String fail() {
        throw new IllegalStateException("Simulated test exception");
    }
}

