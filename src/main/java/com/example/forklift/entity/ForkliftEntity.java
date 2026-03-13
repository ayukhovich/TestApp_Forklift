package com.example.forklift.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность погрузчика
 * Представляет запись справочника погрузчиков
 */
@Entity
@Table(name = "forklifts")
public class ForkliftEntity {

    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Марка погрузчика
     */
    @Column(nullable = false)
    private String brand;

    /**
     * Номер погрузчика
     */
    @Column(name = "forklift_number", nullable = false, length = 50)
    private String number;

    /**
     * Грузоподъемность (максимум 3 знака после запятой)
     */
    @Column(name = "load_capacity", nullable = false, precision = 10, scale = 3)
    private BigDecimal loadCapacity;

    /**
     * Признак активности записи
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Дата и время последнего изменения
     */
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    /**
     * Пользователь, который последним редактировал запись
     */
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    /**
     * Конструктор по умолчанию
     */
    public ForkliftEntity() {
    }

    /**
     * Конструктор с параметрами
     * @param brand марка погрузчика
     * @param number номер погрузчика
     * @param loadCapacity грузоподъемность
     * @param modifiedBy пользователь, создающий запись
     */
    public ForkliftEntity(String brand, String number, BigDecimal loadCapacity, String modifiedBy) {
        this.brand = brand;
        this.number = number;
        this.loadCapacity = loadCapacity;
        this.active = true;
        this.modifiedAt = LocalDateTime.now();
        this.modifiedBy = modifiedBy;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(BigDecimal loadCapacity) {
        this.loadCapacity = loadCapacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Метод для обновления времени изменения перед сохранением
     */
    @PrePersist
    @PreUpdate
    public void updateModifiedTime() {
        this.modifiedAt = LocalDateTime.now();
    }
}
