"""Gera backend/src/main/resources/seed/pokedex.json a partir de Script_Pokedex.sql."""

import json
import re
from pathlib import Path

raiz = Path(__file__).resolve().parents[1]
sql = (raiz / "Script_Pokedex.sql").read_text()


def bloco(tabela):
    encontrado = re.search(rf"INSERT INTO {tabela}\b.*?VALUES\s*(.*?);", sql, re.S)
    if not encontrado:
        raise SystemExit(f"insert ausente: {tabela}")
    return encontrado.group(1)


def linhas(corpo, padrao):
    return [
        tuple(parte.replace("''", "'") if isinstance(parte, str) else parte for parte in grupos)
        for grupos in re.findall(padrao, corpo)
    ]


tipos = [(int(i), nome) for i, nome in linhas(bloco("Types"), r"\((\d+),\s*'((?:[^']|'')*)'\)")]
nome_do_tipo = dict(tipos)
pokemon_sql = linhas(
    bloco("Pokemon"),
    r"\((\d+),\s*'((?:[^']|'')*)',\s*(\d+),\s*(\d+),\s*(\d+),\s*(\d+),\s*(\d+),\s*(\d+)\)",
)
relacoes = [(int(a), int(b)) for a, b in re.findall(r"\((\d+),\s*(\d+)\)", bloco("Rel_Pokemon_Type"))]
treinadores_sql = [
    (int(i), nome, int(insignias))
    for i, nome, insignias in linhas(bloco("Trainers"), r"\((\d+),\s*'((?:[^']|'')*)',\s*(\d+)\)")
]
times = [(int(a), int(b)) for a, b in re.findall(r"\((\d+),\s*(\d+)\)", bloco("Rel_Trainer_Pokemon"))]

tipos_por_pokemon = {}
for pokemon_id, tipo_id in relacoes:
    tipos_por_pokemon.setdefault(pokemon_id, []).append({"id": tipo_id, "name": nome_do_tipo[tipo_id]})

pokemon = []
for linha in pokemon_sql:
    numero = int(linha[0])
    pokemon.append({
        "id": numero,
        "name": linha[1],
        "stats": {
            "hp": int(linha[2]),
            "attack": int(linha[3]),
            "defense": int(linha[4]),
            "specialAttack": int(linha[5]),
            "specialDefense": int(linha[6]),
            "speed": int(linha[7]),
        },
        "types": tipos_por_pokemon.get(numero, []),
    })

por_treinador = {}
for treinador_id, pokemon_id in times:
    por_treinador.setdefault(treinador_id, []).append(pokemon_id)

saida = {
    "types": [{"id": i, "name": nome} for i, nome in tipos],
    "pokemon": pokemon,
    "trainers": [
        {"id": i, "name": nome, "badges": insignias, "pokemonIds": por_treinador.get(i, [])}
        for i, nome, insignias in treinadores_sql
    ],
}

destino = raiz / "backend/src/main/resources/seed/pokedex.json"
destino.write_text(json.dumps(saida, ensure_ascii=False, indent=2) + "\n")
print(f"{len(saida['pokemon'])} pokémon, {len(saida['types'])} tipos, {len(saida['trainers'])} treinadores")
