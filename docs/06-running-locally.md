# Como rodar o projeto localmente (Backend)

Este documento descreve como subir o ambiente local do **BatPonto (Backend)** utilizando **Docker**, **PostgreSQL**, **Flyway** e **Spring Boot**.

---

## Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- Docker
- Docker Compose
- Java 17+
- Maven

---

## Variáveis de ambiente

Na raiz do projeto, crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

Exemplo de conteúdo:

```env
POSTGRES_DB=batponto
POSTGRES_USER=batponto
POSTGRES_PASSWORD=batponto
POSTGRES_PORT=5433
```

> Ajuste os valores conforme necessário para seu ambiente local.

---

## Subindo o banco de dados e migrations

O projeto utiliza **PostgreSQL** com **Flyway** para controle de migrations.

### Subir PostgreSQL + Flyway

```bash
docker compose up -d postgres flyway
```

Esse comando irá:

1. Subir o container do PostgreSQL
2. Aguardar o banco ficar saudável
3. Executar automaticamente as migrations via Flyway

---

## Verificando saúde do banco

### Ver containers ativos

```bash
docker compose ps
```

Saída esperada (exemplo):

```text
NAME              STATUS
ponto-postgres    Up (healthy)
ponto-flyway      Exited (0)
```

> O container do Flyway finaliza após aplicar as migrations com sucesso.

### Ver logs do Flyway (opcional)

```bash
docker compose logs flyway
```

---

## Rodando a API

Com o banco já disponível, inicie a aplicação Spring Boot:

```bash
./mvnw spring-boot:run
```

Ou:

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```
http://localhost:8080
```

---

## Testando com Postman (exemplos)

### Login

POST `/api/auth/login`

```json
{
  "email": "funcionario@empresa.com",
  "password": "senha123"
}
```

---

### Registrar ponto

POST `/api/time-entries`

Header:
```
Authorization: Bearer <jwt>
```

Body:
```json
{
  "type": "ENTRADA"
}
```

---

### Relatório (JSON)

GET `/api/time-entries/my?from=2025-12-01&to=2025-12-31`

---

### Relatório (PDF)

GET `/api/time-entries/my/pdf?from=2025-12-01&to=2025-12-31`

---

## Encerrando o ambiente

```bash
docker compose down
```

Para remover volumes:

```bash
docker compose down -v
```
