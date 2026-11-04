import { useEffect, useState } from 'react'
import { api } from '../api'
import Shell from '../components/Shell'

const vazio = { rg: '', cpf: '', nome: '', endereco: '', profissao: '', empregadores: [{ entidade: '', rendimento: '' }] }

export default function CadastroCliente() {
  const [form, setForm] = useState(vazio)
  const [id, setId] = useState(null)
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState('')

  useEffect(() => {
    api('/api/clientes/me').then((cliente) => {
      setId(cliente.id)
      setForm({
        rg: cliente.rg,
        cpf: cliente.cpf,
        nome: cliente.nome,
        endereco: cliente.endereco,
        profissao: cliente.profissao,
        empregadores: cliente.empregadores.length
          ? cliente.empregadores.map((item) => ({ entidade: item.entidade, rendimento: String(item.rendimento) }))
          : [{ entidade: '', rendimento: '' }],
      })
    }).catch(() => {})
  }, [])

  function campo(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  function renda(indice, evento) {
    const empregadores = form.empregadores.map((item, i) => i === indice ? { ...item, [evento.target.name]: evento.target.value } : item)
    setForm({ ...form, empregadores })
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setOk('')
    const corpo = {
      ...form,
      empregadores: form.empregadores
        .filter((item) => item.entidade.trim())
        .map((item) => ({ entidade: item.entidade, rendimento: Number(item.rendimento) })),
    }
    try {
      const salvo = id
        ? await api(`/api/clientes/${id}`, { method: 'PUT', body: corpo })
        : await api('/api/clientes', { method: 'POST', body: corpo })
      setId(salvo.id)
      setOk('Cadastro salvo.')
    } catch (falha) {
      setErro(falha.message)
    }
  }

  async function remover() {
    if (!id || !window.confirm('Remover o cadastro de cliente?')) return
    try {
      await api(`/api/clientes/${id}`, { method: 'DELETE' })
      setId(null)
      setForm(vazio)
      setOk('Cadastro removido. O login continua ativo.')
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <Shell>
      <form className="painel painel-largo" onSubmit={enviar}>
        <h1>{id ? 'Cadastro do cliente' : 'Novo cadastro'}</h1>
        <p className="lead">RG, CPF, endereço, profissão e até três entidades empregadoras com o rendimento.</p>
        <div className="grade">
          <label>Nome<input name="nome" value={form.nome} onChange={campo} required /></label>
          <label>RG<input name="rg" value={form.rg} onChange={campo} required /></label>
          <label>CPF<input name="cpf" value={form.cpf} onChange={campo} required /></label>
          <label>Profissão<input name="profissao" value={form.profissao} onChange={campo} required /></label>
        </div>
        <label>Endereço<input name="endereco" value={form.endereco} onChange={campo} required /></label>
        <fieldset>
          <legend>Empregadores</legend>
          {form.empregadores.map((item, indice) => (
            <div className="par" key={indice}>
              <label>Entidade<input name="entidade" value={item.entidade} onChange={(evento) => renda(indice, evento)} /></label>
              <label>Rendimento<input name="rendimento" type="number" min="0" step="0.01" value={item.rendimento} onChange={(evento) => renda(indice, evento)} /></label>
            </div>
          ))}
          {form.empregadores.length < 3 && (
            <button type="button" className="texto" onClick={() => setForm({ ...form, empregadores: [...form.empregadores, { entidade: '', rendimento: '' }] })}>
              Adicionar empregador
            </button>
          )}
        </fieldset>
        {erro && <p className="erro">{erro}</p>}
        {ok && <p className="ok">{ok}</p>}
        <div className="acoes">
          <button className="primario" type="submit">{id ? 'Salvar alterações' : 'Criar cadastro'}</button>
          {id && <button className="texto" type="button" onClick={remover}>Remover cadastro</button>}
        </div>
      </form>
    </Shell>
  )
}
