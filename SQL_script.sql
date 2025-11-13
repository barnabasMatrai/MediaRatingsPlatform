-- ===========================
-- ESG ENERGY DATABASE SCHEMA
-- ===========================

-- Drop old tables if reinitializing
DROP TABLE IF EXISTS measurements CASCADE;
DROP TABLE IF EXISTS metrics CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS periods CASCADE;

-- ===========================
-- Core reference tables
-- ===========================

CREATE TABLE periods (
    period_id SERIAL PRIMARY KEY,
    year_label VARCHAR(20) UNIQUE NOT NULL   -- e.g. '2024/2025'
);

CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL        -- e.g. 'Bauteil B'
);

CREATE TABLE metrics (
    metric_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL        -- e.g. 'Energy Consumption'
);

-- ===========================
-- Measurements
-- ===========================
CREATE TABLE measurements (
    measurement_id SERIAL PRIMARY KEY,
    location_id INT NOT NULL REFERENCES locations(location_id),
    metric_id INT NOT NULL REFERENCES metrics(metric_id),
    period_id INT NOT NULL REFERENCES periods(period_id),
    month SMALLINT CHECK (month BETWEEN 1 AND 12),
    value NUMERIC,
    unit VARCHAR(20),
    UNIQUE (location_id, metric_id, period_id, month)
);

-- ===========================
-- Staging Table for CSV Import
-- ===========================
CREATE TABLE raw_energy_import (
    year_label VARCHAR(20),
    month VARCHAR(10),
    location VARCHAR(100),
    value VARCHAR(20),
    unit VARCHAR(20)
);
