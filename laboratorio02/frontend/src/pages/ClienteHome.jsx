import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { STATUS, api, dataCurta, moeda } from '../api'
import Shell from '../components/Shell'

export default function ClienteHome() {
  const [pedidos, setPedidos] = useState(null)
  const [erro, setErro] = useState('')
  const [semCadastro, setSemCadastro] = useState(false)

  useEffect(() => {
    api('/api/pedidos').then(setPedidos).catch((falha) => {
      if (falha.status === 409) setSemCadastro(true)
      else setErro(falha.message)
    })
  }, [])

  const destaque = pedidos?.[0]

  return (
    <Shell>
      <section className="resumo">
        <div>
          <h1>Meus pedidos</h1>
          {destaque ? (
            <p className="lead">
              {destaque.automovel.marca} {destaque.automovel.modelo}, {destaque.automovel.placa}. {STATUS[destaque.status]}, {dataCurta(destaque.dataInicio)} a {dataCurta(destaque.dataFim)}, {moeda(destaque.valorTotal)}.
            </p>
          ) : (
            <p className="lead">
              {semCadastro
                ? 'Preencha o cadastro antes de abrir um pedido.'
                : 'Nenhum pedido ainda.'}
            </p>
          )}
        </div>
        <Link className="primario link-botao" to={semCadastro ? '/cliente/cadastro' : '/cliente/novo'}>
          {semCadastro ? 'Completar cadastro' : 'Novo pedido'}
        </Link>
      </section>
      {erro && <p className="erro">{erro}</p>}
      <ol className="livro">
        {pedidos?.map((pedido) => (
          <li key={pedido.id}>
            <Link to={`/cliente/pedido/${pedido.id}`}>
              <strong>{pedido.automovel.placa}</strong>
              <span>{pedido.automovel.marca} {pedido.automovel.modelo}</span>
              <span>{dataCurta(pedido.dataInicio)}</span>
              <em className={`selo selo-${pedido.status}`}>{STATUS[pedido.status]}</em>
            </Link>
          </li>
        ))}
      </ol>
    </Shell>
  )
}
