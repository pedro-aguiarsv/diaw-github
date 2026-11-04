import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

export default function Login() {
  const { usuario, entrar } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ usuario: '', senha: '' })
  const [erro, setErro] = useState('')

  if (usuario) return <Navigate to={usuario.papel === 'AGENTE' ? '/agente' : '/cliente'} replace />
  if (usuario === undefined) return <p className="carregando">Carregando…</p>

  function campo(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    try {
      const sessao = await entrar(form)
      navigate(sessao.papel === 'AGENTE' ? '/agente' : '/cliente')
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <div className="entrada">
      <section className="entrada-copia">
        <p className="olho">DIAW · Laboratório 02</p>
        <h1>Locadora Norte</h1>
        <p className="lead">Pedidos de aluguel, análise do agente e contrato.</p>
      </section>
      <form className="painel" onSubmit={enviar}>
        <h2>Entrar</h2>
        <label>
          Usuário
          <input name="usuario" autoComplete="username" value={form.usuario} onChange={campo} required />
        </label>
        <label>
          Senha
          <input name="senha" type="password" autoComplete="current-password" value={form.senha} onChange={campo} required />
        </label>
        {erro && <p className="erro">{erro}</p>}
        <button type="submit" className="primario">Entrar</button>
        <p className="nota">Ainda não tem acesso? <Link to="/cadastro">Criar usuário</Link></p>
        <dl className="demo">
          <div><dt>Cliente</dt><dd>ana / ana123</dd></div>
          <div><dt>Empresa</dt><dd>empresa / empresa123</dd></div>
          <div><dt>Banco</dt><dd>banco / banco123</dd></div>
        </dl>
      </form>
    </div>
  )
}
