# Revisão dos modelos

## Versão 1 — leitura do enunciado

O sistema tem dois papéis: cliente e agente (empresa ou banco). O cliente introduz, altera, consulta e cancela pedidos. O agente altera e avalia. O parecer positivo libera a execução do contrato, que pode ter um crédito concedido por um banco. O cliente guarda identificação, profissão e até três rendimentos. O automóvel tem matrícula, ano, marca, modelo e placa, e pode ser de cliente, empresa ou banco.

## Versão 2 — o que o protótipo precisou explicitar

- O login é uma conta (`Usuario`). O cadastro de cliente é outro agregado, criado depois, para o CRUD ficar separado da autenticação.
- O status do pedido passou a ser uma máquina: introduzido, em análise, parecer positivo, parecer negativo, contrato em execução, cancelado.
- O carro fica indisponível enquanto o pedido ocupa o período, e volta a ficar livre se o pedido for cancelado ou receber parecer negativo.
- O contrato de crédito é opcional. A empresa pode executar o contrato sem crédito. O banco pode conceder o crédito e, nesse caso, a execução começa junto.
- A interface em React é o subsistema de páginas. O Spring Boot MVC é o subsistema de pedidos e contratos. Os dois conversam por JSON.

Os arquivos `.puml` desta pasta são a versão 2. A versão 1 fica registrada neste texto.
