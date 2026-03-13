-- Начальные данные для справочника погрузчиков

-- Вставка тестовых погрузчиков
INSERT INTO forklifts (brand, forklift_number, load_capacity, active, modified_at, modified_by)
VALUES 
    ('Toyota', 'FL-001', 2.500, true, CURRENT_TIMESTAMP, 'Администратор'),
    ('Komatsu', 'FL-002', 3.000, true, CURRENT_TIMESTAMP, 'Администратор'),
    ('CAT', 'FL-003', 2.800, true, CURRENT_TIMESTAMP, 'Администратор'),
    ('Hyster', 'FL-004', 1.500, true, CURRENT_TIMESTAMP, 'Администратор'),
    ('Toyota', 'FL-005', 2.000, true, CURRENT_TIMESTAMP, 'Администратор')
ON CONFLICT DO NOTHING;

-- Вставка тестовых простоев
INSERT INTO downtimes (forklift_id, start_time, end_time, problem_description)
VALUES 
    (1, '2026-03-13 08:00:00', '2026-03-13 12:00:00', 'Замена гидравлического шланга'),
    (1, '2026-03-13 14:00:00', NULL, 'Неисправность двигателя'),
    (2, '2026-03-13 09:30:00', '2026-03-10 11:00:00', 'Плановое ТО'),
    (3, '2026-03-13 16:00:00', NULL, 'Проблемы с электрикой')
ON CONFLICT DO NOTHING;
