-- Backfill legacy needs created through organisation bulk flows.
-- An organisation need is attributed to the organisation owner when no user was stored.
UPDATE abstract_user_need need
SET user_id = organisation.owner_id
FROM organisation_need link
JOIN organisation ON organisation.id = link.organisation_id
WHERE need.id = link.need_id
  AND need.user_id IS NULL
  AND organisation.owner_id IS NOT NULL;
