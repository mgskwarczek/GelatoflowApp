CREATE OR REPLACE FUNCTION create_admin_user(
    p_usr_email VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
    v_admin_role_id BIGINT;
BEGIN
SELECT COUNT(*) INTO cnt
FROM gf_users
WHERE usr_email = p_usr_email;

IF cnt = 0 THEN
SELECT rol_id INTO v_admin_role_id
FROM gf_roles
WHERE rol_name = 'ADMIN';

IF v_admin_role_id IS NULL THEN
            PERFORM insert_into_log(p_filename, 'Role ADMIN does not exist.');
            RAISE EXCEPTION 'Role ADMIN does not exist.';
END IF;

INSERT INTO gf_users (
    usr_id,
    usr_first_name,
    usr_last_name,
    usr_email,
    usr_password,
    usr_rol_id
)
VALUES (
           nextval('pk_usr_id_seq'),
           'admin',
           'admin',
           p_usr_email,
           '5q@Hho0(zfGs',
           v_admin_role_id
       );

PERFORM insert_into_log(p_filename, 'Admin user created successfully.');
ELSE
        PERFORM insert_into_log(p_filename, 'Admin user already exists.');
END IF;
EXCEPTION
    WHEN OTHERS THEN
        PERFORM insert_into_log(p_filename, 'Unexpected error in create_admin_user: ' || SQLERRM);
        RAISE;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
filename VARCHAR(200);
BEGIN
    filename := 'V1_8__ADD_ADMIN';

    PERFORM create_admin_user(
        'admin@gelato.pl',
        filename
    );
END;
$$ LANGUAGE plpgsql;
