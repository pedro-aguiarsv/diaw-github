# Pokédex

Pokédex de Kanto feita com **React + Vite** para a disciplina de Desenvolvimento e Integração de Aplicações Web (DIAW), Atividade 03.

A aplicação lista os 151 Pokémon do arquivo da disciplina, permite pesquisar por nome ou número, filtrar por tipo e abrir os atributos de cada um. Também há uma área com os treinadores e os Pokémon ligados a cada um.

---

## Integrantes

- Pedro Aguiar

---

## Como executar

Na pasta do projeto:

```bash
cd atividade03/pokedex
npm install
npm run dev
```

Abra [http://localhost:5173](http://localhost:5173).

---

## Dados

Os dados ficam em `src/data/pokedex-data.json` (Pokémon, tipos e treinadores). A Pokédex funciona com esse arquivo, sem banco de dados.

As imagens vêm da [PokéAPI](https://pokeapi.co/), no repositório de sprites:

`https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{id}.png`

Se a arte oficial não carregar, a tela usa o sprite clássico do mesmo repositório.

---

## O que a tela faz

- Lista os Pokémon com número, nome, tipos e atributos (HP, ataque, defesa, ataque especial, defesa especial e velocidade).
- Pesquisa por nome ou número enquanto o usuário digita.
- Filtra por tipo.
- Mostra o Pokémon selecionado em destaque, com barras de atributo.
- Avisa quando a busca não encontra ninguém.
- Mostra os treinadores, a quantidade de insígnias e os Pokémon de cada um.
- Favoritos (salvos no navegador), ordenação e um botão para escolher um Pokémon aleatório.

---

## Estrutura

```
pokedex/
├── package.json
├── vite.config.js
├── README.md
└── src/
    ├── App.jsx
    ├── main.jsx
    ├── index.css
    ├── css/app.css
    ├── data/pokedex-data.json
    ├── utils/pokemon.js
    └── components/
        ├── Header.jsx
        ├── SearchBar.jsx
        ├── TypeFilters.jsx
        ├── PokemonList.jsx
        ├── PokemonCard.jsx
        ├── PokemonDetails.jsx
        ├── TrainerPanel.jsx
        └── StatBar.jsx
```

O `App` guarda a pesquisa, o tipo, a ordenação, o Pokémon selecionado e a troca entre a Pokédex e os treinadores. Os componentes recebem esses dados por props.
