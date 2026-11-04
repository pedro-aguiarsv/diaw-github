import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { STATUS, api, dataCurta, moeda } from '../api'
import { useAuth } from '../auth'
import Shell from '../components/Shell'

export default function PedidoDetalhe() {
  const { id } = useParams()
  const { usuario } = useAuth()
  const [pedido, setPedido] = useState(null)
  const [erro, setErro] = useState('')
  const [parecer, setParecer] = useState('Renda compatível com o valor do aluguel.')
  const [credito, setCredito] = useState({ valorFinanciado: '', numeroParcelas: 6, taxaMensal: '1.90' })
  const [edicao, setEdicao] = useState(null)

  function carregar() {
    api(`/api/pedidos/${id}`).then((dados) => {
      setPedido(dados)
      setEdicao({
        automovelId: dados.automovel.id,
        dataInicio: dados.dataInicio,
        dataFim: dados.dataFim,
        valorDiaria: dados.valorDiaria,
        observacao: dados.observacao || '',
      })
    }).catch((falha) => setErro(falha.message))
  }

  useEffect(carregar, [id])

  async function acao(caminho, body) {
    setErro('')
    try {
      setPedido(await api(caminho, { method: 'POST', body }))
    } catch (falha) {
      setErro(falha.message)
    }
  }

  async function salvar(evento) {
    evento.preventDefault()
    setErro('')
    try {
      setPedido(await api(`/api/pedidos/${id}`, {
        method: 'PUT',
        body: { ...edicao, valorDiaria: Number(edicao.valorDiaria) },
      }))
    } catch (falha) {
      setErro(falha.message)
    }
  }

  if (!pedido) {
    return <Shell><p className="carregando">{erro || 'Carregando…'}</p></Shell>
  }

  const cliente = usuario.papel === 'CLIENTE'
  const podeAlterar = (cliente && pedido.status === 'INTRODUZIDO')
    || (!cliente && (pedido.status === 'INTRODUZIDO' || pedido.status === 'EM_ANALISE'))
  const podeCancelar = cliente && pedido.status !== 'CONTRATO_EM_EXECUCAO' && pedido.status !== 'CANCELADO'

  return (
    <Shell>
      <article className="dossie">
        <p className="olho">Pedido {pedido.id}</p>
        <h1>{pedido.automovel.marca} {pedido.automovel.modelo}</h1>
        <p className="lead">{pedido.automovel.placa} · {dataCurta(pedido.dataInicio)} até {dataCurta(pedido.dataFim)}</p>
        <p className={`selo selo-${pedido.status}`}>{STATUS[pedido.status]}</p>
        <p className="valor">{moeda(pedido.valorTotal)} <small>{moeda(pedido.valorDiaria)} / dia</small></p>
        <dl className="ficha">
          <div><dt>Cliente</dt><dd>{pedido.cliente.nome}</dd></div>
          <div><dt>Profissão</dt><dd>{pedido.cliente.profissao}</dd></div>
          <div><dt>Endereço</dt><dd>{pedido.cliente.endereco}</dd></div>
          <div><dt>CPF</dt><dd>{pedido.cliente.cpf}</dd></div>
          {pedido.cliente.empregadores.map((item) => (
            <div key={item.entidade}><dt>{item.entidade}</dt><dd>{moeda(item.rendimento)}</dd></div>
          ))}
          {pedido.parecer && <div><dt>Parecer</dt><dd>{pedido.parecer}{pedido.agenteNome ? ` — ${pedido.agenteNome}` : ''}</dd></div>}
          {pedido.contrato && (
            <div>
              <dt>Crédito</dt>
              <dd>{moeda(pedido.contrato.valorFinanciado)} em {pedido.contrato.numeroParcelas}x · taxa {pedido.contrato.taxaMensal}% · {pedido.contrato.bancoNome}</dd>
            </div>
          )}
          {pedido.observacao && <div><dt>Nota</dt><dd>{pedido.observacao}</dd></div>}
        </dl>
        {erro && <p className="erro">{erro}</p>}

        {podeAlterar && edicao && (
          <form className="painel" onSubmit={salvar}>
            <h2>Alterar pedido</h2>
            <label>Retirada<input type="date" value={edicao.dataInicio} onChange={(evento) => setEdicao({ ...edicao, dataInicio: evento.target.value })} required /></label>
            <label>Devolução<input type="date" value={edicao.dataFim} onChange={(evento) => setEdicao({ ...edicao, dataFim: evento.target.value })} required /></label>
            <label>Diária<input type="number" min="1" step="0.01" value={edicao.valorDiaria} onChange={(evento) => setEdicao({ ...edicao, valorDiaria: evento.target.value })} required /></label>
            <label>Observação<textarea value={edicao.observacao} onChange={(evento) => setEdicao({ ...edicao, observacao: evento.target.value })} rows={3} /></label>
            <button className="primario" type="submit">Salvar</button>
          </form>
        )}

        {podeCancelar && <button type="button" className="texto" onClick={() => acao(`/api/pedidos/${id}/cancelar`)}>Cancelar pedido</button>}

        {!cliente && (pedido.status === 'INTRODUZIDO') && (
          <button type="button" className="primario" onClick={() => acao(`/api/pedidos/${id}/analise`)}>Assumir análise</button>
        )}

        {!cliente && (pedido.status === 'INTRODUZIDO' || pedido.status === 'EM_ANALISE') && (
          <form className="painel" onSubmit={(evento) => { evento.preventDefault(); acao(`/api/pedidos/${id}/avaliar`, { decisao: evento.nativeEvent.submitter.value, parecer }) }}>
            <h2>Parecer financeiro</h2>
            <label>Texto<textarea value={parecer} onChange={(evento) => setParecer(evento.target.value)} required /></label>
            <div className="acoes">
              <button className="primario" type="submit" value="POSITIVO">Parecer positivo</button>
              <button className="texto" type="submit" value="NEGATIVO">Parecer negativo</button>
            </div>
          </form>
        )}

        {!cliente && pedido.status === 'PARECER_POSITIVO' && (
          <button type="button" className="primario" onClick={() => acao(`/api/pedidos/${id}/executar`)}>Executar contrato</button>
        )}

        {usuario.tipoAgente === 'BANCO' && pedido.status === 'PARECER_POSITIVO' && (
          <form className="painel" onSubmit={(evento) => {
            evento.preventDefault()
            acao(`/api/pedidos/${id}/contrato`, {
              valorFinanciado: Number(credito.valorFinanciado || pedido.valorTotal),
              numeroParcelas: Number(credito.numeroParcelas),
              taxaMensal: Number(credito.taxaMensal),
            })
          }}>
            <h2>Contrato de crédito</h2>
            <label>Valor<input type="number" min="1" step="0.01" value={credito.valorFinanciado} placeholder={pedido.valorTotal} onChange={(evento) => setCredito({ ...credito, valorFinanciado: evento.target.value })} /></label>
            <label>Parcelas<input type="number" min="1" max="60" value={credito.numeroParcelas} onChange={(evento) => setCredito({ ...credito, numeroParcelas: evento.target.value })} required /></label>
            <label>Taxa mensal (%)<input type="number" min="0" step="0.01" value={credito.taxaMensal} onChange={(evento) => setCredito({ ...credito, taxaMensal: evento.target.value })} required /></label>
            <button className="primario" type="submit">Conceder crédito e executar</button>
          </form>
        )}
      </article>
    </Shell>
  )
}
