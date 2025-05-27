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
