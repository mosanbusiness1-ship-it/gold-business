-- Flyway migration: add organisation status and types columns

-- Add new columns to organisation table
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS commission_on_publish NUMERIC(5, 2);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS commission_on_sale NUMERIC(5, 2);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS commission_mode VARCHAR(20);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS offers_guarantee BOOLEAN DEFAULT FALSE;
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS country VARCHAR(255);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS latitude NUMERIC(10, 8);
ALTER TABLE organisation ADD COLUMN IF NOT EXISTS longitude NUMERIC(11, 8);

-- Create tables for supported types (many-to-many)
CREATE TABLE IF NOT EXISTS organisation_supported_product_types (
  organisation_id BIGINT NOT NULL,
  supported_product_types VARCHAR(50) NOT NULL,
  PRIMARY KEY (organisation_id, supported_product_types)
);

CREATE TABLE IF NOT EXISTS organisation_supported_need_types (
  organisation_id BIGINT NOT NULL,
  supported_need_types VARCHAR(50) NOT NULL,
  PRIMARY KEY (organisation_id, supported_need_types)
);

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_org_status ON organisation(status);
CREATE INDEX IF NOT EXISTS idx_org_commission_mode ON organisation(commission_mode);
