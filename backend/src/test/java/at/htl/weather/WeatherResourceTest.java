package at.htl.weather;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class WeatherResourceTest {

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
    void getWeatherForExistingCityReturnsTemperatureAndDescription() {
        given()
                .when().get("/weather/1")
                .then()
                .statusCode(200)
                .body("cityId", notNullValue())
                .body("cityName", notNullValue())
                .body("temperature", notNullValue())
                .body("description", notNullValue());
    }

    @Test
    void getWeatherForUnknownCityReturns404() {
        given()
                .when().get("/weather/9999")
                .then()
                .statusCode(404)
                .body("message", is("City with id 9999 was not found"));
    }
}

