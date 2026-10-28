import { useState } from 'react';
import { Link } from 'react-router-dom';
import ParticipanteCard from '../components/ParticipanteCard';
import { listarForaDaRoca, listarNaRoca, registrarVoto } from '../services/votacaoService';

function Votacao() {
    const [ultimoVoto, setUltimoVoto] = useState(null);
    const naRoca = listarNaRoca();
    const foraDaRoca = listarForaDaRoca();
    const [contador, setContador] = useState(0);

    function handleVotar(participante) {
        registrarVoto(participante.id);
        setUltimoVoto(participante);
        setContador(contador + 1);
      }

    return (
        <main className="pagina">
            <h1>Quem você quer eliminar?</h1>
            <p className="subtitulo">Cada clique conta um voto para tirar o peão da fazenda.</p>

            {ultimoVoto && (
                <div className="aviso-voto">
                    <p>
                        Voto registrado para eliminar <strong>{ultimoVoto.nome}</strong>. Você já votou {contador} vezes.
                    </p>
                    <Link to="/resultado">Ver resultado</Link>
                </div>
            )}

            <section className="grade-roca">
                {naRoca.map((p) => (
                    <ParticipanteCard
                        key={p.id}
                        participante={p}
                        onVotar={handleVotar}
                    />
                ))}
            </section>

            <section className="demais">
                <h2>Demais peões</h2>
                <p>Fora da roça nesta semana, não podem receber votos.</p>
                <div className="grade-peoes">
                    {foraDaRoca.map((p) => (
                        <ParticipanteCard key={p.id} participante={p} />
                    ))}
                </div>
            </section>
        </main>
    );
}

export default Votacao;