-- Funkcja do bezpiecznego dodawania kolumny do tabeli
CREATE OR REPLACE FUNCTION add_column_safe(
    p_table_name VARCHAR,
    p_column_name VARCHAR,
    p_column_type VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
BEGIN
    -- Sprawdzenie, czy kolumna już istnieje
SELECT COUNT(*)
INTO cnt
FROM information_schema.columns
WHERE table_name = LOWER(p_table_name)
  AND column_name = LOWER(p_column_name);

IF cnt = 0 THEN
        -- Dodanie kolumny
        EXECUTE format('ALTER TABLE %I ADD COLUMN %I %s', p_table_name, p_column_name, p_column_type);
        -- Logowanie operacji
        PERFORM insert_into_log(p_filename, 'Column ' || p_column_name || ' added to table ' || p_table_name || '.');
ELSE
        -- Kolumna już istnieje
        PERFORM insert_into_log(p_filename, 'Column ' || p_column_name || ' already exists in table ' || p_table_name || '.');
END IF;
EXCEPTION
    WHEN OTHERS THEN
        -- Logowanie nieoczekiwanego błędu i ponowne wywołanie wyjątku
        PERFORM insert_into_log(p_filename, 'Unexpected error in add_column_safe: ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION insert_into_gf_roles(
    p_rol_name VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
role_exists BOOLEAN;
BEGIN
SELECT EXISTS (
    SELECT 1
    FROM gf_roles
    WHERE rol_name = p_rol_name
) INTO role_exists;

IF NOT role_exists THEN
        INSERT INTO gf_roles (rol_id, rol_name)
        VALUES (nextval('pk_rol_id_seq'), p_rol_name);
        PERFORM insert_into_log(p_filename, 'Inserted record into gf_roles with rol_name = ' || p_rol_name);
ELSE
        PERFORM insert_into_log(p_filename, 'Record with rol_name = ' || p_rol_name || ' already exists in gf_roles.');
END IF;
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error in insert_into_gf_roles: ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION assign_member_role(
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
v_member_role_id BIGINT;
BEGIN
SELECT rol_id INTO v_member_role_id FROM gf_roles WHERE rol_name = 'MEMBER';

IF v_member_role_id IS NULL THEN
        PERFORM insert_into_log(p_filename, 'Role MEMBER does not exist.');
        RAISE EXCEPTION 'Role MEMBER does not exist.';
END IF;

UPDATE gf_users
SET usr_rol_id = v_member_role_id
WHERE usr_rol_id IS NULL;

PERFORM insert_into_log(p_filename, 'Assigned MEMBER role to all users who had no role.');
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error in assign_member_role: ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
filename VARCHAR(100);
BEGIN
    filename := 'V1_6__PROCEDURES_USERS';

    PERFORM insert_into_gf_roles('MEMBER', filename);

    PERFORM assign_member_role(filename);
END;
$$ LANGUAGE plpgsql;
