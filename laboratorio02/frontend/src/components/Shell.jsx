import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

export default function Shell({ children }) {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const base = usuario.papel === 'AGENTE' ? '/agente' : '/cliente'

  return (
    <div className="palco">
      <header className="topo">
        <Link to={base} className="marca">Locadora Norte</Link>
        <nav>
          <Link to={base}>Pedidos</Link>
          <Link to={`${base}/frota`}>Frota</Link>
          {usuario.papel === 'CLIENTE' && <Link to="/cliente/cadastro">Cadastro</Link>}
        </nav>
        <div className="topo-fim">
          <span>{usuario.nome}</span>
          <button
            type="button"
            className="texto"
            onClick={async () => {
              await sair()
              navigate('/')
            }}
          >
            Sair
          </button>
        </div>
      </header>
      <main>{children}</main>
    </div>
  )
}
