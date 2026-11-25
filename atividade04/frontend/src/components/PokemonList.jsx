import { useEffect, useRef } from 'react'
import PokemonCard from './PokemonCard'

export default function PokemonList({ pokemon, selectedId, onSelect }) {
  const stripRef = useRef(null)

  useEffect(() => {
    const strip = stripRef.current
    const current = strip?.querySelector(`#poke-${selectedId}`)
    if (!strip || !current) {
      return
    }
    strip.scrollTo({ left: current.offsetLeft - 40, behavior: 'smooth' })
  }, [selectedId])

  return (
    <section className="strip-wrap">
      <p className="strip-count">
        {pokemon.length === 0
          ? 'Nenhum Pokémon encontrado'
          : `${pokemon.length} Pokémon`}
      </p>
      {pokemon.length === 0 ? (
        <p className="empty-strip">Tente outro nome, número ou tipo.</p>
      ) : (
        <div className="filmstrip" ref={stripRef}>
          {pokemon.map((item) => (
            <PokemonCard
              key={item.id}
              pokemon={item}
              active={item.id === selectedId}
              onSelect={onSelect}
            />
          ))}
        </div>
      )}
    </section>
  )
}
