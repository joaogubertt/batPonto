# 🦇 BatPonto — MVP (Backend)

Aplicação Web empresarial para **Registro de Ponto**, com controle de acesso baseado em perfis de usuário.

---

## 👥 Perfis de Usuário

- **FUNCIONÁRIO**
    - Registro de ponto
    - Consulta do próprio relatório
- **RH**
    - Consulta relatórios de qualquer funcionário
    - Cadastro de funcionários
- **SUPERADMIN**
    - Todas as funcionalidades do RH
    - Gestão de usuários do sistema
---

## ▶️ Como rodar o projeto (local)

### 📋 Pré-requisitos

Certifique-se de ter instalado:

- Docker
- Docker Compose
- Java **17+**
- Maven

---

### 🗄️ Subir banco de dados + migrations

Execute o comando abaixo para subir o banco PostgreSQL e aplicar as migrations com Flyway:

```bash
docker compose up -d postgres flyway
```
### 🚀 Subir a API
Após o banco estar em execução, inicie a aplicação Spring Boot:
```bash
./mvnw spring-boot:run
```

### ⚙️ Variáveis de Ambiente
Copie o arquivo de exemplo:
```bash
cp .env.example .env
```
Configure as variáveis conforme o seu ambiente local.

## 🔗 Endpoints (MVP)
### 🔐 Autenticação
```bash 
POST /api/auth/login
```
### 🕒 Registro de Ponto — FUNCIONÁRIO
```bash
POST /api/time-entries
```
```bash
GET /api/time-entries/my
```
```bash
GET /api/time-entries/my/pdf
```
### 📊 Relatórios — RH

```bash 
GET /api/time-entries/user/{id}
```
```bash 
GET /api/time-entries/user/{id}/pdf
```

### 📚 Documentação

A documentação adicional do projeto está disponível na pasta: `/docs`