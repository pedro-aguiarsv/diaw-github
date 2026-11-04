import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, moeda } from '../api'
import Shell from '../components/Shell'

export default function NovoPedido() {
  const navigate = useNavigate()
  const [carros, setCarros] = useState([])
  const [escolhido, setEscolhido] = useState(null)
  const [form, setForm] = useState({ dataInicio: '', dataFim: '', valorDiaria: '220', observacao: '' })
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/api/automoveis').then((lista) => {
      setCarros(lista)
      setEscolhido(lista[0]?.id ?? null)
    }).catch((falha) => setErro(falha.message))
  }, [])

  const carro = carros.find((item) => item.id === escolhido)

  function campo(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    try {
      const pedido = await api('/api/pedidos', {
        method: 'POST',
        body: {
          automovelId: escolhido,
          dataInicio: form.dataInicio,
          dataFim: form.dataFim,
          valorDiaria: Number(form.valorDiaria),
          observacao: form.observacao,
        },
      })
      navigate(`/cliente/pedido/${pedido.id}`)
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <Shell>
      <form className="composicao" onSubmit={enviar}>
        <div>
          <h1>Novo pedido</h1>
          <p className="lead">{carro ? `${carro.marca} ${carro.modelo}, ${carro.ano}, ${carro.placa}. Dono: ${carro.proprietarioNome}.` : 'Nenhum automóvel cadastrado.'}</p>
          <div className="placas">
            {carros.map((item) => (
              <button
                key={item.id}
                type="button"
                className={item.id === escolhido ? 'placa ativa' : 'placa'}
                onClick={() => setEscolhido(item.id)}
              >
                {item.placa}
                <small>{item.modelo}</small>
              </button>
            ))}
          </div>
        </div>
        <div className="painel">
          <label>Retirada<input type="date" name="dataInicio" value={form.dataInicio} onChange={campo} required /></label>
          <label>Devolução<input type="date" name="dataFim" value={form.dataFim} onChange={campo} required /></label>
          <label>Diária<input name="valorDiaria" type="number" min="1" step="0.01" value={form.valorDiaria} onChange={campo} required /></label>
          <label>Observação<textarea name="observacao" value={form.observacao} onChange={campo} rows={3} /></label>
          <p className="nota">O total é a diária vezes os dias. Hoje, {moeda(form.valorDiaria || 0)} por dia.</p>
          {erro && <p className="erro">{erro}</p>}
          <button className="primario" type="submit" disabled={!escolhido}>Introduzir pedido</button>
        </div>
      </form>
    </Shell>
  )
}
