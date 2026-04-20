package at.htl.weather.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "city")
public class City extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false, length = 3)
    public String country;

    @Column(nullable = false)
    public double baseTemperature;
}

