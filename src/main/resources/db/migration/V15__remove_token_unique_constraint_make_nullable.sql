-- Remove unique constraint on token column and make it nullable
-- This allows us to not store the full JWT token in the database for security reasons
-- The token is only returned to the client at creation time and regenerated on each retrieval
-- We use token_hash (64 chars) for lookups instead of the full JWT token (500+ chars)

-- Drop the unique index if it exists (safely)
DROP INDEX IF EXISTS idx_org_inv_token;

-- Modify the token column to be nullable and reduce to smaller size since we don't store JWT anymore
ALTER TABLE organisation_invitations
    ALTER COLUMN token DROP NOT NULL,
    ALTER COLUMN token TYPE varchar(255);  -- Reduce from TEXT to varchar(255) since token won't be stored
