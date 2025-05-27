-- Funkcja do usuwania constraintów
CREATE OR REPLACE FUNCTION drop_constraint(
    p_table_name VARCHAR(50),
    p_constraint_name VARCHAR(50),
    p_filename VARCHAR(50)
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
SELECT COUNT(*)
INTO cnt
FROM information_schema.table_constraints
WHERE constraint_name = LOWER(p_constraint_name);

IF cnt > 0 THEN
        EXECUTE 'ALTER TABLE ' || quote_ident(p_table_name) || ' DROP CONSTRAINT ' || quote_ident(p_constraint_name);
        PERFORM insert_into_log(p_filename, 'Constraint ' || p_constraint_name || ' dropped successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Constraint ' || p_constraint_name || ' does not exist.');
END IF;
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error while dropping constraint ' || p_constraint_name || ': ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

-- Funkcja do zmiany nazwy kolumny
CREATE OR REPLACE FUNCTION rename_column(
    p_table_name VARCHAR(50),
    p_column_name_old VARCHAR(50),
    p_column_name_new VARCHAR(50),
    p_filename VARCHAR(50)
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN

SELECT COUNT(*)
INTO cnt
FROM information_schema.tables
WHERE table_name = LOWER(p_table_name);

IF cnt = 0 THEN
        PERFORM insert_into_log(p_filename, 'Table ' || p_table_name || ' does not exist.');
        RAISE EXCEPTION 'Table % does not exist.', p_table_name;
END IF;

    -- Sprawdzenie, czy nowa kolumna już istnieje
SELECT COUNT(*)
INTO cnt
FROM information_schema.columns
WHERE table_name = LOWER(p_table_name) AND column_name = LOWER(p_column_name_new);

IF cnt > 0 THEN
        PERFORM insert_into_log(p_filename, 'Column ' || p_column_name_new || ' already exists in table ' || p_table_name);
        RETURN;
END IF;

    -- Sprawdzenie, czy stara kolumna istnieje
SELECT COUNT(*)
INTO cnt
FROM information_schema.columns
WHERE table_name = LOWER(p_table_name) AND column_name = LOWER(p_column_name_old);

IF cnt = 0 THEN
        PERFORM insert_into_log(p_filename, 'Column ' || p_column_name_old || ' does not exist in table ' || p_table_name || '.');
        RETURN;
END IF;

    -- Zmiana nazwy kolumny
EXECUTE format('ALTER TABLE %I RENAME COLUMN %I TO %I', p_table_name, p_column_name_old, p_column_name_new);
PERFORM insert_into_log(p_filename, 'Column ' || p_column_name_old || ' was renamed to ' || p_column_name_new || '.');
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error while renaming column ' || p_column_name_old || ': ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

-- Funkcja do zmiany nazwy constraintu
CREATE OR REPLACE FUNCTION rename_constraint(
    p_table_name VARCHAR(50),
    p_constraint_name_new VARCHAR(50),
    p_constraint_name_old VARCHAR(50),
    p_filename VARCHAR(50)
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
    -- Sprawdzenie, czy nowy constraint już istnieje
SELECT COUNT(*)
INTO cnt
FROM information_schema.table_constraints
WHERE constraint_name = LOWER(p_constraint_name_new);

IF cnt > 0 THEN
        PERFORM insert_into_log(p_filename, 'Constraint ' || p_constraint_name_new || ' already exists.');
        RETURN;
END IF;

    -- Sprawdzenie, czy stary constraint istnieje
SELECT COUNT(*)
INTO cnt
FROM information_schema.table_constraints
WHERE constraint_name = LOWER(p_constraint_name_old);

IF cnt = 0 THEN
        PERFORM insert_into_log(p_filename, 'Constraint ' || p_constraint_name_old || ' does not exist.');
        RETURN;
END IF;

    -- Zmiana nazwy constraintu
EXECUTE format('ALTER TABLE %I RENAME CONSTRAINT %I TO %I', p_table_name, p_constraint_name_old, p_constraint_name_new);
PERFORM insert_into_log(p_filename, 'Constraint ' || p_constraint_name_old || ' renamed to ' || p_constraint_name_new);
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error while renaming constraint ' || p_constraint_name_old || ': ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
filename VARCHAR(100);
BEGIN
    filename := 'V1_5__LOG_TABLES';

    PERFORM rename_column('gf_audit_log_header', 'alh_cre_date', 'alh_change_date', filename);
    PERFORM rename_column('gf_audit_log_header', 'alh_table_name', 'alh_entity_name', filename);
    PERFORM rename_column('gf_audit_log_values', 'alv_field_name', 'alv_attribute', filename);


    PERFORM rename_constraint('gf_audit_log_header', 'nn_alh_change_date', 'nn_alh_cre_date', filename);
    PERFORM rename_constraint('gf_audit_log_header', 'nn_alh_entity_name', 'nn_alh_table_name', filename);
    PERFORM rename_constraint('gf_audit_log_values', 'nn_alv_attribute', 'nn_alv_field_name', filename);


    PERFORM drop_constraint('gf_audit_log_header', 'old_constraint_name', filename);

END
$$ LANGUAGE plpgsql;
