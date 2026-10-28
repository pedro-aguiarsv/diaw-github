import { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  calcularResultado,
  calcularTotalVotos,
  obterVotos,
  zerarVotos,
} from '../services/votacaoService';

function Resultado() {
  const [votos, setVotos] = useState(obterVotos());

  const total = calcularTotalVotos(votos);
  const placar = calcularResultado(votos);

  function handleZerar() {
    if (window.confirm('Apagar todos os votos?'))
      zerarVotos();
    setVotos({}
    );
  }

  return (
      <main className="pagina">
        <h1>Parcial da eliminação</h1>
        <p className="total">
          <strong>{total}</strong> {total === 1 ? 'voto computado' : 'votos computados'}
        </p>

        {total === 0 ? (
            <div className="vazio">
              <p>
                Nenhum voto ainda. Escolha quem sai da roça para ver a parcial.
              </p>
              <Link to="/votacao" className="botao botao-principal">Ir para a votação</Link>
            </div>
        ) : (
            <>
              <ul className="placar">
                {placar.map((p, index) => (
                    <li key={p.id} className={index === 0 ? 'placar-item placar-lider' : 'placar-item'}>
                      <img src={p.foto} alt={p.nome} />
                      <div className="placar-info">
                        <h3>
                          {p.nome}
                          {index === 0 && <span className="selo-sair">Sairia hoje</span>}
                        </h3>
                        <div className="barra">
                          <div
                              className="barra-preenchida"
                              style={{ width: `${p.percentual}%` }}
                          />
                        </div>
                        <span className="placar-numeros">
                          {p.percentual.toFixed(1)}% ({p.votos} {p.votos === 1 ? 'voto' : 'votos'})
                        </span>
                      </div>
                    </li>
                ))}
              </ul>
              <button type="button" className="botao botao-zerar" onClick={handleZerar}>
                Zerar votação
              </button>
            </>
        )}
      </main>
  );
}

export default Resultado;