ALTER TABLE tb_users
    ADD COLUMN IF NOT EXISTS email VARCHAR(255);

UPDATE tb_users
SET email = LOWER(email)
WHERE email IS NOT NULL;

ALTER TABLE tb_users
    ALTER COLUMN email SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_tb_users_email
    ON tb_users (email);

ALTER TABLE tb_users
    ADD CONSTRAINT ck_tb_users_email_lowercase
        CHECK (email = LOWER(email));
