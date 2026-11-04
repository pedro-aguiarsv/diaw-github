import { useEffect, useState } from 'react'
import { api } from '../api'
import Shell from '../components/Shell'

const vazio = { matricula: '', ano: 2024, marca: '', modelo: '', placa: '' }

export default function Frota() {
  const [carros, setCarros] = useState([])
  const [form, setForm] = useState(vazio)
  const [erro, setErro] = useState('')

  function carregar() {
    api('/api/automoveis').then(setCarros).catch((falha) => setErro(falha.message))
  }

  useEffect(carregar, [])

  function campo(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    try {
      await api('/api/automoveis', { method: 'POST', body: { ...form, ano: Number(form.ano) } })
      setForm(vazio)
      carregar()
    } catch (falha) {
      setErro(falha.message)
    }
  }

  async function remover(id) {
    setErro('')
    try {
      await api(`/api/automoveis/${id}`, { method: 'DELETE' })
      carregar()
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <Shell>
      <section className="composicao">
        <div>
          <h1>Frota</h1>
          <p className="lead">O automóvel fica no nome de quem está logado: cliente, empresa ou banco.</p>
          <ol className="livro">
            {carros.map((carro) => (
              <li key={carro.id}>
                <div>
                  <strong>{carro.placa}</strong>
                  <span>{carro.marca} {carro.modelo} · {carro.ano}</span>
                  <span>{carro.tipoProprietario} · {carro.proprietarioNome}</span>
                  {carro.proprio && <button type="button" className="texto" onClick={() => remover(carro.id)}>Remover</button>}
                </div>
              </li>
            ))}
          </ol>
        </div>
        <form className="painel" onSubmit={enviar}>
          <h2>Registrar automóvel</h2>
          <label>Matrícula<input name="matricula" value={form.matricula} onChange={campo} required /></label>
          <label>Ano<input name="ano" type="number" min="1980" max="2035" value={form.ano} onChange={campo} required /></label>
          <label>Marca<input name="marca" value={form.marca} onChange={campo} required /></label>
          <label>Modelo<input name="modelo" value={form.modelo} onChange={campo} required /></label>
          <label>Placa<input name="placa" value={form.placa} onChange={campo} required /></label>
          {erro && <p className="erro">{erro}</p>}
          <button className="primario" type="submit">Registrar</button>
        </form>
      </section>
    </Shell>
  )
}
