# Requisitos

## Requisitos Funcionais (RF)
- RF01: O FUNCIONARIO deve autenticar e obter um JWT.
- RF02: O FUNCIONARIO deve registrar ponto (ENTRADA ou SAIDA).
- RF03: O FUNCIONARIO deve consultar seu relatório de ponto por período.
- RF04: O RH deve consultar relatório de ponto de qualquer FUNCIONARIO por período.
- RF05: O sistema deve registrar a data/hora usando o relógio do servidor.

## Requisitos Não Funcionais (RNF)
- RNF01: O sistema deve usar PostgreSQL como banco de dados.
- RNF02: O sistema deve usar JWT para autenticação/autorização.
- RNF03: Senhas devem ser armazenadas como hash seguro.
- RNF04: Deve existir padrão de erros em JSON.
- RNF05: Logs mínimos para auditoria (login e registro de ponto).
