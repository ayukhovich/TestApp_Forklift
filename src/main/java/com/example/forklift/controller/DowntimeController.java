package com.example.forklift.controller;

import com.example.forklift.dto.DowntimeDTO;
import com.example.forklift.service.DowntimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с простоями
 */
@RestController
@RequestMapping("/api/downtimes")
public class DowntimeController {

    private final DowntimeService downtimeService;

    public DowntimeController(DowntimeService downtimeService) {
        this.downtimeService = downtimeService;
    }

    /**
     * Получение списка простоев для указанного погрузчика
     * @param forkliftId идентификатор погрузчика
     * @return список простоев
     */
    @GetMapping
    public ResponseEntity<List<DowntimeDTO>> getDowntimesByForklift(@RequestParam Long forkliftId) {
        List<DowntimeDTO> downtimes = downtimeService.findDowntimesByForkliftId(forkliftId);
        return ResponseEntity.ok(downtimes);
    }

    /**
     * Получение простоя по id
     * @param id идентификатор простоя
     * @return простой
     */
    @GetMapping("/{id}")
    public ResponseEntity<DowntimeDTO> getDowntimeById(@PathVariable Long id) {
        DowntimeDTO downtime = downtimeService.findDowntimeById(id);
        return ResponseEntity.ok(downtime);
    }

    /**
     * Создание нового простоя
     * @param dto данные простоя
     * @return созданный простой
     */
    @PostMapping
    public ResponseEntity<DowntimeDTO> createDowntime(@RequestBody DowntimeDTO dto) {
        DowntimeDTO created = downtimeService.createDowntime(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновление простоя
     * @param id идентификатор простоя
     * @param dto новые данные
     * @return обновленный простой
     */
    @PutMapping("/{id}")
    public ResponseEntity<DowntimeDTO> updateDowntime(@PathVariable Long id, @RequestBody DowntimeDTO dto) {
        DowntimeDTO updated = downtimeService.updateDowntime(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление простоя
     * @param id идентификатор простоя
     * @return статус операции
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDowntime(@PathVariable Long id) {
        downtimeService.deleteDowntime(id);
        return ResponseEntity.noContent().build();
    }
}
