package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.analyzer.model.enums.ConditionOperation;
import ru.practicum.analyzer.model.enums.ConditionType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "conditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    ConditionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    ConditionOperation operation;

    @Column
    Integer value;
}
