package com.example.forklift.dto;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о простое
 */
public class DowntimeDTO {

    private Long id;
    private Long forkliftId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String problemDescription;
    private String calculatedDowntime;

    public DowntimeDTO() {
    }

    public DowntimeDTO(Long id, Long forkliftId, LocalDateTime startTime, 
                       LocalDateTime endTime, String problemDescription, String calculatedDowntime) {
        this.id = id;
        this.forkliftId = forkliftId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.problemDescription = problemDescription;
        this.calculatedDowntime = calculatedDowntime;
    }

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

    public String getCalculatedDowntime() {
        return calculatedDowntime;
    }

    public void setCalculatedDowntime(String calculatedDowntime) {
        this.calculatedDowntime = calculatedDowntime;
    }
}
