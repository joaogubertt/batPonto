CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tb_time_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    entry_type VARCHAR(50) NOT NULL,
    entry_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

   CONSTRAINT fk_time_entries_user
       FOREIGN KEY (user_id)
           REFERENCES tb_users(id)
           ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_time_entries_user_id ON tb_time_entries(user_id);
CREATE INDEX IF NOT EXISTS idx_time_entries_entry_at ON tb_time_entries(entry_at);

