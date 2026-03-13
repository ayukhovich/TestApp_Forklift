package com.example.forklift.service;

import com.example.forklift.dto.ForkliftDTO;
import com.example.forklift.entity.ForkliftEntity;
import com.example.forklift.exception.ForkliftHasDowntimesException;
import com.example.forklift.exception.ForkliftNotFoundException;
import com.example.forklift.repository.DowntimeRepository;
import com.example.forklift.repository.ForkliftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с погрузчиками
 */
@Service
@Transactional
public class ForkliftService {

    private static final Logger logger = LoggerFactory.getLogger(ForkliftService.class);

    private final ForkliftRepository forkliftRepository;
    private final DowntimeRepository downtimeRepository;

    public ForkliftService(ForkliftRepository forkliftRepository, DowntimeRepository downtimeRepository) {
        this.forkliftRepository = forkliftRepository;
        this.downtimeRepository = downtimeRepository;
    }

    /**
     * Получение всех погрузчиков
     * @return список всех погрузчиков
     */
    @Transactional(readOnly = true)
    public List<ForkliftDTO> findAllForklifts() {
        logger.info("Получение списка всех погрузчиков");
        return forkliftRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Поиск погрузчиков по номеру без учета регистра
     * @param number номер для поиска
     * @return список найденных погрузчиков
     */
    @Transactional(readOnly = true)
    public List<ForkliftDTO> searchByNumber(String number) {
        logger.info("Поиск погрузчиков по номеру: {}", number);
        
        if (number == null || number.trim().isEmpty()) {
            return findAllForklifts();
        }
        
        return forkliftRepository.findByNumberContainingIgnoreCase(number.trim()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение погрузчика по id
     * @param id идентификатор погрузчика
     * @return DTO погрузчика
     * @throws ForkliftNotFoundException если погрузчик не найден
     */
    @Transactional(readOnly = true)
    public ForkliftDTO findForkliftById(Long id) {
        logger.info("Получение погрузчика по id: {}", id);
        ForkliftEntity entity = forkliftRepository.findById(id)
                .orElseThrow(() -> new ForkliftNotFoundException("Погрузчик с id " + id + " не найден"));
        return toDTO(entity);
    }

    /**
     * Создание нового погрузчика
     * @param dto данные нового погрузчика
     * @return созданный погрузчик
     */
    public ForkliftDTO createForklift(ForkliftDTO dto) {
        logger.info("Создание нового погрузчика: {}", dto.getNumber());
        
        ForkliftEntity entity = new ForkliftEntity();
        entity.setBrand(dto.getBrand());
        entity.setNumber(dto.getNumber());
        entity.setLoadCapacity(dto.getLoadCapacity());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setModifiedBy(dto.getModifiedBy() != null ? dto.getModifiedBy() : "Система");
        entity.setModifiedAt(LocalDateTime.now());
        
        ForkliftEntity saved = forkliftRepository.save(entity);
        logger.info("Создан погрузчик с id: {}", saved.getId());
        
        return toDTO(saved);
    }

    /**
     * Обновление погрузчика
     * @param id идентификатор погрузчика
     * @param dto новые данные
     * @return обновленный погрузчик
     * @throws ForkliftNotFoundException если погрузчик не найден
     */
    public ForkliftDTO updateForklift(Long id, ForkliftDTO dto) {
        logger.info("Обновление погрузчика с id: {}", id);
        
        ForkliftEntity entity = forkliftRepository.findById(id)
                .orElseThrow(() -> new ForkliftNotFoundException("Погрузчик с id " + id + " не найден"));
        
        entity.setBrand(dto.getBrand());
        entity.setNumber(dto.getNumber());
        entity.setLoadCapacity(dto.getLoadCapacity());
        entity.setActive(dto.getActive());
        entity.setModifiedBy(dto.getModifiedBy() != null ? dto.getModifiedBy() : "Система");
        entity.setModifiedAt(LocalDateTime.now());
        
        ForkliftEntity saved = forkliftRepository.save(entity);
        logger.info("Обновлен погрузчик с id: {}", saved.getId());
        
        return toDTO(saved);
    }

    /**
     * Удаление погрузчика
     * @param id идентификатор погрузчика
     * @throws ForkliftNotFoundException если погрузчик не найден
     * @throws ForkliftHasDowntimesException если у погрузчика есть простои
     */
    public void deleteForklift(Long id) {
        logger.info("Удаление погрузчика с id: {}", id);
        
        ForkliftEntity entity = forkliftRepository.findById(id)
                .orElseThrow(() -> new ForkliftNotFoundException("Погрузчик с id " + id + " не найден"));
        
        long downtimesCount = downtimeRepository.countByForkliftId(id);
        if (downtimesCount > 0) {
            logger.warn("Попытка удаления погрузчика {} с простоями", id);
            throw new ForkliftHasDowntimesException("Невозможно удалить погрузчик: у него есть зарегистрированные простои");
        }
        
        forkliftRepository.delete(entity);
        logger.info("Удален погрузчик с id: {}", id);
    }

    /**
     * Преобразование Entity в DTO
     * @param entity сущность погрузчика
     * @return DTO погрузчика
     */
    private ForkliftDTO toDTO(ForkliftEntity entity) {
        return new ForkliftDTO(
                entity.getId(),
                entity.getBrand(),
                entity.getNumber(),
                entity.getLoadCapacity(),
                entity.getActive(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}
