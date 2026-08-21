-- Flyway migration: create organisation_review table

CREATE TABLE IF NOT EXISTS organisation_review (
  id BIGSERIAL PRIMARY KEY,
  organisation_id BIGINT NOT NULL,
  reviewer_user_id BIGINT NOT NULL,
  rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
  title VARCHAR(255),
  comment TEXT,
  is_verified_purchase BOOLEAN DEFAULT FALSE,
  order_id BIGINT,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (organisation_id) REFERENCES organisation(id) ON DELETE CASCADE,
  FOREIGN KEY (reviewer_user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_org_review_org_id ON organisation_review(organisation_id);
CREATE INDEX IF NOT EXISTS idx_org_review_reviewer_id ON organisation_review(reviewer_user_id);
CREATE INDEX IF NOT EXISTS idx_org_review_is_verified ON organisation_review(is_verified_purchase);
CREATE INDEX IF NOT EXISTS idx_org_review_status ON organisation_review(status);
