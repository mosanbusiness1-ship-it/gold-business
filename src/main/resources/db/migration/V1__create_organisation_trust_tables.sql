-- Flyway migration: create organisation trust tables

CREATE TABLE IF NOT EXISTS organisation_product_review (
  organisation_id bigint NOT NULL,
  product_id bigint NOT NULL,
  org_score integer NOT NULL,
  comment text,
  moderator_user_id bigint,
  assigned_at timestamp,
  PRIMARY KEY (organisation_id, product_id)
);

CREATE TABLE IF NOT EXISTS organisation_product_meta (
  organisation_id bigint NOT NULL,
  product_id bigint NOT NULL,
  org_score integer,
  customer_average_score numeric(3,2),
  customer_review_count integer,
  commission_percent numeric(5,2),
  updated_at timestamp,
  PRIMARY KEY (organisation_id, product_id)
);

CREATE TABLE IF NOT EXISTS organisation_rating_summary (
  organisation_id bigint PRIMARY KEY,
  average_org_score double precision,
  average_customer_score double precision,
  total_products_scored integer,
  updated_at timestamp
);

-- Optional indexes
CREATE INDEX IF NOT EXISTS idx_org_product_review_org ON organisation_product_review (organisation_id);
CREATE INDEX IF NOT EXISTS idx_org_product_meta_org ON organisation_product_meta (organisation_id);
