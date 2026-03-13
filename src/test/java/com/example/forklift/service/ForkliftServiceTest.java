package com.example.forklift.service;

import com.example.forklift.dto.ForkliftDTO;
import com.example.forklift.entity.ForkliftEntity;
import com.example.forklift.exception.ForkliftHasDowntimesException;
import com.example.forklift.exception.ForkliftNotFoundException;
import com.example.forklift.repository.DowntimeRepository;
import com.example.forklift.repository.ForkliftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Юнит тесты для ForkliftService
 */
@ExtendWith(MockitoExtension.class)
class ForkliftServiceTest {

    @Mock
    private ForkliftRepository forkliftRepository;

    @Mock
    private DowntimeRepository downtimeRepository;

    @InjectMocks
    private ForkliftService forkliftService;

    private ForkliftEntity testForklift;
    private ForkliftDTO testForkliftDTO;

    @BeforeEach
    void setUp() {
        testForklift = new ForkliftEntity("Toyota", "FL-001", new BigDecimal("2.500"), "Тестовый пользователь");
        testForklift.setId(1L);
        testForklift.setActive(true);
        testForklift.setModifiedAt(LocalDateTime.now());

        testForkliftDTO = new ForkliftDTO(
                1L,
                "Toyota",
                "FL-001",
                new BigDecimal("2.500"),
                true,
                LocalDateTime.now(),
                "Тестовый пользователь"
        );
    }

    /**
     * Тест получения всех погрузчиков
     */
    @Test
    void testFindAllForklifts() {
        // Подготовка
        List<ForkliftEntity> forklifts = Arrays.asList(
                testForklift,
                new ForkliftEntity("Komatsu", "FL-002", new BigDecimal("3.000"), "Тестовый пользователь")
        );
        when(forkliftRepository.findAll()).thenReturn(forklifts);

        // Выполнение
        List<ForkliftDTO> result = forkliftService.findAllForklifts();

        // Проверка
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(forkliftRepository, times(1)).findAll();
    }

    /**
     * Тест поиска погрузчиков по номеру
     */
    @Test
    void testSearchByNumber() {
        // Подготовка
        when(forkliftRepository.findByNumberContainingIgnoreCase("FL-001")).thenReturn(Arrays.asList(testForklift));

        // Выполнение
        List<ForkliftDTO> result = forkliftService.searchByNumber("FL-001");

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FL-001", result.get(0).getNumber());
        verify(forkliftRepository, times(1)).findByNumberContainingIgnoreCase("FL-001");
    }

    /**
     * Тест поиска с пустым номером (возвращает все записи)
     */
    @Test
    void testSearchByNumberEmpty() {
        // Подготовка
        List<ForkliftEntity> forklifts = Arrays.asList(testForklift);
        when(forkliftRepository.findAll()).thenReturn(forklifts);

        // Выполнение
        List<ForkliftDTO> result = forkliftService.searchByNumber("");

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(forkliftRepository, times(1)).findAll();
    }

    /**
     * Тест получения погрузчика по id
     */
    @Test
    void testFindForkliftById() {
        // Подготовка
        when(forkliftRepository.findById(1L)).thenReturn(Optional.of(testForklift));

        // Выполнение
        ForkliftDTO result = forkliftService.findForkliftById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Toyota", result.getBrand());
        verify(forkliftRepository, times(1)).findById(1L);
    }

    /**
     * Тест получения несуществующего погрузчика
     */
    @Test
    void testFindForkliftByIdNotFound() {
        // Подготовка
        when(forkliftRepository.findById(999L)).thenReturn(Optional.empty());

        // Выполнение и проверка
        assertThrows(ForkliftNotFoundException.class, () -> forkliftService.findForkliftById(999L));
    }

    /**
     * Тест создания нового погрузчика
     */
    @Test
    void testCreateForklift() {
        // Подготовка
        when(forkliftRepository.save(any(ForkliftEntity.class))).thenReturn(testForklift);

        // Выполнение
        ForkliftDTO result = forkliftService.createForklift(testForkliftDTO);

        // Проверка
        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        verify(forkliftRepository, times(1)).save(any(ForkliftEntity.class));
    }

    /**
     * Тест обновления погрузчика
     */
    @Test
    void testUpdateForklift() {
        // Подготовка
        when(forkliftRepository.findById(1L)).thenReturn(Optional.of(testForklift));
        when(forkliftRepository.save(any(ForkliftEntity.class))).thenReturn(testForklift);

        testForkliftDTO.setBrand("Toyota Updated");

        // Выполнение
        ForkliftDTO result = forkliftService.updateForklift(1L, testForkliftDTO);

        // Проверка
        assertNotNull(result);
        verify(forkliftRepository, times(1)).findById(1L);
        verify(forkliftRepository, times(1)).save(any(ForkliftEntity.class));
    }

    /**
     * Тест удаления погрузчика без простоев
     */
    @Test
    void testDeleteForklift() {
        // Подготовка
        when(forkliftRepository.findById(1L)).thenReturn(Optional.of(testForklift));
        when(downtimeRepository.countByForkliftId(1L)).thenReturn(0L);

        // Выполнение
        forkliftService.deleteForklift(1L);

        // Проверка
        verify(forkliftRepository, times(1)).findById(1L);
        verify(downtimeRepository, times(1)).countByForkliftId(1L);
        verify(forkliftRepository, times(1)).delete(testForklift);
    }

    /**
     * Тест удаления погрузчика с простоями (должно выбросить исключение)
     */
    @Test
    void testDeleteForkliftWithDowntimes() {
        // Подготовка
        when(forkliftRepository.findById(1L)).thenReturn(Optional.of(testForklift));
        when(downtimeRepository.countByForkliftId(1L)).thenReturn(5L);

        // Выполнение и проверка
        assertThrows(ForkliftHasDowntimesException.class, () -> forkliftService.deleteForklift(1L));
        verify(forkliftRepository, never()).delete(any());
    }
}
