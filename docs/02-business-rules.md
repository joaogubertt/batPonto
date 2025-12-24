# Regras de Negócio

## Tipos de registro
Cada batida de ponto é um evento:
- ENTRADA
- SAIDA

## Regras básicas
- RN01: O horário do registro é definido pelo servidor (não pelo cliente).
- RN02: O FUNCIONARIO só pode registrar ponto para si mesmo.
- RN03: O FUNCIONARIO só pode consultar relatórios do próprio usuário.
- RN04: O RH pode consultar relatórios de qualquer usuário.
- RN05: Não permitir dois registros consecutivos do mesmo tipo (ex: ENTRADA seguida de ENTRADA).
- RN06: Caso não exista registro anterior, o primeiro do dia pode ser ENTRADA (ou o primeiro geral — definido pelo sistema).

## Observações do MVP
- O relatório do MVP é uma lista de eventos (timestamp + tipo).
  - Total de horas por dia é será implementado em breve
