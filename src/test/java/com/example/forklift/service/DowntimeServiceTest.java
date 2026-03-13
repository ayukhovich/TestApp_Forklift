package com.example.forklift.service;

import com.example.forklift.dto.DowntimeDTO;
import com.example.forklift.entity.DowntimeEntity;
import com.example.forklift.exception.DowntimeNotFoundException;
import com.example.forklift.exception.ForkliftNotFoundException;
import com.example.forklift.repository.DowntimeRepository;
import com.example.forklift.repository.ForkliftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Юнит тесты для DowntimeService
 */
@ExtendWith(MockitoExtension.class)
class DowntimeServiceTest {

    @Mock
    private DowntimeRepository downtimeRepository;

    @Mock
    private ForkliftRepository forkliftRepository;

    @InjectMocks
    private DowntimeService downtimeService;

    private DowntimeEntity testDowntime;
    private DowntimeDTO testDowntimeDTO;

    @BeforeEach
    void setUp() {
        testDowntime = new DowntimeEntity(
                1L,
                LocalDateTime.of(2024, 1, 15, 8, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0),
                "Замена гидравлического шланга"
        );
        testDowntime.setId(1L);

        testDowntimeDTO = new DowntimeDTO(
                1L,
                1L,
                LocalDateTime.of(2024, 1, 15, 8, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0),
                "Замена гидравлического шланга",
                "4 ч 0 мин"
        );
    }

    /**
     * Тест получения всех простоев для погрузчика
     */
    @Test
    void testFindDowntimesByForkliftId() {
        // Подготовка
        when(forkliftRepository.existsById(1L)).thenReturn(true);
        when(downtimeRepository.findByForkliftIdOrderByStartTimeDesc(1L)).thenReturn(Arrays.asList(testDowntime));

        // Выполнение
        List<DowntimeDTO> result = downtimeService.findDowntimesByForkliftId(1L);

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Замена гидравлического шланга", result.get(0).getProblemDescription());
        verify(downtimeRepository, times(1)).findByForkliftIdOrderByStartTimeDesc(1L);
    }

    /**
     * Тест получения простоев для несуществующего погрузчика
     */
    @Test
    void testFindDowntimesByForkliftIdNotFound() {
        // Подготовка
        when(forkliftRepository.existsById(999L)).thenReturn(false);

        // Выполнение и проверка
        assertThrows(ForkliftNotFoundException.class, () -> downtimeService.findDowntimesByForkliftId(999L));
    }

    /**
     * Тест получения простоя по id
     */
    @Test
    void testFindDowntimeById() {
        // Подготовка
        when(downtimeRepository.findById(1L)).thenReturn(Optional.of(testDowntime));

        // Выполнение
        DowntimeDTO result = downtimeService.findDowntimeById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Замена гидравлического шланга", result.getProblemDescription());
        verify(downtimeRepository, times(1)).findById(1L);
    }

    /**
     * Тест получения несуществующего простоя
     */
    @Test
    void testFindDowntimeByIdNotFound() {
        // Подготовка
        when(downtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // Выполнение и проверка
        assertThrows(DowntimeNotFoundException.class, () -> downtimeService.findDowntimeById(999L));
    }

    /**
     * Тест создания нового простоя
     */
    @Test
    void testCreateDowntime() {
        // Подготовка
        when(forkliftRepository.existsById(1L)).thenReturn(true);
        when(downtimeRepository.save(any(DowntimeEntity.class))).thenReturn(testDowntime);

        // Выполнение
        DowntimeDTO result = downtimeService.createDowntime(testDowntimeDTO);

        // Проверка
        assertNotNull(result);
        assertEquals("Замена гидравлического шланга", result.getProblemDescription());
        verify(downtimeRepository, times(1)).save(any(DowntimeEntity.class));
    }

    /**
     * Тест создания простоя для несуществующего погрузчика
     */
    @Test
    void testCreateDowntimeForkliftNotFound() {
        // Подготовка
        when(forkliftRepository.existsById(999L)).thenReturn(false);
        testDowntimeDTO.setForkliftId(999L);

        // Выполнение и проверка
        assertThrows(ForkliftNotFoundException.class, () -> downtimeService.createDowntime(testDowntimeDTO));
    }

    /**
     * Тест обновления простоя
     */
    @Test
    void testUpdateDowntime() {
        // Подготовка
        when(downtimeRepository.findById(1L)).thenReturn(Optional.of(testDowntime));
        when(downtimeRepository.save(any(DowntimeEntity.class))).thenReturn(testDowntime);

        testDowntimeDTO.setProblemDescription("Обновленное описание");

        // Выполнение
        DowntimeDTO result = downtimeService.updateDowntime(1L, testDowntimeDTO);

        // Проверка
        assertNotNull(result);
        verify(downtimeRepository, times(1)).findById(1L);
        verify(downtimeRepository, times(1)).save(any(DowntimeEntity.class));
    }

    /**
     * Тест обновления несуществующего простоя
     */
    @Test
    void testUpdateDowntimeNotFound() {
        // Подготовка
        when(downtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // Выполнение и проверка
        assertThrows(DowntimeNotFoundException.class, () -> downtimeService.updateDowntime(999L, testDowntimeDTO));
    }

    /**
     * Тест удаления простоя
     */
    @Test
    void testDeleteDowntime() {
        // Подготовка
        when(downtimeRepository.findById(1L)).thenReturn(Optional.of(testDowntime));

        // Выполнение
        downtimeService.deleteDowntime(1L);

        // Проверка
        verify(downtimeRepository, times(1)).findById(1L);
        verify(downtimeRepository, times(1)).delete(testDowntime);
    }

    /**
     * Тест удаления несуществующего простоя
     */
    @Test
    void testDeleteDowntimeNotFound() {
        // Подготовка
        when(downtimeRepository.findById(999L)).thenReturn(Optional.empty());

        // Выполнение и проверка
        assertThrows(DowntimeNotFoundException.class, () -> downtimeService.deleteDowntime(999L));
    }
}
