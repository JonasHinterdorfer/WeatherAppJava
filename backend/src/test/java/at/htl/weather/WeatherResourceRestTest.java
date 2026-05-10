package at.htl.weather;

import at.htl.weather.entity.City;
import at.htl.weather.repository.CityRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class WeatherResourceRestTest {

    @Inject
    CityRepository cityRepository;

    @Test
    void getCitiesReturnsJsonList() {
        given()
                .when().get("/cities")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(3))
                .body("name", hasItems("Berlin", "Vienna", "Zurich"))
                .body("id[0]", notNullValue())
                .body("country[0]", notNullValue());
    }

    @Test
    void getWeatherReturnsJsonForExistingCity() {
        City berlin = cityRepository.findByName("Berlin").orElseThrow();

        given()
                .when().get("/weather/" + berlin.id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cityId", equalTo(berlin.id.intValue()))
                .body("cityName", equalTo("Berlin"))
                .body("temperature", notNullValue())
                .body("description", notNullValue());
    }

    @Test
    void getWeatherReturns404ForUnknownCity() {
        given()
                .when().get("/weather/9999")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("City with id 9999 was not found"));
    }

    @Test
    void globalExceptionMapperReturnsSanitized500Response() {
        given()
                .when().get("/test/fail")
                .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Unexpected server error"));
    }
}

