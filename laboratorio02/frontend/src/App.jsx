import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth'
import AgenteHome from './pages/AgenteHome'
import Cadastro from './pages/Cadastro'
import CadastroCliente from './pages/CadastroCliente'
import ClienteHome from './pages/ClienteHome'
import Frota from './pages/Frota'
import Login from './pages/Login'
import NovoPedido from './pages/NovoPedido'
import PedidoDetalhe from './pages/PedidoDetalhe'

function Protegido({ papel, children }) {
  const { usuario } = useAuth()
  if (usuario === undefined) return <p className="carregando">Carregando…</p>
  if (!usuario) return <Navigate to="/" replace />
  if (papel && usuario.papel !== papel) {
    return <Navigate to={usuario.papel === 'AGENTE' ? '/agente' : '/cliente'} replace />
  }
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route path="/cliente" element={<Protegido papel="CLIENTE"><ClienteHome /></Protegido>} />
      <Route path="/cliente/novo" element={<Protegido papel="CLIENTE"><NovoPedido /></Protegido>} />
      <Route path="/cliente/cadastro" element={<Protegido papel="CLIENTE"><CadastroCliente /></Protegido>} />
      <Route path="/cliente/frota" element={<Protegido papel="CLIENTE"><Frota /></Protegido>} />
      <Route path="/cliente/pedido/:id" element={<Protegido papel="CLIENTE"><PedidoDetalhe /></Protegido>} />
      <Route path="/agente" element={<Protegido papel="AGENTE"><AgenteHome /></Protegido>} />
      <Route path="/agente/frota" element={<Protegido papel="AGENTE"><Frota /></Protegido>} />
      <Route path="/agente/pedido/:id" element={<Protegido papel="AGENTE"><PedidoDetalhe /></Protegido>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
