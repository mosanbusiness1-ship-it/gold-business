-- V7__add_sla_tracking_to_organisation_product_meta.sql
-- Add SLA tracking fields to organisation_product_meta for Phase 2 moderation workflow

ALTER TABLE organisation_product_meta
ADD COLUMN sla_minutes_elapsed BIGINT,
ADD COLUMN sla_exceeded BOOLEAN DEFAULT FALSE;

-- Create index on sla_exceeded for moderation queue queries
CREATE INDEX idx_org_prod_meta_sla_exceeded ON organisation_product_meta(sla_exceeded)
WHERE sla_exceeded = TRUE;

-- Create index on submitted_at for sorting moderation queue by age
CREATE INDEX idx_org_prod_meta_submitted_at ON organisation_product_meta(submitted_at DESC)
WHERE approval_status = 'PENDING';
