package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.ScenarioCondition;
import ru.practicum.analyzer.model.ScenarioConditionId;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {
    @Modifying
    @Transactional
    @Query("delete from ScenarioCondition sc where sc.id.scenarioId = ?1")
    void deleteByIdScenarioId(Long scenarioId);
}
