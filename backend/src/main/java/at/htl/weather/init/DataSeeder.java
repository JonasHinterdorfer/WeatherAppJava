package at.htl.weather.init;

import at.htl.weather.entity.City;
import at.htl.weather.repository.CityRepository;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Startup
@Singleton
public class DataSeeder {

    private final CityRepository cityRepository;

    public DataSeeder(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    void seed(@Observes StartupEvent event) {
        createIfMissing("Berlin", "DE", 12.0);
        createIfMissing("Vienna", "AT", 13.5);
        createIfMissing("Zurich", "CH", 11.0);
    }

    private void createIfMissing(String name, String country, double baseTemperature) {
        if (cityRepository.findByName(name).isPresent()) {
            return;
        }

        City city = new City();
        city.name = name;
        city.country = country;
        city.baseTemperature = baseTemperature;
        cityRepository.persist(city);
    }
}

