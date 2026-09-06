-- Add token_hash column and backfill using pgcrypto if available
ALTER TABLE organisation_invitations
    ADD COLUMN token_hash varchar(64);

-- If pgcrypto is available on the server, compute SHA-256 hex and populate token_hash
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pgcrypto') THEN
        EXECUTE 'UPDATE organisation_invitations SET token_hash = encode(digest(token, ''sha256''), ''hex'')';
    END IF;
EXCEPTION WHEN undefined_table THEN
    -- ignore if table doesn't exist
END$$;

-- Create unique index on token_hash
CREATE UNIQUE INDEX IF NOT EXISTS idx_org_inv_token_hash_unique ON organisation_invitations(token_hash);
