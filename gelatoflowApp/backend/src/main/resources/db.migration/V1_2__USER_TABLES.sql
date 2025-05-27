DO $$
DECLARE
FILENAME VARCHAR(100);
BEGIN
    FILENAME := 'V1_2__USER_TABLES';

    PERFORM create_sequence_safe(
        'PK_USH_ID_SEQ',
        'CREATE SEQUENCE PK_USH_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_table_safe(
        'GF_USERS_SHOPS',
        FILENAME,
        'CREATE TABLE GF_USERS_SHOPS (
            USH_ID         BIGINT DEFAULT nextval(''PK_USH_ID_SEQ'') PRIMARY KEY,
            USH_USR_ID     BIGINT NOT NULL,
            USH_ICS_ID     BIGINT NOT NULL
        )'
    );

    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_USER', 'GF_USERS_SHOPS', 'GF_USERS', 'USH_USR_ID', 'USR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_SHOP', 'GF_USERS_SHOPS', 'GF_IC_SHOPS', 'USH_ICS_ID', 'ICS_ID', FILENAME);

    PERFORM create_index_safe(
        'IDX_USH_USR_ID',
        'CREATE INDEX IDX_USH_USR_ID ON GF_USERS_SHOPS (USH_USR_ID)',
        FILENAME
    );


    PERFORM create_sequence_safe(
        'PK_USR_ID_SEQ',
        'CREATE SEQUENCE PK_USR_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_table_safe(
        'GF_USERS',
        FILENAME,
        'CREATE TABLE GF_USERS (
            USR_ID           BIGINT DEFAULT nextval(''PK_USR_ID_SEQ'') PRIMARY KEY,
            USR_CRE_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            USR_MOD_DATE     TIMESTAMP,
            USR_DEL_DATE     TIMESTAMP,
            USR_FIRST_NAME   VARCHAR(100) NOT NULL,
            USR_LAST_NAME    VARCHAR(100) NOT NULL,
            USR_EMAIL        VARCHAR(255) NOT NULL,
            USR_PASSWORD     VARCHAR(255) NOT NULL,
            USR_ROL_ID       BIGINT NOT NULL
        )'
    );



    PERFORM create_sequence_safe(
        'PK_ICS_ID_SEQ',
        'CREATE SEQUENCE PK_ICS_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );


    PERFORM create_sequence_safe(
        'PK_ROL_ID_SEQ',
        'CREATE SEQUENCE PK_ROL_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_table_safe(
        'GF_USERS',
        FILENAME,
        'CREATE TABLE GF_USERS (
            USR_ID           BIGINT DEFAULT nextval(''PK_USR_ID_SEQ'') PRIMARY KEY,
            USR_CRE_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            USR_MOD_DATE     TIMESTAMP,
            USR_DEL_DATE     TIMESTAMP,
            USR_FIRST_NAME   VARCHAR(100) NOT NULL,
            USR_LAST_NAME    VARCHAR(100) NOT NULL,
            USR_EMAIL        VARCHAR(255) NOT NULL,
            USR_PASSWORD     VARCHAR(255) NOT NULL,
            USR_ROL_ID       BIGINT NOT NULL
        )'
    );

    PERFORM create_table_safe(
        'GF_IC_SHOPS',
        FILENAME,
        'CREATE TABLE GF_IC_SHOPS (
            ICS_ID          BIGINT DEFAULT nextval(''PK_ICS_ID_SEQ'') PRIMARY KEY,
            ICS_OWNER_ID    BIGINT REFERENCES GF_USERS(USR_ID),
            ICS_NAME        VARCHAR(100) NOT NULL,
            ICS_LOCATION    VARCHAR(100) NOT NULL,
            ICS_CRE_DATE    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            ICS_MOD_DATE    TIMESTAMP,
            ICS_DEL_DATE    TIMESTAMP
        )'
    );


    PERFORM create_table_safe(
        'GF_ROLES',
        FILENAME,
        'CREATE TABLE GF_ROLES (
            ROL_ID         BIGINT DEFAULT nextval(''PK_ROL_ID_SEQ'') PRIMARY KEY,
            ROL_NAME       VARCHAR(100) NOT NULL
        )'
    );


    PERFORM create_foreign_key_safe('FK_GF_USERS_ROLES', 'GF_USERS', 'GF_ROLES', 'USR_ROL_ID', 'ROL_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_IC_SHOPS_OWNER', 'GF_IC_SHOPS', 'GF_USERS', 'ICS_OWNER_ID', 'USR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_USER', 'GF_USERS_SHOPS', 'GF_USERS', 'USH_USR_ID', 'USR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_SHOP', 'GF_USERS_SHOPS', 'GF_IC_SHOPS', 'USH_ICS_ID', 'ICS_ID', FILENAME);

    PERFORM create_index_safe(
        'IDX_USR_EMAIL_DEL_DATE',
        'CREATE UNIQUE INDEX IDX_USR_EMAIL_DEL_DATE ON GF_USERS (USR_EMAIL, USR_DEL_DATE)',
        FILENAME
    );

    PERFORM create_index_safe(
        'IDX_ICS_NAME_DEL_DATE',
        'CREATE UNIQUE INDEX IDX_ICS_NAME_DEL_DATE ON GF_IC_SHOPS (ICS_NAME, ICS_DEL_DATE)',
        FILENAME
    );



    PERFORM create_index_safe(
        'IDX_USH_ICS_ID',
        'CREATE INDEX IDX_USH_ICS_ID ON GF_USERS_SHOPS (USH_ICS_ID)',
        FILENAME
    );

    PERFORM create_index_safe(
        'IDX_ROL_NAME',
        'CREATE INDEX IDX_ROL_NAME ON GF_ROLES (ROL_NAME)',
        FILENAME
    );

END $$;
