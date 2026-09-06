-- Migration: enlarge token to TEXT and invited_email to varchar(254)
-- Use with Flyway or run manually against the database.

ALTER TABLE organisation_invitations
    ALTER COLUMN token TYPE text;

ALTER TABLE organisation_invitations
    ALTER COLUMN invited_email TYPE varchar(254);

-- If using PostgreSQL and the column has NOT NULL or UNIQUE constraints already, the ALTER TYPE should keep them intact.
