DO $$
DECLARE
filename VARCHAR(200);
BEGIN
    filename := 'V1_9__ADD_COLUMN_SALT';

    PERFORM add_column_safe(
        'gf_users',
        'usr_salt',
        'VARCHAR(255)',
        filename
    );

EXECUTE 'UPDATE gf_users SET usr_salt = ''placeHolder'' WHERE usr_salt IS NULL';
PERFORM insert_into_log(filename, 'Salt added to users without salt.');

    -- TODO: Po wykonaniu zapytania użyj metody changePassword, aby przypisać nowe wartości salt użytkownikom.

--     PERFORM add_not_null_constraint(
--         'gf_users',
--         'usr_salt',
--         'nn_usr_salt',
--         filename
--     );
END;
$$ LANGUAGE plpgsql;
