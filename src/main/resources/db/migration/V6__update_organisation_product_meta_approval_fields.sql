-- Flyway migration: update organisation_product_meta table with approval fields

ALTER TABLE organisation_product_meta ADD COLUMN IF NOT EXISTS approval_status VARCHAR(50);
ALTER TABLE organisation_product_meta ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMP;
ALTER TABLE organisation_product_meta ADD COLUMN IF NOT EXISTS validated_at TIMESTAMP;
ALTER TABLE organisation_product_meta ADD COLUMN IF NOT EXISTS validated_by_user_id BIGINT;
ALTER TABLE organisation_product_meta ADD COLUMN IF NOT EXISTS validation_comments TEXT;

-- Add foreign key for validated_by_user_id
ALTER TABLE organisation_product_meta 
  ADD CONSTRAINT fk_org_product_meta_validator 
    FOREIGN KEY (validated_by_user_id) 
    REFERENCES "user"(id) ON DELETE SET NULL;

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_org_product_meta_approval_status ON organisation_product_meta(approval_status);
CREATE INDEX IF NOT EXISTS idx_org_product_meta_validated_by ON organisation_product_meta(validated_by_user_id);
