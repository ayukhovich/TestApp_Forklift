package com.example.forklift.service;

import com.example.forklift.dto.DowntimeDTO;
import com.example.forklift.entity.DowntimeEntity;
import com.example.forklift.exception.DowntimeNotFoundException;
import com.example.forklift.exception.ForkliftNotFoundException;
import com.example.forklift.repository.DowntimeRepository;
import com.example.forklift.repository.ForkliftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с простоями
 */
@Service
@Transactional
public class DowntimeService {

    private static final Logger logger = LoggerFactory.getLogger(DowntimeService.class);

    private final DowntimeRepository downtimeRepository;
    private final ForkliftRepository forkliftRepository;

    public DowntimeService(DowntimeRepository downtimeRepository, ForkliftRepository forkliftRepository) {
        this.downtimeRepository = downtimeRepository;
        this.forkliftRepository = forkliftRepository;
    }

    /**
     * Получение всех простоев для указанного погрузчика
     * @param forkliftId идентификатор погрузчика
     * @return список простоев, отсортированный по времени начала (обратный порядок)
     */
    @Transactional(readOnly = true)
    public List<DowntimeDTO> findDowntimesByForkliftId(Long forkliftId) {
        logger.info("Получение списка простоев для погрузчика с id: {}", forkliftId);
        
        if (!forkliftRepository.existsById(forkliftId)) {
            throw new ForkliftNotFoundException("Погрузчик с id " + forkliftId + " не найден");
        }
        
        return downtimeRepository.findByForkliftIdOrderByStartTimeDesc(forkliftId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение простоя по id
     * @param id идентификатор простоя
     * @return DTO простоя
     * @throws DowntimeNotFoundException если простой не найден
     */
    @Transactional(readOnly = true)
    public DowntimeDTO findDowntimeById(Long id) {
        logger.info("Получение простоя по id: {}", id);
        DowntimeEntity entity = downtimeRepository.findById(id)
                .orElseThrow(() -> new DowntimeNotFoundException("Простой с id " + id + " не найден"));
        return toDTO(entity);
    }

    /**
     * Создание нового простоя
     * @param dto данные нового простоя
     * @return созданный простой
     */
    public DowntimeDTO createDowntime(DowntimeDTO dto) {
        logger.info("Создание нового простоя для погрузчика с id: {}", dto.getForkliftId());
        
        if (!forkliftRepository.existsById(dto.getForkliftId())) {
            throw new ForkliftNotFoundException("Погрузчик с id " + dto.getForkliftId() + " не найден");
        }
        
        DowntimeEntity entity = new DowntimeEntity();
        entity.setForkliftId(dto.getForkliftId());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setProblemDescription(dto.getProblemDescription());
        
        DowntimeEntity saved = downtimeRepository.save(entity);
        logger.info("Создан простой с id: {}", saved.getId());
        
        return toDTO(saved);
    }

    /**
     * Обновление простоя
     * @param id идентификатор простоя
     * @param dto новые данные
     * @return обновленный простой
     * @throws DowntimeNotFoundException если простой не найден
     */
    public DowntimeDTO updateDowntime(Long id, DowntimeDTO dto) {
        logger.info("Обновление простоя с id: {}", id);
        
        DowntimeEntity entity = downtimeRepository.findById(id)
                .orElseThrow(() -> new DowntimeNotFoundException("Простой с id " + id + " не найден"));
        
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setProblemDescription(dto.getProblemDescription());
        
        DowntimeEntity saved = downtimeRepository.save(entity);
        logger.info("Обновлен простой с id: {}", saved.getId());
        
        return toDTO(saved);
    }

    /**
     * Удаление простоя
     * @param id идентификатор простоя
     * @throws DowntimeNotFoundException если простой не найден
     */
    public void deleteDowntime(Long id) {
        logger.info("Удаление простоя с id: {}", id);
        
        DowntimeEntity entity = downtimeRepository.findById(id)
                .orElseThrow(() -> new DowntimeNotFoundException("Простой с id " + id + " не найден"));
        
        downtimeRepository.delete(entity);
        logger.info("Удален простой с id: {}", id);
    }

    /**
     * Преобразование Entity в DTO
     * @param entity сущность простоя
     * @return DTO простоя
     */
    private DowntimeDTO toDTO(DowntimeEntity entity) {
        return new DowntimeDTO(
                entity.getId(),
                entity.getForkliftId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getProblemDescription(),
                entity.getCalculatedDowntimeHours()
        );
    }
}
