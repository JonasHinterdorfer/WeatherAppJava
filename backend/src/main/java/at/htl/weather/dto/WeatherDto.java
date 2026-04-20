package at.htl.weather.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Weather details for a city")
public class WeatherDto {

    @Schema(description = "City ID", example = "1")
    public Long cityId;

    @Schema(description = "City name", example = "Berlin")
    public String cityName;

    @Schema(description = "Current temperature in degrees Celsius", example = "12.7")
    public double temperature;

    @Schema(description = "Simple weather label based on the temperature", example = "Cool")
    public String description;

    public WeatherDto() {
    }

    public WeatherDto(Long cityId, String cityName, double temperature, String description) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.temperature = temperature;
        this.description = description;
    }
}

