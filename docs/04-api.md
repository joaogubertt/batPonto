# API (Contrato do MVP)

## Convenções
- Base URL: /api
- Autenticação: Bearer JWT no header:
    - Authorization: Bearer <token>
- Datas em UTC ISO-8601 (ex: 2025-12-23T12:30:00Z) ou timestamp com timezone.

## Auth

### POST /api/auth/login
Autentica usuário e retorna JWT.

Request:
{
"email": "fulano@empresa.com",
"password": "senha"
}

Response 200:
{
"accessToken": "<jwt>",
"tokenType": "Bearer",
"expiresIn": 3600,
"user": {
"id": "uuid",
"name": "Fulano",
"role": "FUNCIONARIO"
}
}

Erros:
- 401 INVALID_CREDENTIALS

---

## Registro de ponto

### POST /api/time-entries
Permissões: FUNCIONARIO

Request:
{
"type": "ENTRADA" | "SAIDA"
}

Response 201:
{
"id": "uuid",
"userId": "uuid",
"type": "ENTRADA",
"timestamp": "2025-12-23T11:58:00Z"
}

Erros:
- 401 UNAUTHORIZED
- 403 FORBIDDEN
- 422 BUSINESS_RULE_VIOLATION (ex: tipo repetido)

---

## Relatórios

### GET /api/time-entries/my?from=2025-12-01&to=2025-12-31
Permissões: FUNCIONARIO

Response 200:
{
"userId": "uuid",
"from": "2025-12-01",
"to": "2025-12-31",
"entries": [
{ "id": "uuid", "type": "ENTRADA", "timestamp": "2025-12-01T11:00:00Z" },
{ "id": "uuid", "type": "SAIDA", "timestamp": "2025-12-01T20:00:00Z" }
]
}

---

### GET /api/time-entries/user/{userId}?from=2025-12-01&to=2025-12-31
Permissões: RH

Response 200: (mesmo formato do /my)

Erros:
- 401 UNAUTHORIZED
- 403 FORBIDDEN
- 404 USER_NOT_FOUND
