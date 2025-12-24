# Segurança

## Autenticação
- JWT (Bearer Token)
- Login em /api/auth/login

## Autorização (roles)
- FUNCIONARIO:
    - POST /api/time-entries
    - GET /api/time-entries/my
- RH:
    - GET /api/time-entries/user/{userId}

## Regras importantes
- O backend deve extrair o usuário do JWT (subject / userId).
- O backend nunca deve confiar no userId vindo do client para ações do FUNCIONARIO.
- Senhas armazenadas em Hash.

## Expiração e Refresh
MVP:
- Somente access token com expiração (ex: 1h). 

Versão futura:
  - refresh token e blacklist/rotacionamento.
