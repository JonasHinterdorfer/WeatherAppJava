package at.htl.weather.service;

import at.htl.weather.dto.CityDto;
import at.htl.weather.dto.WeatherDto;
import at.htl.weather.entity.City;
import at.htl.weather.repository.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeatherService {

    private final CityRepository cityRepository;

    public WeatherService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    public List<CityDto> getAllCities() {
        return cityRepository.listAll().stream()
                .map(city -> new CityDto(city.id, city.name, city.country))
                .toList();
    }

    @Transactional
    public Optional<WeatherDto> getWeatherByCityId(Long cityId) {
        return cityRepository.findByIdOptional(cityId)
                .map(this::toWeatherDto);
    }

    private WeatherDto toWeatherDto(City city) {
        double variation = Math.random() * 2 - 1;
        double actualTemp = roundToOneDecimal(city.baseTemperature + variation);
        return new WeatherDto(city.id, city.name, actualTemp, getDescription(actualTemp));
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String getDescription(double temperature) {
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

