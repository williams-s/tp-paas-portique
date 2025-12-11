#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE TYPE PROFILE AS ENUM ('etudiant', 'enseignant', 'personnel', 'invite', 'externe');
    
    -- Crée la table 'people' si elle n'existe pas
    CREATE TABLE IF NOT EXISTS people (
        id SERIAL PRIMARY KEY,
        num VARCHAR(50) UNIQUE,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(150) NOT NULL,
        function PROFILE NOT NULL,
        allowed_interval_start TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        allowed_interval_end TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        registration_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

    -- Insère des données de test si la table est vide
    --INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    --VALUES ('9058', 'Gilles', 'Giraud', 'enseignant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 year', CURRENT_TIMESTAMP);

    --INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    --VALUES ('32003415', 'Clément', 'Taurand', 'etudiant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 year', CURRENT_TIMESTAMP);

    
    --INSERT INTO people (num, first_name, last_name, function, allowed_interval_start, allowed_interval_end, registration_date)
    --VALUES ('1234', 'John', 'Doe', 'invite', CURRENT_TIMESTAMP - INTERVAL '2 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP);

    -- Simple PL/Python function that calls an external script using subprocess
    -- Note: This requires the plpython3u extension to be available in the Postgres image.
    -- The script path used here is '/docker-entrypoint-initdb.d/notify-cache-update.sh'.
    -- Ensure this script is mounted into the Postgres container (for example via docker-compose).

    DO $$
    BEGIN
        -- Create plpython3u extension if available
        BEGIN
            CREATE EXTENSION IF NOT EXISTS plpython3u;
        EXCEPTION WHEN OTHERS THEN
            -- Ignore if extension cannot be created
            RAISE NOTICE 'plpython3u extension not available or cannot be created: %', SQLERRM;
        END;
    END$$;

    -- Create a trigger function that will call the script in background via subprocess
    CREATE OR REPLACE FUNCTION call_notify_script()
    RETURNS TRIGGER AS $$
    import subprocess
    import os
    try:
        script = '/docker-entrypoint-initdb.d/notify-cache-update.sh'
        if os.path.exists(script) and os.access(script, os.X_OK):
            # spawn in background so DB transaction is not blocked
            subprocess.Popen([script], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            # Try to execute with sh in case execute bit is not set
            subprocess.Popen(['/bin/sh', script], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    except Exception as e:
        plpy.notice('Error executing notify script: %s' % str(e))

    # Return the appropriate row depending on operation
    if TG_OP == 'DELETE':
        return OLD
    else:
        return NEW
    $$ LANGUAGE plpython3u;

    -- Create trigger that calls the function after any change to 'people'
    DROP TRIGGER IF EXISTS trigger_call_notify_script ON people;
    CREATE TRIGGER trigger_call_notify_script
    AFTER INSERT OR UPDATE OR DELETE ON people
    FOR EACH ROW
    EXECUTE FUNCTION call_notify_script();

EOSQL
