# Modelo de Dados

## Tabela: users
- id (UUID, PK)
- name (varchar, not null)
- email (varchar, unique, not null)
- password_hash (varchar, not null)
- role (varchar, not null) // FUNCIONARIO ou RH
- created_at (timestamp, not null)

Índices:
- unique(email)

## Tabela: time_entries
- id (UUID, PK)
- user_id (UUID, FK -> users.id, not null)
- entry_type (varchar, not null) // ENTRADA ou SAIDA
- timestamp (timestamp, not null) // horário do servidor
- created_at (timestamp, not null)

Índices:
- (user_id, timestamp)

Relacionamentos:
- users 1 -> N time_entries
