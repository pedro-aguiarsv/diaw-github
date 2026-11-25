export const typeColor = {
  Typeless: '#8d8d8d',
  Normal: '#a8a77a',
  Fire: '#ee8130',
  Water: '#6390f0',
  Grass: '#7ac74c',
  Electric: '#f7d02c',
  Ice: '#96d9d6',
  Fighting: '#c22e28',
  Poison: '#a33ea1',
  Ground: '#e2bf65',
  Flying: '#a98ff3',
  Psychic: '#f95587',
  Bug: '#a6b91a',
  Rock: '#b6a136',
  Ghost: '#735797',
  Dark: '#705746',
  Dragon: '#6f35fc',
  Steel: '#b7b7ce',
}

export const statFields = [
  { key: 'hp', label: 'HP' },
  { key: 'attack', label: 'Ataque' },
  { key: 'defense', label: 'Defesa' },
  { key: 'specialAttack', label: 'Ataque Especial' },
  { key: 'specialDefense', label: 'Defesa Especial' },
  { key: 'speed', label: 'Velocidade' },
]

export function padId(id) {
  return String(id).padStart(3, '0')
}

export function artworkUrl(id) {
  return `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png`
}

export function spriteUrl(id) {
  return `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png`
}

export function pokemonTypes(pokemon) {
  if (!pokemon.types || pokemon.types.length === 0) {
    return [{ id: 0, name: 'Typeless' }]
  }
  return pokemon.types
}

export function typesInUse(pokemon, types) {
  const used = new Set(pokemon.flatMap((item) => pokemonTypes(item).map((type) => type.name)))
  return types.filter((type) => used.has(type.name))
}

export function filterPokemon(list, { query, typeName, favoritesOnly, favoriteIds, sort }) {
  const q = query.trim().toLowerCase().replace(/^#/, '')

  const result = list.filter((pokemon) => {
    if (favoritesOnly && !favoriteIds.includes(pokemon.id)) {
      return false
    }
    if (typeName !== 'todos' && !pokemonTypes(pokemon).some((type) => type.name === typeName)) {
      return false
    }
    if (!q) {
      return true
    }
    return (
      pokemon.name.toLowerCase().includes(q) ||
      String(pokemon.id).includes(q) ||
      padId(pokemon.id).includes(q)
    )
  })

  result.sort((a, b) => {
    if (sort === 'name') {
      return a.name.localeCompare(b.name)
    }
    if (sort === 'id') {
      return a.id - b.id
    }
    return b.stats[sort] - a.stats[sort]
  })

  return result
}
