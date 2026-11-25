import { useEffect, useMemo, useState } from 'react'
import Header from './components/Header'
import TypeFilters from './components/TypeFilters'
import PokemonList from './components/PokemonList'
import PokemonDetails from './components/PokemonDetails'
import TrainerPanel from './components/TrainerPanel'
import PokemonForm from './components/PokemonForm'
import { filterPokemon, pokemonTypes, typeColor, typesInUse } from './utils/pokemon'
import {
  atualizarPokemon,
  criarPokemon,
  excluirPokemon,
  listarPokemons,
  listarTipos,
  listarTreinadores,
} from './api'

const STORAGE_KEY = 'pokedex-favoritos'

export default function App() {
  const [pokemon, setPokemon] = useState([])
  const [acervo, setAcervo] = useState([])
  const [catalogo, setCatalogo] = useState([])
  const [trainers, setTrainers] = useState([])
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
  const [erro, setErro] = useState('')
  const [pronto, setPronto] = useState(false)
  const [formulario, setFormulario] = useState(null)

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(favoriteIds))
  }, [favoriteIds])

  useEffect(() => {
    let ativo = true
    Promise.all([listarTipos(), listarTreinadores()])
      .then(([tipos, treinadores]) => {
        if (!ativo) {
          return
        }
        setCatalogo(tipos)
        setTrainers(treinadores)
      })
      .catch((error) => {
        if (ativo) {
          setErro(error.message)
        }
      })
    return () => {
      ativo = false
    }
  }, [])

  useEffect(() => {
    let ativo = true
    const espera = setTimeout(() => {
      listarPokemons({ nome: query, tipo: typeName === 'todos' ? '' : typeName })
        .then((lista) => {
          if (!ativo) {
            return
          }
          setPokemon(lista)
          if (!query.trim() && typeName === 'todos') {
            setAcervo(lista)
          }
          setErro('')
          setPronto(true)
        })
        .catch((error) => {
          if (ativo) {
            setErro(error.message)
            setPronto(true)
          }
        })
    }, 180)
    return () => {
      ativo = false
      clearTimeout(espera)
    }
  }, [query, typeName])

  const types = useMemo(() => typesInUse(acervo, catalogo), [acervo, catalogo])

  const filtered = filterPokemon(pokemon, {
    query,
    typeName,
    favoritesOnly,
    favoriteIds,
    sort,
  })

  const selected = filtered.find((item) => item.id === selectedId) || filtered[0] || null
  const accentName = selected ? pokemonTypes(selected)[0].name : null
  const accent = typeColor[accentName] || '#f2c14e'

  function toggleFavorite(id) {
    setFavoriteIds((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    )
  }

  function pickRandom() {
    const pool = filtered.length ? filtered : acervo
    if (!pool.length) {
      return
    }
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

  async function salvar(payload) {
    const salvo = formulario === 'novo' ? await criarPokemon(payload) : await atualizarPokemon(formulario.id, payload)
    setFormulario(null)
    const lista = await listarPokemons()
    setPokemon(lista)
    setAcervo(lista)
    openPokemon(salvo.id)
  }

  async function excluir(id) {
    if (!window.confirm('Excluir este Pokémon da Pokédex?')) {
      return
    }
    try {
      await excluirPokemon(id)
      setFavoriteIds((current) => current.filter((item) => item !== id))
      setTrainers((current) =>
        current.map((trainer) => ({
          ...trainer,
          pokemonIds: trainer.pokemonIds.filter((pokemonId) => pokemonId !== id),
        })),
      )
      const lista = await listarPokemons({ nome: query, tipo: typeName === 'todos' ? '' : typeName })
      const completa = query.trim() || typeName !== 'todos' ? await listarPokemons() : lista
      setPokemon(lista)
      setAcervo(completa)
    } catch (error) {
      setErro(error.message)
    }
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
              <button type="button" className="chip" onClick={() => setFormulario('novo')}>
                Cadastrar
              </button>
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
            {!pronto ? (
              <section className="empty-stage">
                <p>Carregando a Pokédex…</p>
              </section>
            ) : selected ? (
              <PokemonDetails
                pokemon={selected}
                favorite={favoriteIds.includes(selected.id)}
                onToggleFavorite={toggleFavorite}
                onEdit={() => setFormulario(selected)}
                onDelete={() => excluir(selected.id)}
              />
            ) : (
              <section className="empty-stage">
                <p>{erro || 'Nenhum Pokémon encontrado.'}</p>
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
      ) : trainers.length > 0 ? (
        <TrainerPanel
          trainers={trainers}
          pokemon={acervo}
          onOpenPokemon={openPokemon}
        />
      ) : (
        <section className="empty-stage">
          <p>{erro || 'Nenhum treinador carregado.'}</p>
        </section>
      )}

      {formulario && (
        <PokemonForm
          types={catalogo}
          pokemon={formulario === 'novo' ? null : formulario}
          onClose={() => setFormulario(null)}
          onSave={salvar}
        />
      )}
    </div>
  )
}
