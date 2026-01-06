create database mini_football_db;
CREATE USER mini_football_db_manager WITH PASSWORD "12345678";

--Donner les privilèges nécessaires
--Accorder la possibilité de créer des tables et d’effectuer des opérations CRUD
GRANT CONNECT ON DATABASE mini_football_db TO mini_football_db_manager;
GRANT CREATE ON DATABASE mini_football_db TO mini_football_db_manager;

--accorder les droits sur le schéma public
GRANT USAGE ON SCHEMA public TO mini_football_db_manager;
GRANT CREATE ON SCHEMA public TO mini_football_db_manager;

-- Accorder les droits CRUD sur toutes les tables existantes
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO mini_football_db_manager;


