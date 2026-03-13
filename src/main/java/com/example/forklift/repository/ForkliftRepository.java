package com.example.forklift.repository;

import com.example.forklift.entity.ForkliftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с погрузчиками
 */
@Repository
public interface ForkliftRepository extends JpaRepository<ForkliftEntity, Long> {

    /**
     * Поиск погрузчиков по номеру без учета регистра
     * @param number номер погрузчика для поиска
     * @return список найденных погрузчиков
     */
    @Query("SELECT f FROM ForkliftEntity f WHERE LOWER(f.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    List<ForkliftEntity> findByNumberContainingIgnoreCase(@Param("number") String number);

    /**
     * Получение всех активных погрузчиков
     * @return список активных погрузчиков
     */
    List<ForkliftEntity> findByActiveTrue();
}
