# Norte

> Sistema de aluguel de carros do Laboratório 02 de DIAW. O cliente pede o carro. O agente analisa a renda. O contrato só começa depois do parecer.

![Norte](frontend/public/favicon.svg)

---

## Status do Projeto

![Versão](https://img.shields.io/badge/Versão-v1.0.0-blue)
![Java](https://img.shields.io/badge/Java-25-007ec6?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.9-6db33f?logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-007ec6?logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-7-646cff?logo=vite&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-336791?logo=postgresql&logoColor=white)

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades Principais](#funcionalidades-principais)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Instalação e Execução](#instalação-e-execução)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Demonstração](#demonstração)
- [Testes](#testes)
- [Documentações utilizadas](#documentações-utilizadas)
- [Autores](#autores)
- [Licença](#licença)

---

## Sobre o Projeto

A Norte existe para o Laboratório 02 da disciplina Desenvolvimento e Integração de Aplicações Web. O enunciado pede um sistema em que clientes introduzem, alteram, consultam e cancelam pedidos de aluguel, e agentes (empresas e bancos) modificam e avaliam esses pedidos. Depois de um parecer positivo, o contrato pode ser executado e, se houver um banco, associado a um crédito.

O uso começa no cadastro e no login. Sem autenticação, a API não abre.

---

## Funcionalidades Principais

- Login e cadastro de cliente e de agente (empresa ou banco).
- CRUD de cliente: RG, CPF, nome, endereço, profissão e até três empregadores com rendimento.
- Registro de automóvel com matrícula, ano, marca, modelo e placa, como propriedade de cliente, empresa ou banco.
- Pedido de aluguel com status visível: introduzido, em análise, parecer positivo, parecer negativo, contrato em execução ou cancelado.
- Avaliação financeira pelo agente e contrato de crédito concedido por um banco.

---

## Tecnologias Utilizadas

### Front-end

* React 19
* Vite 7
* CSS próprio

### Back-end

* Java 25
* Spring Boot 4.0.9
* Spring MVC, Spring Security e Spring Data JPA
* Maven

### Banco

* PostgreSQL no Supabase
* O mesmo código sobe com o PostgreSQL do `docker-compose.yml` quando as variáveis do Supabase não estão definidas

---

## Arquitetura

Dois subsistemas, como o enunciado separa.

O de pedidos e contratos é o back-end MVC: `controller` recebe HTTP, `service` aplica a regra, `repository` fala com o banco e `model` guarda as entidades. A senha fica com hash BCrypt. A sessão fica no cookie depois do login.

O de páginas é o React. Ele não acessa o banco. As telas pedem `/api` e o Vite encaminha isso para a porta 8080.

Os diagramas estão em [`modelos`](modelos/diagramas.md). A revisão entre a leitura do enunciado e o código está em [`modelos/revisao.md`](modelos/revisao.md).

---

## Instalação e Execução

### Pré-requisitos

* JDK 25 (o projeto compila com `--release 25`)
* Maven 3.9
* Node.js 22
* Docker, se for usar o PostgreSQL local
* Um projeto no [Supabase](https://supabase.com/docs) para a entrega com o banco da disciplina

### Variáveis de Ambiente

Sem variáveis, o back-end usa `jdbc:postgresql://localhost:5433/aluguel`, usuário `aluguel` e senha `aluguel`. A porta 5433 evita conflito com um PostgreSQL que já esteja na 5432.

Para o Supabase:

```bash
export SUPABASE_DB_URL='jdbc:postgresql://db.SEU_PROJETO.supabase.co:5432/postgres?sslmode=require'
export SUPABASE_DB_USER='postgres'
export SUPABASE_DB_PASSWORD='senha-do-banco'
```

O Hibernate cria e atualiza as tabelas (`ddl-auto=update`). O arquivo [`supabase/schema.sql`](supabase/schema.sql) descreve a mesma modelagem, para consulta no SQL Editor.

### Banco local

Na pasta `laboratorio02`:

```bash
docker compose up -d
```

Se o comando `docker compose` não existir, use `docker-compose up -d`.

### Back-end

```bash
cd backend
mvn spring-boot:run
```

A API fica em `http://localhost:8080`.

### Front-end

```bash
cd frontend
npm install
npm run dev
```

Abra `http://localhost:5173`.

### Contas de demonstração

| Papel | Usuário | Senha |
| --- | --- | --- |
| Cliente | ana | ana123 |
| Empresa | empresa | empresa123 |
| Banco | banco | banco123 |

A Ana já tem cadastro, um Tracker e um pedido introduzido do Compass.

---

## Estrutura de Pastas

```text
laboratorio02/
├── backend/          Spring Boot MVC
├── frontend/         React + Vite
├── modelos/          casos de uso, classes, pacotes e histórias
├── supabase/         schema SQL
├── docker-compose.yml
└── README.md
```

---

## Demonstração

1. Entre como `ana` e abra o pedido do Compass.
2. Saia e entre como `empresa`. Assuma a análise e dê parecer positivo.
3. Saia e entre como `banco`. Associe o contrato de crédito.
4. Volte como `ana` e veja o status **Contrato em execução**.

Não há deploy. A execução prevista é local, com Supabase ou com o PostgreSQL do Docker.

---

## Testes

```bash
cd backend
mvn test
```

`CpfPeriodoTest` cobre CPF e contagem de dias. `FluxoAluguelTest` percorre cadastro de cliente, automóvel, pedido, parecer positivo e contrato de crédito, em H2 no perfil `test`.

---

## Documentações utilizadas

* [Spring Boot 4.0](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now)
* [Supabase — Postgres](https://supabase.com/docs/guides/database/overview)
* [React](https://react.dev/)
* [Vite](https://vite.dev/)
* [Template de README da disciplina](https://github.com/joaopauloaramuni/desenvolvimento-e-integracao-de-aplicacoes-web/blob/main/TEMPLATES/template_README.md)

---

## Autores

* Pedro Aguiar

---

## Licença

Uso acadêmico na disciplina DIAW, PUC Minas, 2026.2.
