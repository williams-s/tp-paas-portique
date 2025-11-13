#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE TYPE PROFILE AS ENUM ('etudiant', 'enseignant', 'personnel', 'invite', 'externe');
    
    -- Crée la table 'people' si elle n'existe pas
    CREATE TABLE IF NOT EXISTS people (
        id SERIAL PRIMARY KEY,
        num integer UNIQUE,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(150) NOT NULL,
        function PROFILE NOT NULL,
        allowed_interval_start TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        allowed_interval_end TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        registration_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

    -- Insère des données de test si la table est vide
    INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    VALUES (9058, 'Gilles', 'Giraud', 'enseignant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 year', CURRENT_TIMESTAMP);

    INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    VALUES (32003415, 'Clément', 'Taurand', 'etudiant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 year', CURRENT_TIMESTAMP);

    
    INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    VALUES (1234, 'John', 'Doe', 'invite', CURRENT_TIMESTAMP - INTERVAL '2 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP);

EOSQL