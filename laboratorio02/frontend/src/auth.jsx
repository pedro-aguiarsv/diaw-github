import { createContext, useContext, useEffect, useState } from 'react'
import { api } from './api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(undefined)

  useEffect(() => {
    api('/api/auth/me').then(setUsuario).catch(() => setUsuario(null))
  }, [])

  async function entrar(credenciais) {
    const sessao = await api('/api/auth/login', { method: 'POST', body: credenciais })
    setUsuario(sessao)
    return sessao
  }

  async function registrar(dados) {
    return api('/api/auth/registrar', { method: 'POST', body: dados })
  }

  async function sair() {
    await api('/api/auth/sair', { method: 'POST' })
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, entrar, registrar, sair }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
