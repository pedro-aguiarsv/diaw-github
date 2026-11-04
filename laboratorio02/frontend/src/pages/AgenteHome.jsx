import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { STATUS, api, dataCurta, moeda } from '../api'
import Shell from '../components/Shell'

export default function AgenteHome() {
  const [pedidos, setPedidos] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/api/pedidos').then(setPedidos).catch((falha) => setErro(falha.message))
  }, [])

  const fila = pedidos?.filter((pedido) => pedido.status === 'INTRODUZIDO' || pedido.status === 'EM_ANALISE') ?? []
  const proximo = fila[0] ?? pedidos?.[0]

  return (
    <Shell>
      <section className="resumo">
        <div>
          <h1>Pedidos</h1>
          <p className="lead">
            {fila.length === 0 ? 'Nenhum pedido aguardando análise.' : `${fila.length} aguardando análise.`}
            {proximo ? ` Próximo: ${proximo.cliente.nome}, ${proximo.automovel.modelo}, ${moeda(proximo.valorTotal)}.` : ''}
          </p>
        </div>
        {proximo && <Link className="primario link-botao" to={`/agente/pedido/${proximo.id}`}>Abrir</Link>}
      </section>
      {erro && <p className="erro">{erro}</p>}
      <ol className="livro">
        {pedidos?.map((pedido) => (
          <li key={pedido.id}>
            <Link to={`/agente/pedido/${pedido.id}`}>
              <strong>{pedido.cliente.nome}</strong>
              <span>{pedido.automovel.placa}</span>
              <span>{dataCurta(pedido.dataInicio)}</span>
              <em className={`selo selo-${pedido.status}`}>{STATUS[pedido.status]}</em>
            </Link>
          </li>
        ))}
      </ol>
    </Shell>
  )
}
