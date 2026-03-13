package com.example.forklift.controller;

import com.example.forklift.dto.ForkliftDTO;
import com.example.forklift.service.ForkliftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с погрузчиками
 */
@RestController
@RequestMapping("/api/forklifts")
public class ForkliftController {

    private final ForkliftService forkliftService;

    public ForkliftController(ForkliftService forkliftService) {
        this.forkliftService = forkliftService;
    }

    /**
     * Получение списка всех погрузчиков
     * @return список погрузчиков
     */
    @GetMapping
    public ResponseEntity<List<ForkliftDTO>> getAllForklifts() {
        List<ForkliftDTO> forklifts = forkliftService.findAllForklifts();
        return ResponseEntity.ok(forklifts);
    }

    /**
     * Поиск погрузчиков по номеру
     * @param number номер для поиска
     * @return список найденных погрузчиков
     */
    @GetMapping("/search")
    public ResponseEntity<List<ForkliftDTO>> searchForklifts(@RequestParam(required = false) String number) {
        List<ForkliftDTO> forklifts = forkliftService.searchByNumber(number);
        return ResponseEntity.ok(forklifts);
    }

    /**
     * Получение погрузчика по id
     * @param id идентификатор погрузчика
     * @return погрузчик
     */
    @GetMapping("/{id}")
    public ResponseEntity<ForkliftDTO> getForkliftById(@PathVariable Long id) {
        ForkliftDTO forklift = forkliftService.findForkliftById(id);
        return ResponseEntity.ok(forklift);
    }

    /**
     * Создание нового погрузчика
     * @param dto данные погрузчика
     * @return созданный погрузчик
     */
    @PostMapping
    public ResponseEntity<ForkliftDTO> createForklift(@RequestBody ForkliftDTO dto) {
        ForkliftDTO created = forkliftService.createForklift(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновление погрузчика
     * @param id идентификатор погрузчика
     * @param dto новые данные
     * @return обновленный погрузчик
     */
    @PutMapping("/{id}")
    public ResponseEntity<ForkliftDTO> updateForklift(@PathVariable Long id, @RequestBody ForkliftDTO dto) {
        ForkliftDTO updated = forkliftService.updateForklift(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление погрузчика
     * @param id идентификатор погрузчика
     * @return статус операции
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForklift(@PathVariable Long id) {
        forkliftService.deleteForklift(id);
        return ResponseEntity.noContent().build();
    }
}
