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
DO $$
DECLARE
FILENAME VARCHAR(100);
BEGIN
    FILENAME := 'V1_3__LOG_TABLES';

    PERFORM CREATE_SEQUENCE_SAFE (
        'PK_ALH_ID_SEQ',
        'CREATE SEQUENCE PK_ALH_ID_SEQ
              START WITH 50
              INCREMENT BY 50
              NO MINVALUE
              NO MAXVALUE
              NO CYCLE',
        FILENAME
    );

    PERFORM CREATE_SEQUENCE_SAFE (
        'PK_ALV_ID_SEQ',
        'CREATE SEQUENCE PK_ALV_ID_SEQ
             START WITH 50
             INCREMENT BY 50
             NO MINVALUE
             NO MAXVALUE
             NO CYCLE',
        FILENAME
    );

    PERFORM CREATE_TABLE_SAFE (
        'GF_AUDIT_LOG_HEADER',
        FILENAME,
        'CREATE TABLE GF_AUDIT_LOG_HEADER (
             ALH_ID         BIGINT DEFAULT nextval(''PK_ALH_ID_SEQ'') PRIMARY KEY,
             ALH_CRE_DATE   TIMESTAMP NOT NULL,
             ALH_TABLE_NAME VARCHAR(50) NOT NULL,
             ALH_RECORD_PK  BIGINT NOT NULL,
             ALH_USR_ID     BIGINT NOT NULL
        )'
    );

    PERFORM CREATE_FOREIGN_KEY_SAFE (
        'FK_ALH_USR_ID',
        'GF_AUDIT_LOG_HEADER',
        'GF_USERS',
        'ALH_USR_ID',
        'USR_ID',
        FILENAME
    );

    PERFORM CREATE_TABLE_SAFE (
        'GF_AUDIT_LOG_VALUES',
        FILENAME,
        'CREATE TABLE GF_AUDIT_LOG_VALUES (
             ALV_ID             BIGINT DEFAULT nextval(''PK_ALV_ID_SEQ'') PRIMARY KEY,
             ALV_CRE_DATE       TIMESTAMP NOT NULL,
             ALV_FIELD_NAME     VARCHAR(100) NOT NULL,
             ALV_ALH_ID         BIGINT NOT NULL,
             ALV_PREVIOUS_VALUE TEXT,
             ALV_NEW_VALUE      TEXT
        )'
    );

    PERFORM CREATE_FOREIGN_KEY_SAFE (
        'FK_HEADER_VALUES',
        'GF_AUDIT_LOG_VALUES',
        'GF_AUDIT_LOG_HEADER',
        'ALV_ALH_ID',
        'ALH_ID',
        FILENAME
    );

    PERFORM CREATE_INDEX_SAFE(
        'IDX_ALH_USR_ID',
        'CREATE INDEX IDX_ALH_USR_ID ON GF_AUDIT_LOG_HEADER (ALH_USR_ID)',
        FILENAME
    );

    PERFORM CREATE_INDEX_SAFE(
        'IDX_ALH_TABLE_NAME_RECORD_PK',
        'CREATE INDEX IDX_ALH_TABLE_NAME_RECORD_PK ON GF_AUDIT_LOG_HEADER (ALH_TABLE_NAME, ALH_RECORD_PK)',
        FILENAME
    );

    PERFORM CREATE_INDEX_SAFE(
        'IDX_ALV_ALH_ID',
        'CREATE INDEX IDX_ALV_ALH_ID ON GF_AUDIT_LOG_VALUES (ALV_ALH_ID)',
        FILENAME
    );
END;
$$;

COMMENT ON TABLE GF_AUDIT_LOG_HEADER IS 'Change log table';
COMMENT ON COLUMN GF_AUDIT_LOG_HEADER.ALH_ID IS 'The log ID';
COMMENT ON COLUMN GF_AUDIT_LOG_HEADER.ALH_CRE_DATE IS 'Date when log was created';
COMMENT ON COLUMN GF_AUDIT_LOG_HEADER.ALH_TABLE_NAME IS 'Name of edited table';
COMMENT ON COLUMN GF_AUDIT_LOG_HEADER.ALH_RECORD_PK IS 'Primary key of the edited table';
COMMENT ON COLUMN GF_AUDIT_LOG_HEADER.ALH_USR_ID IS 'ID of the user who made the change';

COMMENT ON TABLE GF_AUDIT_LOG_VALUES IS 'Table with values before and after the change';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_ID IS 'ID of the change made';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_CRE_DATE IS 'Date of the creation of new value';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_FIELD_NAME IS 'Name of the field which was changed';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_PREVIOUS_VALUE IS 'Previous value';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_NEW_VALUE IS 'New value';
COMMENT ON COLUMN GF_AUDIT_LOG_VALUES.ALV_ALH_ID IS 'ID of the header to find specific changes faster';
DO $$
DECLARE
FILENAME VARCHAR(100);
BEGIN
    FILENAME := 'V1_4__PRODUCTS_ORDERS_TABLES';

    PERFORM create_sequence_safe(
        'PK_ORD_ID_SEQ',
        'CREATE SEQUENCE PK_ORD_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_ICS_ID_SEQ',
        'CREATE SEQUENCE PK_ICS_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_USH_ID_SEQ',
        'CREATE SEQUENCE PK_USH_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_PRD_ID_SEQ',
        'CREATE SEQUENCE PK_PRD_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_OST_ID_SEQ',
        'CREATE SEQUENCE PK_OST_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_OPR_ID_SEQ',
        'CREATE SEQUENCE PK_OPR_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_PPT_ID_SEQ',
        'CREATE SEQUENCE PK_PPT_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_sequence_safe(
        'PK_PRV_ID_SEQ',
        'CREATE SEQUENCE PK_PRV_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_table_safe(
        'GF_ORDERS',
        FILENAME,
        'CREATE TABLE GF_ORDERS (
            ORD_ID           BIGINT DEFAULT nextval(''PK_ORD_ID_SEQ'') PRIMARY KEY,
            ORD_PRD_ID       BIGINT NOT NULL,
            ORD_ICS_ID       BIGINT NOT NULL,
            ORD_DESCRIPTION  VARCHAR(250),
            ORD_TITLE        VARCHAR(100) NOT NULL,
            ORD_STATUS       BIGINT NOT NULL,
            ORD_PRIORITY     BIGINT NOT NULL,
            ORD_CRE_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            ORD_MOD_DATE     TIMESTAMP,
            ORD_DEL_DATE     TIMESTAMP
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
        'GF_USERS_SHOPS',
        FILENAME,
        'CREATE TABLE GF_USERS_SHOPS (
            USH_ID         BIGINT DEFAULT nextval(''PK_USH_ID_SEQ'') PRIMARY KEY,
            USH_USR_ID     BIGINT NOT NULL,
            USH_ICS_ID     BIGINT NOT NULL
        )'
    );

    PERFORM create_table_safe(
        'GF_ORDERS_STATUS',
        FILENAME,
        'CREATE TABLE GF_ORDERS_STATUS (
            OST_ID              BIGINT DEFAULT nextval(''PK_OST_ID_SEQ'') PRIMARY KEY,
            OST_STATUS_NAME     VARCHAR(100) NOT NULL
        )'
    );


    PERFORM create_table_safe(
        'GF_ORDERS_PRIORITY',
        FILENAME,
        'CREATE TABLE GF_ORDERS_PRIORITY (
            OPR_ID                BIGINT DEFAULT nextval(''PK_OPR_ID_SEQ'') PRIMARY KEY,
            OPR_PRIORITY_NAME     VARCHAR(100) NOT NULL
        )'
    );

    PERFORM create_table_safe(
        'GF_PRODUCTS',
        FILENAME,
        'CREATE TABLE GF_PRODUCTS (
            PRD_ID                BIGINT DEFAULT nextval(''PK_PRD_ID_SEQ'') PRIMARY KEY,
            PRD_NAME              VARCHAR(100) NOT NULL,
            PRD_TYPE              BIGINT NOT NULL,
            PRD_DESCRIPTION       VARCHAR(255) NOT NULL,
            PRD_CRE_DATE          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            PRD_MOD_DATE          TIMESTAMP,
            PRD_DEL_DATE          TIMESTAMP
        )'
    );

    PERFORM create_table_safe(
        'GF_PRODUCTS_TYPE',
        FILENAME,
        'CREATE TABLE GF_PRODUCTS_TYPE (
            PPT_ID                BIGINT DEFAULT nextval(''PK_PPT_ID_SEQ'') PRIMARY KEY,
            PPT_TYPE_NAME         VARCHAR(100) NOT NULL
        )'
    );

    PERFORM create_table_safe(
            'GF_PRODUCT_VARIANTS',
            FILENAME,
            'CREATE TABLE GF_PRODUCTS_VARIANTS(
            PRV_ID              BIGINT DEFAULT nextval(''PK_PRV_ID_SEQ'') PRIMARY KEY,
            PRV_PRD_ID          BIGINT REFERENCES GF_PRODUCTS(PRD_ID),
            PRV_NAME            VARCHAR(100) NOT NULL,
            PRV_QUANTITY        BIGINT DEFAULT 0 NOT NULL,
            PRV_CRE_DATE        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
            PRV_MOD_DATE        TIMESTAMP,
            PRV_DEL_DATE        TIMESTAMP
            )'
    );

    PERFORM create_sequence_safe(
        'PK_OPD_ID_SEQ',
        'CREATE SEQUENCE PK_OPD_ID_SEQ START WITH 50 INCREMENT BY 50 NO MINVALUE NO MAXVALUE',
        FILENAME
    );

    PERFORM create_table_safe(
        'GF_ORDERS_PRODUCTS',
        FILENAME,
        'CREATE TABLE GF_ORDERS_PRODUCTS (
            OPD_ID           BIGINT DEFAULT nextval(''PK_OPD_ID_SEQ'') PRIMARY KEY,
            OPD_ORD_ID       BIGINT NOT NULL,
            OPD_PRD_ID       BIGINT NOT NULL,
            OPD_PRV_ID       BIGINT NOT NULL,
            OPD_QUANTITY     BIGINT NOT NULL
        )'
    );

    PERFORM create_foreign_key_safe('FK_GF_ORDERS_STATUS', 'GF_ORDERS', 'GF_ORDERS_STATUS', 'ORD_STATUS', 'OST_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_ORDERS_PRIORITY', 'GF_ORDERS', 'GF_ORDERS_PRIORITY', 'ORD_PRIORITY', 'OPR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_PRODUCTS_TYPE', 'GF_PRODUCTS', 'GF_PRODUCTS_TYPE', 'PRD_TYPE', 'PPT_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_IC_SHOPS_OWNER', 'GF_IC_SHOPS', 'GF_USERS', 'ICS_OWNER_ID', 'USR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_USER', 'GF_USERS_SHOPS', 'GF_USERS', 'USH_USR_ID', 'USR_ID', FILENAME);
    PERFORM create_foreign_key_safe('FK_GF_USERS_SHOPS_SHOP', 'GF_USERS_SHOPS', 'GF_IC_SHOPS', 'USH_ICS_ID', 'ICS_ID', FILENAME);
    PERFORM create_foreign_key_safe(
        'FK_GF_ORDERS_PRODUCTS_ORDER',
        'GF_ORDERS_PRODUCTS',
        'GF_ORDERS',
        'OPD_ORD_ID',
        'ORD_ID',
        FILENAME
    );
    PERFORM create_foreign_key_safe(
        'FK_GF_ORDERS_PRODUCTS_PRODUCT',
        'GF_ORDERS_PRODUCTS',
        'GF_PRODUCTS',
        'OPD_PRD_ID',
        'PRD_ID',
        FILENAME
    );
    PERFORM create_foreign_key_safe(
        'FK_GF_ORDERS_PRODUCTS_VARIANTS',
        'GF_ORDERS_PRODUCTS',
        'GF_PRODUCTS_VARIANTS',
        'OPD_PRV_ID',
        'PRV_ID',
        FILENAME
    );

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
        'IDX_USH_USR_ID',
        'CREATE INDEX IDX_USH_USR_ID ON GF_USERS_SHOPS (USH_USR_ID)',
        FILENAME
    );

    PERFORM create_index_safe(
        'IDX_USH_ICS_ID',
        'CREATE INDEX IDX_USH_ICS_ID ON GF_USERS_SHOPS (USH_ICS_ID)',
        FILENAME
    );

END $$;
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
DO $$
DECLARE
FILENAME TEXT := 'V1_7__ADD_ROLES';
BEGIN
    PERFORM insert_into_gf_roles('LEADER', FILENAME);
    PERFORM insert_into_gf_roles('MEMBER', FILENAME);
    PERFORM insert_into_gf_roles('ADMIN', FILENAME);

    PERFORM assign_member_role(FILENAME);
END $$;
-- Funkcja do tworzenia użytkownika administratora
CREATE OR REPLACE FUNCTION create_admin_user(
    p_usr_email VARCHAR,
    p_filename VARCHAR
) RETURNS VOID AS $$
DECLARE
cnt INTEGER;
    v_admin_role_id BIGINT;
BEGIN
    -- Sprawdzenie, czy użytkownik już istnieje
SELECT COUNT(*) INTO cnt
FROM gf_users
WHERE usr_email = p_usr_email;

IF cnt = 0 THEN
        -- Pobranie ID roli 'ADMIN'
SELECT rol_id INTO v_admin_role_id
FROM gf_roles
WHERE rol_name = 'ADMIN';

IF v_admin_role_id IS NULL THEN
            PERFORM insert_into_log(p_filename, 'Role ADMIN does not exist.');
            RAISE EXCEPTION 'Role ADMIN does not exist.';
END IF;

        -- Wstawienie nowego użytkownika administratora
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
DO $$
DECLARE
FILENAME VARCHAR(100);
BEGIN
    FILENAME := 'V2_10__REMOVE_ORD_PRD_ID';

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'gf_orders' AND column_name = 'ord_prod_id'
    ) THEN
        EXECUTE 'ALTER TABLE GF_ORDERS DROP COLUMN ORD_PROD_ID';
        PERFORM insert_into_log(FILENAME, 'Column ORD_PROD_ID removed from GF_ORDERS.');
    ELSE
        PERFORM insert_into_log(FILENAME, 'Column ORD_PROD_ID does not exist in GF_ORDERS.');
    END IF;
END $$;