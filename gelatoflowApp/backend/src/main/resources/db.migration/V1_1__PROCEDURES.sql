DO $$
DECLARE
cnt INTEGER;
BEGIN

SELECT COUNT(*) INTO cnt
FROM information_schema.tables
WHERE table_name = LOWER('gf_script_log');

IF cnt = 0 THEN
        EXECUTE 'CREATE TABLE GF_SCRIPT_LOG (
            scl_id SERIAL PRIMARY KEY,
            scl_cre_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            scl_msg VARCHAR(500) NOT NULL,
            scl_filename VARCHAR(50) NOT NULL
        )';
END IF;

SELECT COUNT(*) INTO cnt
FROM information_schema.sequences
WHERE sequence_name = LOWER('gf_script_log_seq');

IF cnt = 0 THEN
        EXECUTE 'CREATE SEQUENCE gf_script_log_seq START 1';
END IF;
END $$;

CREATE OR REPLACE FUNCTION insert_into_log(
    p_filename VARCHAR,
    p_message VARCHAR
) RETURNS VOID AS $$
BEGIN
INSERT INTO GF_SCRIPT_LOG(scl_msg, scl_filename)
VALUES (p_message, p_filename);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_table_safe(
    p_table_name VARCHAR,
    p_filename VARCHAR,
    p_creation_script VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*) INTO cnt
FROM information_schema.tables
WHERE table_name = LOWER(p_table_name);

IF cnt = 0 THEN
        EXECUTE p_creation_script;
        PERFORM insert_into_log(p_filename, 'Table ' || p_table_name || ' created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Table ' || p_table_name || ' already exists.');
END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_index_safe(
    p_index_name VARCHAR,
    p_creation_script VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*) INTO cnt
FROM pg_indexes
WHERE indexname = LOWER(p_index_name);

IF cnt = 0 THEN
        EXECUTE p_creation_script;
        PERFORM insert_into_log(p_filename, 'Index ' || p_index_name || ' created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Index ' || p_index_name || ' already exists.');
END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_sequence_safe(
    p_sequence_name VARCHAR,
    p_creation_script VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*) INTO cnt
FROM information_schema.sequences
WHERE sequence_name = LOWER(p_sequence_name);

IF cnt = 0 THEN
        EXECUTE p_creation_script;
        PERFORM insert_into_log(p_filename, 'Sequence ' || p_sequence_name || ' created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Sequence ' || p_sequence_name || ' already exists.');
END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_foreign_key_safe(
    p_foreign_key_name VARCHAR,
    p_primary_table_name VARCHAR,
    p_foreign_table_name VARCHAR,
    p_primary_field VARCHAR,
    p_foreign_field VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*) INTO cnt
FROM information_schema.table_constraints
WHERE constraint_name = LOWER(p_foreign_key_name) AND constraint_type = 'FOREIGN KEY';

IF cnt = 0 THEN
        EXECUTE 'ALTER TABLE ' || p_primary_table_name ||
                ' ADD CONSTRAINT ' || p_foreign_key_name ||
                ' FOREIGN KEY (' || p_primary_field || ') REFERENCES ' ||
                p_foreign_table_name || ' (' || p_foreign_field || ')';
        PERFORM insert_into_log(p_filename, 'Foreign key ' || p_foreign_key_name || ' created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Foreign key ' || p_foreign_key_name || ' already exists.');
END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION create_primary_key_safe(
    p_primary_key_name VARCHAR,
    p_table_name VARCHAR,
    p_field VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*) INTO cnt
FROM information_schema.table_constraints
WHERE constraint_name = LOWER(p_primary_key_name) AND constraint_type = 'PRIMARY KEY';

IF cnt = 0 THEN
        EXECUTE 'ALTER TABLE ' || p_table_name ||
                ' ADD CONSTRAINT ' || p_primary_key_name ||
                ' PRIMARY KEY (' || p_field || ')';
        PERFORM insert_into_log(p_filename, 'Primary key ' || p_primary_key_name || ' created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Primary key ' || p_primary_key_name || ' already exists.');
END IF;
END;
$$ LANGUAGE plpgsql;
