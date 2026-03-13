package com.example.forklift.repository;

import com.example.forklift.entity.DowntimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с простоями
 */
@Repository
public interface DowntimeRepository extends JpaRepository<DowntimeEntity, Long> {

    /**
     * Получение всех простоев для указанного погрузчика, отсортированных по времени начала (обратный порядок)
     * @param forkliftId идентификатор погрузчика
     * @return список простоев
     */
    List<DowntimeEntity> findByForkliftIdOrderByStartTimeDesc(Long forkliftId);

    /**
     * Проверка наличия простоев для погрузчика
     * @param forkliftId идентификатор погрузчика
     * @return количество простоев
     */
    long countByForkliftId(Long forkliftId);
}
