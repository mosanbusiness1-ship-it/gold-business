ALTER TABLE auction_bids ADD COLUMN IF NOT EXISTS organisation_id BIGINT;
ALTER TABLE product_transactions ADD COLUMN IF NOT EXISTS organisation_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_auction_bid_organisation'
    ) THEN
        ALTER TABLE auction_bids
            ADD CONSTRAINT fk_auction_bid_organisation
            FOREIGN KEY (organisation_id) REFERENCES organisation(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_transaction_organisation'
    ) THEN
        ALTER TABLE product_transactions
            ADD CONSTRAINT fk_product_transaction_organisation
            FOREIGN KEY (organisation_id) REFERENCES organisation(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_auction_bids_organisation ON auction_bids(organisation_id);
CREATE INDEX IF NOT EXISTS idx_product_transactions_organisation ON product_transactions(organisation_id);

CREATE TABLE IF NOT EXISTS need_offers (
    id BIGSERIAL PRIMARY KEY,
    need_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    offerer_id BIGINT NOT NULL,
    organisation_id BIGINT NOT NULL,
    message VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_need_offer_need FOREIGN KEY (need_id) REFERENCES abstract_user_need(id),
    CONSTRAINT fk_need_offer_product FOREIGN KEY (product_id) REFERENCES abstract_product(id),
    CONSTRAINT fk_need_offer_offerer FOREIGN KEY (offerer_id) REFERENCES users(id),
    CONSTRAINT fk_need_offer_organisation FOREIGN KEY (organisation_id) REFERENCES organisation(id)
);

CREATE INDEX IF NOT EXISTS idx_need_offers_need ON need_offers(need_id);
CREATE INDEX IF NOT EXISTS idx_need_offers_organisation ON need_offers(organisation_id);
