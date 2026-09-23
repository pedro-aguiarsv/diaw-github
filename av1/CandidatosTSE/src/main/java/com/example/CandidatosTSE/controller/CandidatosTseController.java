@Controller

public class CandidatosTseController {
    private final CandidatosTseService candidatosTseService;
    public CandidatosTseController(CandidatosTseService candidatosTseService) {
        this.candidatosTseService = candidatosTseService;
    }
     
}






























[10,0 pts] Questão 1) Construa o arquivo controller/CandidatosTseController.java e realize 
as chamadas necessárias para os arquivos disponibilizados na prova, chamados 
service/CandidatosTseService.java e model/Candidato.java. 
 
Dicas: 
• O Controller é uma classe anotada com @Controller (não @RestController, pois ele 
retorna uma view do Thymeleaf, não JSON). 
• Injete o CandidatosTseService via construtor: 
 
 
• Crie um único endpoint (GET /) que atenda tanto o carregamento inicial da página 
quanto os filtros; os filtros são simplesmente parâmetros de query string 
(?cargo=...&partido=...&texto=...). 
• Use @RequestParam(required = false) para cada filtro, já que o usuário pode não 
preencher nenhum: 
• Confira a assinatura exata do método filtrar(...) no CandidatosTseService fornecido; os 
parâmetros e a ordem precisam bater certinho. 
• Você vai precisar passar pro Model (para o Thymeleaf usar no HTML): 
o a lista filtrada de candidatos; 
o o total de candidatos encontrados (útil pra mensagem "X candidato(s) 
encontrado(s)" e pra saber quando mostrar "nenhum resultado"); 
o as opções de filtro (lista de cargos e de partidos distintos), o service já deve ter 
métodos prontos para isso (algo como listarCargos() e listarPartidos()); 
o os valores atualmente selecionados de cada filtro, para que o formulário 
"lembre" o que o usuário digitou/selecionou depois de recarregar a página. 
• Cuidado com null: se o parâmetro vier null, ao devolver pro HTML prefira transformar 
em string vazia (""), assim evita null aparecendo escrito na tela ou quebrando 
comparações no Thymeleaf. 
• O método deve retornar o nome da view (uma String), não o objeto HTML, o 
Thymeleaf resolve isso a partir do nome (ex.: return "index"; aponta pra 
templates/index.html). 
[10,0 pts] Questão 2) Construa o arquivo templates/index.html 
Dicas: 
Estrutura geral da página 
• Declare o namespace do Thymeleaf no <html>: 
o <html lang="pt-BR" xmlns:th="http://www.thymeleaf.org"> 
• Referencie o CSS com th:href (não href puro), para que o Thymeleaf resolva o caminho 
junto com o contexto da aplicação: 
o <link rel="stylesheet" th:href="@{/css/style.css}"> 
2 
O formulário de filtro 
• Use method="get" — assim os filtros viram parâmetros de URL, sem precisar de 
JavaScript nem de outro endpoint: 
o <form method="get" th:action="@{/}" class="filtros"> 
• Para o campo de texto, use th:value pra manter o que o usuário digitou depois de filtrar: 
o <input type="text" name="texto" th:value="${textoSelecionado}"> 
• Para os <select> de cargo/partido, você vai precisar de duas coisas: uma opção "Todos" 
(valor vazio) e as opções dinâmicas vindas da lista do Model, usando th:each. Preste 
atenção em marcar qual opção deve ficar selecionada por padrão, comparando o valor 
da opção com o valor atualmente selecionado: 
A listagem de candidatos 
• Use th:each numa <div> (ou outro elemento) pra iterar a lista de candidatos vinda do 
Model: 
o <div class="card" th:each="cand : ${candidatos}"> 
• Pra foto, monte a URL concatenando o caminho da pasta com o nome do arquivo (o 
Candidato já deve ter um método pronto pra isso, ex.: getNomeArquivoFoto()): 
o <img th:src="@{'/images/candidatos/' + ${cand.nomeArquivoFoto}}" 
th:alt="${cand.nomeUrna}" class="foto-candidato"> 
• Use th:text para exibir os campos do candidato (nome de urna, número, cargo, partido, 
etc.). 
• Esconda com th:if os campos que às vezes vêm vazios ou sem valor útil (situação, 
idade, ocupação, escolaridade): 
3 
• Contagem de resultados e lista vazia: 
• Os botões do formulário: 
o <button type="submit">Filtrar</button> 
o <a th:href="@{/}" class="limpar">Limpar</a> 
[5,0 pts] Questão 3) Construa o arquivo static/css/style.css 
Dicas: 
• Use CSS Grid pra montar a grade de cards de forma responsiva, sem precisar definir 
manualmente quantas colunas cabem em cada tamanho de tela: 
o .grade-candidatos { 
display: grid; 
grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); 
gap: 12px; 
} 
• Padronize a proporção da foto com aspect-ratio, evitando fotos esticadas ou cortadas de 
forma estranha: 
o .foto-candidato { 
width: 100%; 
aspect-ratio: 161 / 225; 
object-fit: cover; 
} 
Observações: - Não se esqueça de copiar o arquivo data/candidatos/consulta_cand_2026_MG.csv para a pasta 
resources do projeto. - Copie também a pasta images/candidatos para a pasta static do projeto. - Leia o arquivo README.md antes de começar a prova.