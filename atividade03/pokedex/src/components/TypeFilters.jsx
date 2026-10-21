import { typeColor } from '../utils/pokemon'

export default function TypeFilters({ types, selected, onSelect }) {
  return (
    <div className="types">
      <button
        type="button"
        className={selected === 'todos' ? 'chip on' : 'chip'}
        onClick={() => onSelect('todos')}
      >
        Todos
      </button>
      {types.map((type) => (
        <button
          key={type.id}
          type="button"
          className={selected === type.name ? 'chip on' : 'chip'}
          style={{ '--type': typeColor[type.name] }}
          onClick={() => onSelect(type.name)}
        >
          {type.name}
        </button>
      ))}
    </div>
  )
}
