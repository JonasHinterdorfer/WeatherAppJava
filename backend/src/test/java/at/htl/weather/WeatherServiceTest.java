package at.htl.weather;

import at.htl.weather.dto.CityDto;
import at.htl.weather.dto.WeatherDto;
import at.htl.weather.entity.City;
import at.htl.weather.repository.CityRepository;
import at.htl.weather.service.WeatherService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class WeatherServiceTest {

    private static final Set<String> DESCRIPTIONS = Set.of("Cold", "Cool", "Pleasant", "Warm");

    @Inject
    WeatherService weatherService;

    @Inject
    CityRepository cityRepository;

    @Test
    void getAllCitiesReturnsSeededCitiesAsDtos() {
        List<CityDto> cities = weatherService.getAllCities();

        assertNotNull(cities);
        assertTrue(cities.size() >= 3);
        assertTrue(cities.stream().anyMatch(c -> "Berlin".equals(c.name) && "DE".equals(c.country)));
        assertTrue(cities.stream().anyMatch(c -> "Vienna".equals(c.name) && "AT".equals(c.country)));
        assertTrue(cities.stream().anyMatch(c -> "Zurich".equals(c.name) && "CH".equals(c.country)));
        assertTrue(cities.stream().allMatch(c -> c.id != null));
    }

    @Test
    void getWeatherByCityIdReturnsEmptyForUnknownCity() {
        Optional<WeatherDto> weather = weatherService.getWeatherByCityId(999_999L);
        assertTrue(weather.isEmpty());
    }

    @Test
    void getWeatherByCityIdForBerlinRespectsRangeAndRounding() {
        City berlin = cityRepository.findByName("Berlin").orElseThrow();

        Optional<WeatherDto> maybeWeather = weatherService.getWeatherByCityId(berlin.id);
        assertTrue(maybeWeather.isPresent());

        WeatherDto weather = maybeWeather.orElseThrow();
        assertEquals(berlin.id, weather.cityId);
        assertEquals("Berlin", weather.cityName);
        assertTrue(weather.temperature >= berlin.baseTemperature - 1.0);
        assertTrue(weather.temperature <= berlin.baseTemperature + 1.0);
        assertEquals(roundToOneDecimal(weather.temperature), weather.temperature, 0.0001);
        assertEquals(expectedDescription(weather.temperature), weather.description);
    }

    @Test
    void getWeatherByCityIdProducesOnlyAllowedDescriptions() {
        City berlin = cityRepository.findByName("Berlin").orElseThrow();

        for (int i = 0; i < 20; i++) {
            WeatherDto weather = weatherService.getWeatherByCityId(berlin.id).orElseThrow();
            assertFalse(Double.isNaN(weather.temperature));
            assertEquals(roundToOneDecimal(weather.temperature), weather.temperature, 0.0001);
            assertTrue(DESCRIPTIONS.contains(weather.description));
            assertEquals(expectedDescription(weather.temperature), weather.description);
        }
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String expectedDescription(double temperature) {
        if (temperature < 5.0) {
            return "Cold";
        }
        if (temperature <= 15.0) {
            return "Cool";
        }
        if (temperature <= 25.0) {
            return "Pleasant";
        }
        return "Warm";
    }
}

