package at.htl.weather;

import at.htl.weather.entity.City;
import at.htl.weather.repository.CityRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class WeatherResourceTest {

    private static final Set<String> ALLOWED_DESCRIPTIONS = Set.of("Cold", "Cool", "Pleasant", "Warm");

    @Inject
    CityRepository cityRepository;

    @Test
    void getCitiesReturnsSeededCities() {
        given()
                .when().get("/cities")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3))
                .body("name", hasItems("Berlin", "Vienna", "Zurich"));
    }

    @Test
    void getCitiesReturnsCityContract() {
        given()
                .when().get("/cities")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(notNullValue()))
                .body("country", everyItem(notNullValue()));
    }

    @Test
    void seededCitiesAreUnique() {
        assertEquals(1L, cityRepository.count("name", "Berlin"));
        assertEquals(1L, cityRepository.count("name", "Vienna"));
        assertEquals(1L, cityRepository.count("name", "Zurich"));
    }

    @Test
    void getWeatherForSeededCityRespectsRangeAndRounding() {
        City berlin = cityRepository.findByName("Berlin").orElseThrow();

        JsonPath body = given()
                .when().get("/weather/" + berlin.id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath();

        Long cityId = body.getLong("cityId");
        String cityName = body.getString("cityName");
        Double temperature = body.getDouble("temperature");
        String description = body.getString("description");

        assertEquals(berlin.id, cityId);
        assertEquals("Berlin", cityName);
        assertNotNull(temperature);
        assertNotNull(description);
        assertTrue(temperature >= berlin.baseTemperature - 1.0);
        assertTrue(temperature <= berlin.baseTemperature + 1.0);
        assertEquals(roundToOneDecimal(temperature), temperature, 0.0001);
        assertEquals(descriptionFor(temperature), description);
    }

    @Test
    void weatherDescriptionIsAlwaysWithinAllowedValues() {
        City berlin = cityRepository.findByName("Berlin").orElseThrow();

        for (int i = 0; i < 15; i++) {
            JsonPath body = given()
                    .when().get("/weather/" + berlin.id)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();

            Double temperature = body.getDouble("temperature");
            String description = body.getString("description");

            assertNotNull(temperature);
            assertNotNull(description);
            assertFalse(Double.isNaN(temperature));
            assertEquals(roundToOneDecimal(temperature), temperature, 0.0001);
            assertEquals(descriptionFor(temperature), description);
            assertTrue(ALLOWED_DESCRIPTIONS.contains(description));
        }
    }

    @Test
    void getWeatherForUnknownCityReturns404() {
        given()
                .when().get("/weather/9999")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", is("City with id 9999 was not found"));
    }

    @Test
    void coldBandAlwaysReturnsColdDescription() {
        Long cityId = ensureCity("Test-Cold", "AT", 2.0);
        assertDescriptionForRepeatedCalls(cityId, "Cold");
    }

    @Test
    void coolBandAlwaysReturnsCoolDescription() {
        Long cityId = ensureCity("Test-Cool", "AT", 10.0);
        assertDescriptionForRepeatedCalls(cityId, "Cool");
    }

    @Test
    void pleasantBandAlwaysReturnsPleasantDescription() {
        Long cityId = ensureCity("Test-Pleasant", "AT", 20.0);
        assertDescriptionForRepeatedCalls(cityId, "Pleasant");
    }

    @Test
    void warmBandAlwaysReturnsWarmDescription() {
        Long cityId = ensureCity("Test-Warm", "AT", 30.0);
        assertDescriptionForRepeatedCalls(cityId, "Warm");
    }

    @Test
    void unhandledExceptionReturnsSanitized500Error() {
        given()
                .when().get("/test/fail")
                .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", is("Unexpected server error"));
    }

    private void assertDescriptionForRepeatedCalls(Long cityId, String expected) {
        for (int i = 0; i < 8; i++) {
            given()
                    .when().get("/weather/" + cityId)
                    .then()
                    .statusCode(200)
                    .body("description", is(expected));
        }
    }

    private String descriptionFor(double temperature) {
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

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    @Transactional
    Long ensureCity(String name, String country, double baseTemperature) {
        return cityRepository.findByName(name)
                .map(existing -> existing.id)
                .orElseGet(() -> {
                    City city = new City();
                    city.name = name;
                    city.country = country;
                    city.baseTemperature = baseTemperature;
                    cityRepository.persist(city);
                    return city.id;
                });
    }

    @Test
    void weatherEndpointContainsRequiredFields() {
        List<Map<String, Object>> cities = given()
                .when().get("/cities")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$");

        Map<String, Object> city = cities.stream()
                .filter(entry -> "Berlin".equals(entry.get("name")))
                .findFirst()
                .orElseThrow();

        Number cityId = (Number) city.get("id");

        given()
                .when().get("/weather/" + cityId.longValue())
                .then()
                .statusCode(200)
                .body("cityId", notNullValue())
                .body("cityName", notNullValue())
                .body("temperature", notNullValue())
                .body("description", notNullValue());
    }
}

