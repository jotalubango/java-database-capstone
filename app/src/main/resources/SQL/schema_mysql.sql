-- ------------------------------------------
-- MySql
create database cms;
-- ------------------------------------------
-- DROP TABLES
-- ------------------------------------------
DROP TABLE IF EXISTS cms.doctor_available_times;
DROP TABLE IF EXISTS cms.appointment;
DROP TABLE IF EXISTS cms.patient;
DROP TABLE IF EXISTS cms.doctor;
DROP TABLE IF EXISTS cms.admin;
-- ------------------------------------------
-- CREATE TABLES
-- ------------------------------------------
use cms;
-- ------------------------------------------
-- Table: cms.admin
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS cms.admin (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT admin_pkey PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------
-- Table: cms.doctor
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS cms.doctor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    CONSTRAINT doctor_pkey PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------
-- Table: cms.patient
-- ------------------------------------------

CREATE TABLE IF NOT EXISTS cms.patient (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    emergency_contact VARCHAR(255),
    insurance_provider VARCHAR(100),
    CONSTRAINT patient_pkey PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------
-- Table: cms.appointment
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS cms.appointment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_time DATETIME(6),
    status INT NOT NULL,
    CONSTRAINT appointment_pkey PRIMARY KEY (id),
    CONSTRAINT fk4apif2ewfyf14077ichee8g06 FOREIGN KEY (patient_id)
        REFERENCES cms.patient (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkoeb98n82eph1dx43v3y2bcmsl FOREIGN KEY (doctor_id)
        REFERENCES cms.doctor (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------
-- Table: cms.doctor_available_times
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS cms.doctor_available_times (
    doctor_id BIGINT NOT NULL,
    available_time VARCHAR(255),
    CONSTRAINT fkdgs10srq75djpwnb9c22k3lmk FOREIGN KEY (doctor_id)
        REFERENCES cms.doctor (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
