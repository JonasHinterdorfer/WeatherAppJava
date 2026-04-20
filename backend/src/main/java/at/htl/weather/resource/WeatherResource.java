package at.htl.weather.resource;

import at.htl.weather.dto.CityDto;
import at.htl.weather.dto.ErrorDto;
import at.htl.weather.dto.WeatherDto;
import at.htl.weather.service.WeatherService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherResource {

    private final WeatherService weatherService;

    public WeatherResource(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GET
    @Path("/cities")
    @Operation(summary = "Returns all stored cities")
    @APIResponse(
            responseCode = "200",
            description = "List of all cities",
            content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = CityDto.class))
    )
    public List<CityDto> getCities() {
        return weatherService.getAllCities();
    }

    @GET
    @Path("/weather/{cityId}")
    @Operation(summary = "Returns weather details for a city")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Weather data for the requested city", content = @Content(schema = @Schema(implementation = WeatherDto.class))),
            @APIResponse(responseCode = "404", description = "City was not found", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @APIResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public Response getWeather(@PathParam("cityId") Long cityId) {
        return weatherService.getWeatherByCityId(cityId)
                .map(weatherDto -> Response.ok(weatherDto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorDto("City with id " + cityId + " was not found"))
                        .build());
    }
}

