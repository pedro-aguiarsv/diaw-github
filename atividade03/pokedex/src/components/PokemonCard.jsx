import PokemonImage from './PokemonImage'
import { padId, pokemonTypes, typeColor } from '../utils/pokemon'

export default function PokemonCard({ pokemon, active, onSelect }) {
  const types = pokemonTypes(pokemon)
  const mainType = types[0].name

  return (
    <button
      type="button"
      id={`poke-${pokemon.id}`}
      className={active ? 'card active' : 'card'}
      style={{ '--type': typeColor[mainType] }}
      onClick={() => onSelect(pokemon.id)}
    >
      <PokemonImage id={pokemon.id} name={pokemon.name} className="card-img" />
      <span className="card-id">#{padId(pokemon.id)}</span>
      <span className="card-name">{pokemon.name}</span>
      <span className="card-types">
        {types.map((type) => type.name).join(' · ')}
      </span>
    </button>
  )
}
