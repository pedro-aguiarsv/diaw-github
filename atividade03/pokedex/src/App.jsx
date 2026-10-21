import { useEffect, useMemo, useState } from 'react'
import data from './data/pokedex-data.json'
import Header from './components/Header'
import TypeFilters from './components/TypeFilters'
import PokemonList from './components/PokemonList'
import PokemonDetails from './components/PokemonDetails'
import TrainerPanel from './components/TrainerPanel'
import { filterPokemon, pokemonTypes, typeColor, typesInUse } from './utils/pokemon'

const STORAGE_KEY = 'pokedex-favoritos'

export default function App() {
  const [query, setQuery] = useState('')
  const [typeName, setTypeName] = useState('todos')
  const [sort, setSort] = useState('id')
  const [selectedId, setSelectedId] = useState(1)
  const [view, setView] = useState('dex')
  const [favoritesOnly, setFavoritesOnly] = useState(false)
  const [favoriteIds, setFavoriteIds] = useState(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(STORAGE_KEY))
      return Array.isArray(saved) ? saved : []
    } catch {
      return []
    }
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(favoriteIds))
  }, [favoriteIds])

  const types = useMemo(() => typesInUse(data.pokemon, data.types), [])

  const filtered = filterPokemon(data.pokemon, {
    query,
    typeName,
    favoritesOnly,
    favoriteIds,
    sort,
  })

  const selected = filtered.find((pokemon) => pokemon.id === selectedId) || filtered[0] || null
  const accentName = selected ? pokemonTypes(selected)[0].name : null
  const accent = typeColor[accentName] || '#f2c14e'

  function toggleFavorite(id) {
    setFavoriteIds((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    )
  }

  function pickRandom() {
    const pool = filtered.length ? filtered : data.pokemon
    const pick = pool[Math.floor(Math.random() * pool.length)]
    setSelectedId(pick.id)
    setView('dex')
  }

  function openPokemon(id) {
    setQuery('')
    setTypeName('todos')
    setFavoritesOnly(false)
    setSort('id')
    setSelectedId(id)
    setView('dex')
  }

  function clearFilters() {
    setQuery('')
    setTypeName('todos')
    setFavoritesOnly(false)
  }

  return (
    <div className={view === 'dex' ? 'app dex' : 'app trainers-view'} style={{ '--type': accent }}>
      <div className="atmosphere" />
      <Header
        view={view}
        onChangeView={setView}
        query={query}
        onQuery={setQuery}
        onRandom={pickRandom}
      />

      {view === 'dex' ? (
        <>
          <div className="toolbar">
            <TypeFilters types={types} selected={typeName} onSelect={setTypeName} />
            <div className="toolbar-end">
              <button
                type="button"
                className={favoritesOnly ? 'chip on' : 'chip'}
                onClick={() => setFavoritesOnly((current) => !current)}
              >
                Favoritos
              </button>
              <label className="sort">
                Ordenar
                <select value={sort} onChange={(event) => setSort(event.target.value)}>
                  <option value="id">Número</option>
                  <option value="name">Nome</option>
                  <option value="hp">HP</option>
                  <option value="attack">Ataque</option>
                  <option value="defense">Defesa</option>
                  <option value="specialAttack">Ataque Especial</option>
                  <option value="specialDefense">Defesa Especial</option>
                  <option value="speed">Velocidade</option>
                </select>
              </label>
            </div>
          </div>

          <main className="stage">
            {selected ? (
              <PokemonDetails
                pokemon={selected}
                favorite={favoriteIds.includes(selected.id)}
                onToggleFavorite={toggleFavorite}
              />
            ) : (
              <section className="empty-stage">
                <p>Nenhum Pokémon encontrado.</p>
                <button type="button" className="ghost" onClick={clearFilters}>
                  Limpar filtros
                </button>
              </section>
            )}
          </main>

          <PokemonList
            pokemon={filtered}
            selectedId={selected?.id}
            onSelect={setSelectedId}
          />
        </>
      ) : (
        <TrainerPanel
          trainers={data.trainers}
          pokemon={data.pokemon}
          onOpenPokemon={openPokemon}
        />
      )}
    </div>
  )
}
