package com.example.forklift.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных о погрузчике
 */
public class ForkliftDTO {

    private Long id;
    private String brand;
    private String number;
    private BigDecimal loadCapacity;
    private Boolean active;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    public ForkliftDTO() {
    }

    public ForkliftDTO(Long id, String brand, String number, BigDecimal loadCapacity, 
                       Boolean active, LocalDateTime modifiedAt, String modifiedBy) {
        this.id = id;
        this.brand = brand;
        this.number = number;
        this.loadCapacity = loadCapacity;
        this.active = active;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

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
}
