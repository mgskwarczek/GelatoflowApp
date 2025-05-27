DO $$
DECLARE
FILENAME TEXT := 'V1_7__ADD_ROLES';
BEGIN
    PERFORM insert_into_gf_roles('LEADER', FILENAME);
    PERFORM insert_into_gf_roles('MEMBER', FILENAME);
    PERFORM insert_into_gf_roles('ADMIN', FILENAME);

    PERFORM assign_member_role(FILENAME);
END $$;
