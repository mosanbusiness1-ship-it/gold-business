-- Flyway migration: create commission_transaction table

CREATE TABLE IF NOT EXISTS commission_transaction (
  id BIGSERIAL PRIMARY KEY,
  organisation_id BIGINT NOT NULL,
  product_id BIGINT,
  type VARCHAR(50),
  amount NUMERIC(10, 2),
  status VARCHAR(50),
  transaction_ref VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (organisation_id) REFERENCES organisation(id) ON DELETE CASCADE
);

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_commission_org_id ON commission_transaction(organisation_id);
CREATE INDEX IF NOT EXISTS idx_commission_type ON commission_transaction(type);
CREATE INDEX IF NOT EXISTS idx_commission_status ON commission_transaction(status);
CREATE INDEX IF NOT EXISTS idx_commission_product_id ON commission_transaction(product_id);
CREATE INDEX IF NOT EXISTS idx_commission_created_at ON commission_transaction(created_at);
