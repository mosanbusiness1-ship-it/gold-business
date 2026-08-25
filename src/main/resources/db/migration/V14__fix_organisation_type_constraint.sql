-- Force Organisation.type to accept only GROUP or COMMUNITY
ALTER TABLE IF EXISTS organisation
    DROP CONSTRAINT IF EXISTS organisation_type_check;

UPDATE organisation
SET type = 'COMMUNITY'
WHERE type IS NULL OR type NOT IN ('GROUP', 'COMMUNITY');

ALTER TABLE IF EXISTS organisation
    ALTER COLUMN type TYPE VARCHAR(50);

ALTER TABLE IF EXISTS organisation
    ADD CONSTRAINT organisation_type_check
    CHECK (type IN ('GROUP', 'COMMUNITY'));
