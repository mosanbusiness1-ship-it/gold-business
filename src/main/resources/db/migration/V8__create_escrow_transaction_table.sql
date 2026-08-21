-- V8__create_escrow_transaction_table.sql
-- Create escrow_transaction table to hold funds during sales

CREATE TABLE escrow_transaction (
  id BIGSERIAL PRIMARY KEY,
  organisation_id bigint NOT NULL REFERENCES organisations(id),
  product_id bigint,
  amount numeric(12,2) NOT NULL,
  status varchar(20),
  metadata text,
  created_at timestamp without time zone default now(),
  released_at timestamp without time zone
);

CREATE INDEX idx_escrow_org ON escrow_transaction(organisation_id);
CREATE INDEX idx_escrow_status ON escrow_transaction(status);
