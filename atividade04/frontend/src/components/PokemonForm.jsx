import { useState } from 'react'
import { statFields } from '../utils/pokemon'

const VAZIO = {
  id: '',
  name: '',
  hp: 50,
  attack: 50,
  defense: 50,
  specialAttack: 50,
  specialDefense: 50,
  speed: 50,
  typeIds: [],
}

function estadoInicial(pokemon) {
  if (!pokemon) {
    return VAZIO
  }
  return {
    id: pokemon.id,
    name: pokemon.name,
    hp: pokemon.stats.hp,
    attack: pokemon.stats.attack,
    defense: pokemon.stats.defense,
    specialAttack: pokemon.stats.specialAttack,
    specialDefense: pokemon.stats.specialDefense,
    speed: pokemon.stats.speed,
    typeIds: pokemon.types.map((type) => type.id),
  }
}

export default function PokemonForm({ types, pokemon, onClose, onSave }) {
  const editando = Boolean(pokemon)
  const [form, setForm] = useState(() => estadoInicial(pokemon))
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const catalogo = types.filter((type) => type.name !== 'Typeless')

  function setCampo(campo, valor) {
    setForm((atual) => ({ ...atual, [campo]: valor }))
  }

  function alternarTipo(id) {
    setForm((atual) => {
      const marcado = atual.typeIds.includes(id)
      const typeIds = marcado ? atual.typeIds.filter((item) => item !== id) : [...atual.typeIds, id]
      return { ...atual, typeIds }
    })
  }

  async function enviar(event) {
    event.preventDefault()
    setErro('')
    setEnviando(true)
    const payload = {
      id: form.id === '' ? null : Number(form.id),
      name: form.name,
      stats: {
        hp: Number(form.hp),
        attack: Number(form.attack),
        defense: Number(form.defense),
        specialAttack: Number(form.specialAttack),
        specialDefense: Number(form.specialDefense),
        speed: Number(form.speed),
      },
      types: form.typeIds.map((id) => ({ id })),
    }
    try {
      await onSave(payload)
    } catch (error) {
      setErro(error.message)
      setEnviando(false)
    }
  }

  return (
    <div className="sheet-backdrop" onClick={onClose}>
      <form className="sheet" onClick={(event) => event.stopPropagation()} onSubmit={enviar}>
        <p className="kicker">{editando ? 'Editar' : 'Cadastrar'}</p>
        <h2>{editando ? pokemon.name : 'Novo Pokémon'}</h2>
        <div className="sheet-grid">
          <label>
            Número
            <input
              type="number"
              min="1"
              value={form.id}
              disabled={editando}
              placeholder="automático"
              onChange={(event) => setCampo('id', event.target.value)}
            />
          </label>
          <label>
            Nome
            <input
              value={form.name}
              maxLength={40}
              required
              onChange={(event) => setCampo('name', event.target.value)}
            />
          </label>
          {statFields.map((field) => (
            <label key={field.key}>
              {field.label}
              <input
                type="number"
                min="1"
                max="255"
                required
                value={form[field.key]}
                onChange={(event) => setCampo(field.key, event.target.value)}
              />
            </label>
          ))}
        </div>
        <fieldset className="type-pick">
          <legend>Tipos</legend>
          {catalogo.map((type) => (
            <label key={type.id}>
              <input
                type="checkbox"
                checked={form.typeIds.includes(type.id)}
                onChange={() => alternarTipo(type.id)}
              />
              {type.name}
            </label>
          ))}
        </fieldset>
        {erro && <p className="sheet-erro">{erro}</p>}
        <div className="sheet-actions">
          <button type="button" className="ghost" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="fav on" disabled={enviando}>
            {enviando ? 'Salvando…' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  )
}
