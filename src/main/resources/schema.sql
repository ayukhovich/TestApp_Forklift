-- Схема базы данных для справочника погрузчиков

-- Таблица погрузчиков
CREATE TABLE IF NOT EXISTS forklifts (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    forklift_number VARCHAR(50) NOT NULL,
    load_capacity DECIMAL(10, 3) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255) NOT NULL DEFAULT 'Система'
);

-- Индекс для поиска по номеру погрузчика без учета регистра
CREATE INDEX IF NOT EXISTS idx_forklift_number_lower ON forklifts (LOWER(forklift_number));

-- Таблица простоев
CREATE TABLE IF NOT EXISTS downtimes (
    id BIGSERIAL PRIMARY KEY,
    forklift_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    problem_description TEXT,
    FOREIGN KEY (forklift_id) REFERENCES forklifts(id) ON DELETE CASCADE
);

-- Индекс для поиска простоев по id погрузчика
CREATE INDEX IF NOT EXISTS idx_downtime_forklift_id ON downtimes(forklift_id);

-- Индекс для сортировки простоев по времени начала
CREATE INDEX IF NOT EXISTS idx_downtime_start_time ON downtimes(start_time DESC);
