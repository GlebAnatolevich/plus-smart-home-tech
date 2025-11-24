package ru.practicum.analyzer.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioActionId {
    @NotNull
    Long scenarioId;

    @NotNull
    String sensorId;

    @NotNull
    Long actionId;
}
