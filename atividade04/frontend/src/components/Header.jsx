import SearchBar from './SearchBar'

export default function Header({ view, onChangeView, query, onQuery, onRandom }) {
  return (
    <header className="top">
      <div className="brand-block">
        <p className="kicker">Kanto · 151</p>
        <h1>Pokédex</h1>
      </div>
      <div className="top-actions">
        <div className="view-switch">
          <button
            type="button"
            className={view === 'dex' ? 'on' : ''}
            onClick={() => onChangeView('dex')}
          >
            Pokémon
          </button>
          <button
            type="button"
            className={view === 'trainers' ? 'on' : ''}
            onClick={() => onChangeView('trainers')}
          >
            Treinadores
          </button>
        </div>
        {view === 'dex' && (
          <>
            <SearchBar value={query} onChange={onQuery} />
            <button type="button" className="ghost" onClick={onRandom}>
              Aleatório
            </button>
          </>
        )}
      </div>
    </header>
  )
}
