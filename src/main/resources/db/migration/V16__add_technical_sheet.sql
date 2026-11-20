ALTER TABLE abstract_product ADD COLUMN IF NOT EXISTS technical_sheet TEXT;
ALTER TABLE abstract_user_need ADD COLUMN IF NOT EXISTS technical_sheet TEXT;
ALTER TABLE cautions ADD COLUMN IF NOT EXISTS technical_sheet TEXT;
