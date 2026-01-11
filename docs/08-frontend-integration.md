# Integração com Frontend

Este guia descreve como integrar o **frontend** (em outro repositório) com o **backend do BatPonto**, configuração por ambiente e contrato de API.

---

## Objetivo

- Permitir que o frontend consuma a API com segurança (JWT)
- Garantir que o browser consiga acessar a API (CORS)
- Padronizar variáveis de ambiente e endpoints
- Documentar exemplos práticos de requests (JSON e PDF)

---

## Repositórios

- **Backend**: BatPonto API (este repositório)
- **Frontend**: [BatPonto Frontend](https://github.com/joaogubertt/batPonto-frontend)


> Como são repositórios separados, a integração é feita via:
- URL da API (baseUrl)
- Autenticação JWT (Bearer token)
- CORS configurado no backend

---
## Variáveis de ambiente (Frontend)

O frontend deve ler a URL do backend por variável de ambiente.

### Exemplo (Vite)
Arquivo `.env.example` no frontend:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Uso no código:

```ts
const baseUrl = import.meta.env.VITE_API_BASE_URL;
```

### Exemplo (Next.js)
Arquivo `.env.example` no frontend:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

---

## CORS (Backend)

Como o frontend e o backend rodam em origens diferentes (portas/domínios), o backend precisa permitir CORS.

### Configuração recomendada

No `application.properties` (dev):

```properties
app.cors.allowed-origins=http://localhost:5173
```

### Headers importantes
- `Authorization` (Bearer token)
- `Content-Disposition` (para o frontend conseguir ler nome do arquivo PDF)
- `Content-Type`

---

## Autenticação (JWT)

### Login
**POST** `/api/auth/login`

Request:
```json
{
  "email": "funcionario@empresa.com",
  "password": "senha123"
}
```

Response (exemplo):
```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "name": "Funcionario",
    "role": "FUNCIONARIO"
  }
}
```

### Uso do token no frontend
Para endpoints protegidos, enviar:

```http
Authorization: Bearer <jwt>
```

---

## Endpoints do MVP (Backend)

### Autenticação
- `POST /api/auth/login`

### Registro de ponto (FUNCIONARIO)
- `POST /api/time-entries`

### Relatórios (JSON)
- `GET /api/time-entries/my?from=YYYY-MM-DD&to=YYYY-MM-DD` (**FUNCIONARIO**)
- `GET /api/time-entries/user/{userId}?from=YYYY-MM-DD&to=YYYY-MM-DD` (**RH**)

### Relatórios (PDF)
- `GET /api/time-entries/my/pdf?from=YYYY-MM-DD&to=YYYY-MM-DD` (**FUNCIONARIO**)
- `GET /api/time-entries/user/{userId}/pdf?from=YYYY-MM-DD&to=YYYY-MM-DD` (**RH**)

---

## Consumindo PDF no Frontend

### Opção A — Abrir em nova aba (simples)
```ts
window.open(`${baseUrl}/api/time-entries/my/pdf?from=2025-12-01&to=2025-12-31`, "_blank");
```

> Observação: essa opção depende do token. Para token Bearer, normalmente você precisa buscar com `fetch` e abrir via blob (opção B).

### Opção B — Baixar via fetch (recomendado)
```ts
const res = await fetch(`${baseUrl}/api/time-entries/my/pdf?from=2025-12-01&to=2025-12-31`, {
  headers: { Authorization: `Bearer ${token}` }
});

if (!res.ok) {
  const err = await res.json();
  throw new Error(err.message ?? "Falha ao gerar PDF");
}

const blob = await res.blob();
const url = window.URL.createObjectURL(blob);

const a = document.createElement("a");
a.href = url;
a.download = "relatorio-ponto.pdf";
a.click();

window.URL.revokeObjectURL(url);
```

---

## Erros padronizados (JSON)

Quando a API retorna erro, o frontend pode receber algo como:

```json
{
  "timestamp": "2026-01-03T18:04:53Z",
  "status": 422,
  "error": "INVALID_SEQUENCE",
  "message": "Não é permitido registrar ENTRADA duas vezes seguidas",
  "path": "/api/time-entries"
}
```

Recomendação no frontend:
- Exibir `message` ao usuário
- Logar `error` para debug
- Tratar `401` (não autenticado) redirecionando para login

---

## Checklist de integração

- [ ] Frontend possui `API_BASE_URL` configurado por ambiente
- [ ] Backend está com CORS habilitado para a origem do frontend
- [ ] Login retorna `accessToken`
- [ ] Frontend envia `Authorization: Bearer <token>` nos endpoints protegidos
- [ ] Relatórios JSON retornam 200
- [ ] Relatórios PDF retornam `application/pdf`
- [ ] Header `Content-Disposition` está presente e exposto via CORS (se necessário)

---
