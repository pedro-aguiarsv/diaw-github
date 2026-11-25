# Atividade 04 — Pokédex com Spring Boot e MongoDB

A Pokédex da Atividade 03 deixa de ler `pokedex-data.json`. O React consulta uma API REST em Spring Boot, e os documentos ficam no MongoDB.

O arquivo `Script_Pokedex.sql` é a fonte dos dados. Na primeira subida, com a coleção vazia, o backend grava os 151 Pokémon, os tipos e os treinadores a partir de `backend/src/main/resources/seed/pokedex.json`, gerado desse script.

## Arquitetura

React + Vite → Spring Boot → MongoDB

## Como executar

É preciso ter JDK 25 (ou mais novo), Maven, Node.js e Docker.

```bash
cd atividade04
docker-compose up -d
cd backend
mvn spring-boot:run
```

Em outro terminal:

```bash
cd atividade04/frontend
npm install
npm run dev
```

A interface abre em http://localhost:5173. A API fica em http://127.0.0.1:8081.

Para usar o MongoDB Atlas, exporte a URI antes de subir o backend. Não grave usuário nem senha no repositório.

```bash
export MONGODB_URI='mongodb+srv://USUARIO:SENHA@cluster.mongodb.net/pokedex?retryWrites=true&w=majority'
```

O modelo está em `.env.example`. O Spring Boot não lê esse arquivo sozinho: a variável precisa estar no ambiente.

## API

| Método | Caminho | Uso |
| --- | --- | --- |
| GET | `/api/pokemons` | Lista. Aceita `nome` e `tipo` |
| GET | `/api/pokemons/{id}` | Um Pokémon |
| POST | `/api/pokemons` | Cadastra |
| PUT | `/api/pokemons/{id}` | Edita |
| DELETE | `/api/pokemons/{id}` | Exclui |
| GET | `/api/types` | Tipos |
| GET | `/api/trainers` | Treinadores e os Pokémon de cada um |

Exemplo de documento na coleção `pokemons`:

```json
{
  "id": 6,
  "name": "Charizard",
  "stats": {
    "hp": 78,
    "attack": 84,
    "defense": 78,
    "specialAttack": 109,
    "specialDefense": 85,
    "speed": 100
  },
  "types": [
    { "id": 2, "name": "Fire" },
    { "id": 10, "name": "Flying" }
  ]
}
```

## Interface

A tela continua com busca, filtro por tipo, ficha, atributos, favoritos e treinadores. A busca e o filtro de tipo vão para a API. O cadastro, a edição e a exclusão também passam por ela.

Pedro Aguiar — DIAW, PUC Minas.
