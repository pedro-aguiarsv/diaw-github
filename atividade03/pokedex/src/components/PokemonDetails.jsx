import PokemonImage from './PokemonImage'
import StatBar from './StatBar'
import { padId, pokemonTypes, statFields, typeColor } from '../utils/pokemon'

export default function PokemonDetails({ pokemon, favorite, onToggleFavorite }) {
  const types = pokemonTypes(pokemon)
  const mainType = types[0].name
  const color = typeColor[mainType]

  return (
    <>
      <div className="hero">
        <div className="halo" />
        <PokemonImage
          id={pokemon.id}
          name={pokemon.name}
          className="hero-img"
          priority
        />
      </div>
      <article className="panel">
        <p className="dex-no">#{padId(pokemon.id)}</p>
        <div className="panel-head">
          <h2>{pokemon.name}</h2>
          <button
            type="button"
            className={favorite ? 'fav on' : 'fav'}
            onClick={() => onToggleFavorite(pokemon.id)}
          >
            {favorite ? 'Favorito' : 'Favoritar'}
          </button>
        </div>
        <div className="type-row">
          {types.map((type) => (
            <span
              key={type.id}
              className="type-pill"
              style={{ '--type': typeColor[type.name] }}
            >
              {type.name}
            </span>
          ))}
        </div>
        <div className="stats">
          {statFields.map((field) => (
            <StatBar
              key={field.key}
              label={field.label}
              value={pokemon.stats[field.key]}
              color={color}
            />
          ))}
        </div>
      </article>
    </>
  )
}
