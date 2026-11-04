export async function api(caminho, { method = 'GET', body } = {}) {
  const resposta = await fetch(caminho, {
    method,
    credentials: 'include',
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  })
  if (resposta.status === 204) return null
  const dados = await resposta.json().catch(() => ({}))
  if (!resposta.ok) {
    const erro = new Error(dados.erro || 'Não foi possível concluir.')
    erro.status = resposta.status
    throw erro
  }
  return dados
}

export function moeda(valor) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function dataCurta(valor) {
  return new Date(`${valor}T00:00:00`).toLocaleDateString('pt-BR')
}

export const STATUS = {
  INTRODUZIDO: 'Introduzido',
  EM_ANALISE: 'Em análise',
  PARECER_POSITIVO: 'Parecer positivo',
  PARECER_NEGATIVO: 'Parecer negativo',
  CONTRATO_EM_EXECUCAO: 'Contrato em execução',
  CANCELADO: 'Cancelado',
}
