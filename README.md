##  Executando com Docker

Este guia descreve como construir e executar a aplicação **Bat Ponto** em um container Docker localmente.

###  Pré-requisitos

* **Docker Desktop** (ou Engine) instalado e em execução.
* **Java JDK 17** instalado (necessário para compilar o `.jar` antes de criar a imagem).

###  Passo a Passo

#### 1. Build da Aplicação (Gerar o .jar)
Antes de criar a imagem, é necessário compilar o código fonte. Na raiz do projeto, execute:

```powershell
.\mvnw clean package
```

#### 2. Criar a Imagem Docker
Com o artefato gerado, construa a imagem do container com a tag bat-ponto:
```powershell
docker build -t bat-ponto .
```

### 3. Rodar o Container
Inicie a aplicação mapeando a porta 8080 do seu computador para a porta do container:
```powershell
docker run -p 8080:8080 bat-ponto
```
A aplicação estará disponível em: http://localhost:8080


OBS: Ainda é possível utilizar via docker-compose com base no docker-compose.yml com o comando
```powershell
docker-compose up --build
```