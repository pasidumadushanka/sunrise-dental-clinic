-- ============================================================
-- Sunrise Dental Clinic — Appointment & Patient Management System
-- schema.sql
-- Target: MySQL 5.7+ / MariaDB (WampServer default)
--
-- This script matches the audited ERD produced in Phase 1:
--   - USER stores Receptionist/Admin via a role discriminator
--     (Single Table Inheritance; see class-diagram design note
--     and UserFactory for the OO-side mapping)
--   - Composite UNIQUE key (dentistId, appointmentDate,
--     appointmentTime) prevents double-booking at the DB level
--   - APPOINTMENT.createdBy is an audit-trail FK to USER
--   - BILL.treatmentCost intentionally duplicates
--     TREATMENT.price (price-snapshot design decision)
-- ============================================================

DROP DATABASE IF EXISTS sunrise_dental_db;
CREATE DATABASE sunrise_dental_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_db;

-- ------------------------------------------------------------
-- USER  (Receptionist / Admin — Single Table Inheritance)
-- ------------------------------------------------------------
CREATE TABLE USER (
    userId          VARCHAR(10)   NOT NULL,
    username        VARCHAR(50)   NOT NULL,
    passwordHash    VARCHAR(255)  NOT NULL,
    role            ENUM('Receptionist','Admin') NOT NULL,
    createdAt       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userId),
    UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- DENTIST
-- ------------------------------------------------------------
CREATE TABLE DENTIST (
    dentistId       VARCHAR(10)   NOT NULL,
    name            VARCHAR(100)  NOT NULL,
    specialization  VARCHAR(80)   NULL,
    contactNumber   VARCHAR(15)   NULL,
    PRIMARY KEY (dentistId)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- PATIENT
-- ------------------------------------------------------------
CREATE TABLE PATIENT (
    patientId       VARCHAR(10)   NOT NULL,
    name            VARCHAR(100)  NOT NULL,
    address         VARCHAR(200)  NULL,
    contactNumber   VARCHAR(15)   NOT NULL,
    registeredDate  DATE          NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (patientId)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- TREATMENT
-- ------------------------------------------------------------
CREATE TABLE TREATMENT (
    treatmentCode   VARCHAR(10)   NOT NULL,
    description     VARCHAR(150)  NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (treatmentCode)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- APPOINTMENT
-- Composite UNIQUE key enforces the "no double-booking" rule
-- at the database level (dentistId + date + time together).
-- ------------------------------------------------------------
CREATE TABLE APPOINTMENT (
    appointmentNo     VARCHAR(15)   NOT NULL,
    patientId         VARCHAR(10)   NOT NULL,
    dentistId         VARCHAR(10)   NOT NULL,
    treatmentCode     VARCHAR(10)   NOT NULL,
    appointmentDate   DATE          NOT NULL,
    appointmentTime   TIME          NOT NULL,
    status            ENUM('Scheduled','Completed','Cancelled') NOT NULL DEFAULT 'Scheduled',
    createdBy         VARCHAR(10)   NOT NULL,
    PRIMARY KEY (appointmentNo),
    CONSTRAINT uk_dentist_slot UNIQUE (dentistId, appointmentDate, appointmentTime),
    CONSTRAINT fk_appt_patient   FOREIGN KEY (patientId)     REFERENCES PATIENT(patientId)     ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appt_dentist   FOREIGN KEY (dentistId)     REFERENCES DENTIST(dentistId)     ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appt_treatment FOREIGN KEY (treatmentCode) REFERENCES TREATMENT(treatmentCode) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appt_createdby FOREIGN KEY (createdBy)     REFERENCES USER(userId)           ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- MEDICAL_HISTORY
-- ------------------------------------------------------------
CREATE TABLE MEDICAL_HISTORY (
    historyId       VARCHAR(10)   NOT NULL,
    patientId       VARCHAR(10)   NOT NULL,
    appointmentNo   VARCHAR(15)   NULL,
    notes           TEXT          NULL,
    visitDate       DATE          NOT NULL,
    PRIMARY KEY (historyId),
    CONSTRAINT fk_mh_patient     FOREIGN KEY (patientId)     REFERENCES PATIENT(patientId)         ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_mh_appointment FOREIGN KEY (appointmentNo) REFERENCES APPOINTMENT(appointmentNo) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- BILL
-- treatmentCost intentionally duplicates TREATMENT.price at
-- billing time (deliberate denormalization — see design note).
-- ------------------------------------------------------------
CREATE TABLE BILL (
    billId            VARCHAR(10)   NOT NULL,
    appointmentNo     VARCHAR(15)   NOT NULL,
    consultationFee   DECIMAL(10,2) NOT NULL,
    treatmentCost     DECIMAL(10,2) NOT NULL,
    totalAmount       DECIMAL(10,2) NOT NULL,
    billDate          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (billId),
    CONSTRAINT uk_bill_appointment UNIQUE (appointmentNo),
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointmentNo) REFERENCES APPOINTMENT(appointmentNo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Helpful indexes beyond the PK/FK/UK constraints above
-- ------------------------------------------------------------
CREATE INDEX idx_appt_status       ON APPOINTMENT(status);
CREATE INDEX idx_appt_date         ON APPOINTMENT(appointmentDate);
CREATE INDEX idx_patient_name      ON PATIENT(name);
