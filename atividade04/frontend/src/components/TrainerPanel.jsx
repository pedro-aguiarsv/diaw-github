import { useState } from 'react'
import PokemonImage from './PokemonImage'

export default function TrainerPanel({ trainers, pokemon, onOpenPokemon }) {
  const [activeId, setActiveId] = useState(trainers[0].id)
  const active = trainers.find((trainer) => trainer.id === activeId) || trainers[0]
  const byId = Object.fromEntries(pokemon.map((item) => [item.id, item]))

  return (
    <section className="trainers">
      <aside className="trainer-names">
        {trainers.map((trainer) => (
          <button
            key={trainer.id}
            type="button"
            className={trainer.id === active.id ? 'on' : ''}
            onClick={() => setActiveId(trainer.id)}
          >
            {trainer.name}
          </button>
        ))}
      </aside>
      <div className="trainer-stage">
        <p className="kicker">Treinador</p>
        <h2>{active.name}</h2>
        <p className="badges">
          {active.badges} {active.badges === 1 ? 'insígnia' : 'insígnias'}
        </p>
        <div className="badge-row" aria-hidden="true">
          {Array.from({ length: active.badges }, (_, index) => (
            <span key={index} className="badge-dot" />
          ))}
        </div>
        <div className="team">
          {active.pokemonIds.map((id, index) => {
            const creature = byId[id]
            if (!creature) {
              return null
            }
            return (
              <button
                key={`${id}-${index}`}
                type="button"
                className="team-member"
                onClick={() => onOpenPokemon(id)}
              >
                <PokemonImage id={id} name={creature.name} />
                <span>{creature.name}</span>
              </button>
            )
          })}
        </div>
      </div>
    </section>
  )
}
