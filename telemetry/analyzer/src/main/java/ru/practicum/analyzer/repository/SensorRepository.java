package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {
}
