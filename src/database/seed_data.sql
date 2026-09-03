-- ============================================================
-- Sunrise Dental Clinic — seed_data.sql
-- Sample data for local development / testing against schema.sql
--
-- Run this AFTER schema.sql has been executed successfully.
--
-- NOTE ON PASSWORDS:
-- passwordHash values below are SHA-256 hashes of these plaintext
-- passwords (for Module 1 testing only — a proper PasswordUtil with
-- salting will be introduced in feature/user-factory):
--   admin1      -> Admin@123
--   receptionA  -> Reception@123
-- ============================================================

USE sunrise_dental_db;

-- ------------------------------------------------------------
-- USER (1 Admin, 1 Receptionist)
-- ------------------------------------------------------------
INSERT INTO USER (userId, username, passwordHash, role, createdAt) VALUES
('U001', 'admin1',     'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'Admin',        NOW()),
('U002', 'receptionA', '238f1cf33d39690fba3c171984fc1120a1181d236723d337e6a2fdc8d92ae88a', 'Receptionist', NOW());

-- ------------------------------------------------------------
-- DENTIST (2 dentists)
-- ------------------------------------------------------------
INSERT INTO DENTIST (dentistId, name, specialization, contactNumber) VALUES
('D001', 'Dr. Nimal Perera',    'Orthodontics',   '0711234567'),
('D002', 'Dr. Sanduni Fernando','General Dentistry','0772345678');

-- ------------------------------------------------------------
-- TREATMENT (3 treatments)
-- ------------------------------------------------------------
INSERT INTO TREATMENT (treatmentCode, description, price) VALUES
('T001', 'Root Canal Treatment', 150.00),
('T002', 'Teeth Cleaning & Polishing', 40.00),
('T003', 'Tooth Extraction', 60.00);
