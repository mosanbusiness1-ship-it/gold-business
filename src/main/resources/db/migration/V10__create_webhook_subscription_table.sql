-- V10__create_webhook_subscription_table.sql
-- Table to store webhook subscriptions per organisation

CREATE TABLE webhook_subscription (
  id BIGSERIAL PRIMARY KEY,
  organisation_id bigint REFERENCES organisations(id),
  url varchar(2048) NOT NULL,
  event_types varchar(255),
  created_at timestamp without time zone default now(),
  active boolean default true
);

CREATE INDEX idx_webhook_org ON webhook_subscription(organisation_id);
