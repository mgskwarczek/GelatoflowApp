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