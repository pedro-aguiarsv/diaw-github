async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  })
  if (response.status === 204) {
    return null
  }
  const body = await response.json().catch(() => null)
  if (!response.ok) {
    throw new Error(body?.mensagem || 'A API não respondeu.')
  }
  return body
}

export function listarPokemons({ nome, tipo } = {}) {
  const params = new URLSearchParams()
  if (nome && nome.trim()) {
    params.set('nome', nome.trim())
  }
  if (tipo) {
    params.set('tipo', tipo)
  }
  const query = params.toString()
  return request('/api/pokemons' + (query ? `?${query}` : ''))
}

export function criarPokemon(pokemon) {
  return request('/api/pokemons', { method: 'POST', body: JSON.stringify(pokemon) })
}

export function atualizarPokemon(id, pokemon) {
  return request(`/api/pokemons/${id}`, { method: 'PUT', body: JSON.stringify(pokemon) })
}

export function excluirPokemon(id) {
  return request(`/api/pokemons/${id}`, { method: 'DELETE' })
}

export function listarTipos() {
  return request('/api/types')
}

export function listarTreinadores() {
  return request('/api/trainers')
}
