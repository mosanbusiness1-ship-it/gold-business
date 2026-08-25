-- Fix organisation.category to be a plain String instead of enum/check-constrained values
ALTER TABLE IF EXISTS organisation
    DROP CONSTRAINT IF EXISTS organisation_category_check;

ALTER TABLE IF EXISTS organisation
    ALTER COLUMN category TYPE VARCHAR(255);

UPDATE organisation
SET category = 'general'
WHERE category IS NULL;

ALTER TABLE IF EXISTS organisation
    ALTER COLUMN category SET NOT NULL;
