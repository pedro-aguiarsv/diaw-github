export default function SearchBar({ value, onChange }) {
  return (
    <label className="search">
      <span className="sr-only">Pesquisar por nome ou número</span>
      <input
        type="search"
        value={value}
        placeholder="Pesquisar Pokémon..."
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  )
}
