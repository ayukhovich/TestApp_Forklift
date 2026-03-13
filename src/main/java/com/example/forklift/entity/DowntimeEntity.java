package com.example.forklift.entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Сущность простоя
 * Представляет запись о простое погрузчика
 */
@Entity
@Table(name = "downtimes")
public class DowntimeEntity {

    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор погрузчика
     */
    @Column(name = "forklift_id", nullable = false)
    private Long forkliftId;

    /**
     * Дата и время начала простоя
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Дата и время окончания простоя (nullable - если не указано, простой продолжается)
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Описание проблемы
     */
    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    /**
     * Конструктор по умолчанию
     */
    public DowntimeEntity() {
    }

    /**
     * Конструктор с параметрами
     * @param forkliftId идентификатор погрузчика
     * @param startTime время начала простоя
     * @param endTime время окончания простоя
     * @param problemDescription описание проблемы
     */
    public DowntimeEntity(Long forkliftId, LocalDateTime startTime, 
                          LocalDateTime endTime, String problemDescription) {
        this.forkliftId = forkliftId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.problemDescription = problemDescription;
    }

    /**
     * Вычисление времени простоя в часах и минутах
     * Если конечная дата не указана - используется текущее время
     * @return строка в формате "X ч Y мин"
     */
    public String getCalculatedDowntimeHours() {
        LocalDateTime end = (this.endTime != null) ? this.endTime : LocalDateTime.now();
        Duration duration = Duration.between(startTime, end);
        
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        return String.format("%d ч %d мин", hours, minutes);
    }

    /**
     * Получение времени простоя в минутах (для сортировки/фильтрации)
     * @return количество минут простоя
     */
    public Long getDowntimeMinutes() {
        LocalDateTime end = (this.endTime != null) ? this.endTime : LocalDateTime.now();
        Duration duration = Duration.between(startTime, end);
        return duration.toMinutes();
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForkliftId() {
        return forkliftId;
    }

    public void setForkliftId(Long forkliftId) {
        this.forkliftId = forkliftId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }
}
