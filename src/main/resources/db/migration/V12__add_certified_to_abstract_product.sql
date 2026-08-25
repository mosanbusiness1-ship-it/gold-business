ALTER TABLE IF EXISTS abstract_product
    ADD COLUMN IF NOT EXISTS certified boolean DEFAULT false;

UPDATE abstract_product
SET certified = false
WHERE certified IS NULL;

ALTER TABLE abstract_product
    ALTER COLUMN certified SET NOT NULL;
