-- V9__create_guarantee_claim_table.sql
-- Create guarantee_claim table

CREATE TABLE guarantee_claim (
  id BIGSERIAL PRIMARY KEY,
  organisation_id bigint REFERENCES organisations(id),
  product_id bigint,
  reason text,
  created_at timestamp without time zone default now(),
  resolved boolean default false,
  resolution_notes text
);

CREATE INDEX idx_guarantee_org ON guarantee_claim(organisation_id);
