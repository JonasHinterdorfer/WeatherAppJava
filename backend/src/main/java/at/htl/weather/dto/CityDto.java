package at.htl.weather.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "City information used in the city list")
public class CityDto {

    @Schema(description = "Unique city ID", example = "1")
    public Long id;

    @Schema(description = "City name", example = "Berlin")
    public String name;

    @Schema(description = "Country code", example = "DE")
    public String country;

    public CityDto() {
    }

    public CityDto(Long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }
}

