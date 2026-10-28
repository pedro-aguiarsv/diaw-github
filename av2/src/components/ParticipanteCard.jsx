function ParticipanteCard({ participante, onVotar }) {
    const { nome, foto, naRoca } = participante
    return (
        <article className={participante.naRoca ? 'card card-roca' : 'card'}>
            {participante.naRoca && <span className="selo">Na roça</span>}
            <img src={participante.foto} alt={participante.nome} />
            <h3>{participante.nome}</h3>
            {onVotar && (
                <button type="button" className="botao botao-principal" onClick={() => onVotar(participante)}>
                    Votar para eliminar
                </button>
            )}
        </article>
    );
}

export default ParticipanteCard;