-- Flyway migration: create guarantee_policy table

CREATE TABLE IF NOT EXISTS guarantee_policy (
  id BIGSERIAL PRIMARY KEY,
  organisation_id BIGINT NOT NULL,
  duration_months INTEGER,
  cost NUMERIC(10, 2),
  coverage TEXT,
  conditions TEXT,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (organisation_id) REFERENCES organisation(id) ON DELETE CASCADE
);

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_guarantee_policy_org_id ON guarantee_policy(organisation_id);
CREATE INDEX IF NOT EXISTS idx_guarantee_policy_active ON guarantee_policy(active);
