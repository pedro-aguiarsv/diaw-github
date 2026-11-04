import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

const inicial = { usuario: '', senha: '', nome: '', papel: 'CLIENTE', tipoAgente: 'EMPRESA', razaoSocial: '' }

export default function Cadastro() {
  const { registrar, entrar } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState(inicial)
  const [erro, setErro] = useState('')

  function campo(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    const corpo = {
      usuario: form.usuario,
      senha: form.senha,
      nome: form.nome,
      papel: form.papel,
      tipoAgente: form.papel === 'AGENTE' ? form.tipoAgente : null,
      razaoSocial: form.papel === 'AGENTE' ? form.razaoSocial : null,
    }
    try {
      await registrar(corpo)
      const sessao = await entrar({ usuario: form.usuario, senha: form.senha })
      navigate(sessao.papel === 'AGENTE' ? '/agente' : '/cliente/cadastro')
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <div className="entrada entrada-curta">
      <form className="painel painel-largo" onSubmit={enviar}>
        <h2>Criar usuário</h2>
        <div className="escolha">
          <label><input type="radio" name="papel" value="CLIENTE" checked={form.papel === 'CLIENTE'} onChange={campo} /> Cliente</label>
          <label><input type="radio" name="papel" value="AGENTE" checked={form.papel === 'AGENTE'} onChange={campo} /> Agente</label>
        </div>
        <label>Nome<input name="nome" value={form.nome} onChange={campo} required /></label>
        <label>Usuário<input name="usuario" value={form.usuario} onChange={campo} required minLength={3} /></label>
        <label>Senha<input name="senha" type="password" value={form.senha} onChange={campo} required minLength={6} /></label>
        {form.papel === 'AGENTE' && (
          <>
            <label>
              Tipo
              <select name="tipoAgente" value={form.tipoAgente} onChange={campo}>
                <option value="EMPRESA">Empresa</option>
                <option value="BANCO">Banco</option>
              </select>
            </label>
            <label>Razão social<input name="razaoSocial" value={form.razaoSocial} onChange={campo} required /></label>
          </>
        )}
        {erro && <p className="erro">{erro}</p>}
        <button type="submit" className="primario">Criar e entrar</button>
        <p className="nota"><Link to="/">Voltar ao login</Link></p>
      </form>
    </div>
  )
}
