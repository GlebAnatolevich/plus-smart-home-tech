package ru.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "sensors")
public class Sensor {
    @Id
    @Column(nullable = false)
    String id;

    @Column(name = "hub_id", nullable = false)
    String hubId;
}
