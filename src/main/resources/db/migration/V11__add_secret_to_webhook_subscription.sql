-- V11__add_secret_to_webhook_subscription.sql
-- Add support for HMAC secrets on webhook subscriptions

ALTER TABLE webhook_subscription
ADD COLUMN secret varchar(255);
