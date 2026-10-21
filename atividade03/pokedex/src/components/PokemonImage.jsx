import { useState } from 'react'
import { artworkUrl, spriteUrl } from '../utils/pokemon'

export default function PokemonImage({ id, name, className, priority = false }) {
  const [failedId, setFailedId] = useState(null)
  const src = failedId === id ? spriteUrl(id) : artworkUrl(id)

  function handleError() {
    if (failedId !== id) {
      setFailedId(id)
    }
  }

  return (
    <img
      className={className}
      src={src}
      alt={name}
      loading={priority ? 'eager' : 'lazy'}
      onError={handleError}
    />
  )
}
