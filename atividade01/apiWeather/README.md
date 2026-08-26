# API REST de Clima

Projeto desenvolvido em **Spring Boot** para a disciplina de Desenvolvimento e Integração de Aplicações Web (DIAW), como parte da Atividade 01.

A aplicação consulta a API pública **Open-Meteo** e disponibiliza, por meio de um endpoint REST próprio, as informações meteorológicas atuais de uma cidade. A cidade consultada é definida diretamente no código (latitude/longitude fixas dentro do `ClimaService`), e pode ser alterada trocando essas coordenadas e a rota do endpoint.

**Resultado oficial entregue:** consulta ao clima de **Curitiba - PR**.

---

## Integrantes

- Pedro Aguiar

---

## Tecnologias utilizadas

- Java 25
- Spring Boot 4.1.1 (starter `spring-boot-starter-webmvc`)
- Maven
- RestClient (Spring Web) para consumo de API externa
- API pública [Open-Meteo](https://open-meteo.com/) (não exige API Key)

---

## Estrutura do projeto

```
apiWeather/
├── pom.xml
├── README.md
└── src/main/java/com/example/apiWeather/
    ├── ApiWeatherApplication.java      # classe principal, sobe a aplicação
    ├── controller/
    │   └── ClimaController.java        # recebe a requisição HTTP e delega ao service
    └── service/
        └── ClimaService.java           # consulta a API externa Open-Meteo
```

---

## Configuração

A API utilizada (Open-Meteo) é **gratuita e não exige API Key**, portanto não é necessário configurar nenhum token no `application.properties`.

A cidade consultada é fixada por latitude/longitude dentro de `ClimaService.java`. Para trocar a cidade, basta atualizar essas duas coordenadas e o texto da rota em `ClimaController.java`.

---

## Endpoint disponível

### Consultar clima da cidade configurada

```
GET /clima/{cidade}
```

No estado atual do projeto, a rota ativa é:

```
GET /clima/curitiba
```

Exemplo de uso local:

```
http://localhost:8080/clima/curitiba
```

### Exemplo de resposta (JSON)

```json
{
  "latitude": -25.413006,
  "longitude": -49.241608,
  "timezone": "America/Sao_Paulo",
  "current": {
    "time": "2026-08-26T18:45",
    "temperature_2m": 16.3,
    "relative_humidity_2m": 92,
    "wind_speed_10m": 4.6,
    "wind_direction_10m": 9
  },
  "daily": {
    "time": ["2026-08-26", "2026-08-27", "..."],
    "temperature_2m_max": [20.8, 24.3, "..."],
    "temperature_2m_min": [10.9, 12.4, "..."]
  }
}
```

Campos retornados: temperatura atual (`current.temperature_2m`), umidade do ar (`current.relative_humidity_2m`), velocidade do vento (`current.wind_speed_10m`), direção do vento (`current.wind_direction_10m`) e temperatura máxima/mínima do dia (primeiro valor de `daily.temperature_2m_max` / `daily.temperature_2m_min`).

---

## Como executar localmente

### 1. Clonar o repositório

```
git clone https://github.com/pedro-aguiarsv/diaw-github.git
```

### 2. Entrar na pasta do projeto

```
cd atividade01/apiWeather
```

### 3. Compilar o projeto

```
mvn clean install
```

### 4. Executar a aplicação

```
mvn spring-boot:run
```

### 5. Acessar a API

No navegador ou em um cliente HTTP (Postman, Insomnia):

```
http://localhost:8080/clima/curitiba
```

---

## Licença

Projeto acadêmico, desenvolvido para fins educacionais na disciplina de DIAW.
