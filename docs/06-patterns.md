# Padrões do Projeto

## Arquitetura
Controller -> Service -> Repository -> Entity

- Controller: valida request, chama service, retorna response
- Service: regras de negócio e autorização
- Repository: acesso ao banco (JPA)
- Entity: modelo persistido

## Padrão de erro (JSON)
Sempre retornar:
{
"timestamp": "2025-12-23T12:00:00Z",
"status": 422,
"error": "BUSINESS_RULE_VIOLATION",
"message": "Não é permitido registrar ENTRADA duas vezes seguidas.",
"path": "/api/time-entries"
}

## Convenções
- Nomes de classes em inglês
- Endpoints no plural
- DTOs separados de Entities
