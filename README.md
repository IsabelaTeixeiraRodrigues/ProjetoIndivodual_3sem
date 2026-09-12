# PokéLab

Projeto Individual da Sprint (3º semestre) - sistema para criar e treinar Pokémon, com Front-end em HTML/CSS/JS Vanilla consumindo uma API REST em Spring Boot + JdbcTemplate.

O usuário monta seu próprio Pokémon (nome, nível, data de captura, tipo, ataques e se é shiny), o cadastro é validado no front-end e no back-end, persistido em banco relacional, e o time atual é exibido dinamicamente na tela ao lado.

## Funcionalidades

- Cadastro de Pokémon com validação de campos (nome, nível 1–100, data de captura não futura, tipo, de 1 a 4 ataques, shiny sim/não)
- Carregamento dinâmico do dropdown de **Tipos** e da lista de **Ataques** via API (sem valores fixos no HTML)
- Listagem do time atual do jogador
- Remoção ("liberar") de um Pokémon do time
- Edição do nome do time

## Tecnologias

**Front-end**
- HTML5, CSS3 e JavaScript puro (Vanilla) - sem frameworks ou bibliotecas
- `fetch()` para consumo da API

**Back-end**
- Java + Spring Boot
- JdbcTemplate para acesso a dados
- Banco de dados relacional (H2)

## Estrutura do repositório

```
ProjetoIndivodual_3sem/
├── backend/
│   ├── src/main/java/com/ProjetoIndividual/sprint1/
│   │   ├── PokemonController.java
│   │   ├── Pokemon.java
│   │   ├── Ataque.java
│   │   ├── Tipo.java
│   │   └── Time.java
│   ├── pom.xml
│   └── ...
│
├── frontend/
│   ├── index.html
│   ├── index.css
│   └── README.md
│
└── README.md
```

> Ajuste esta árvore caso a organização real de pastas do seu repositório seja diferente.

## Como executar

### Back-end (Spring Boot)

1. Entre na pasta `backend/`
2. Rode a aplicação com o Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   ou pela sua IDE, executando a classe principal (`@SpringBootApplication`)
3. A API sobe em `http://localhost:8080`
4. O console do H2 (se habilitado) fica disponível em `http://localhost:8080/h2-console`

### Front-end (HTML/CSS/JS)

1. Entre na pasta `frontend/`
2. Abra `index.html` com a extensão **Live Server** do VS Code (ou outro servidor estático)
3. A página faz as requisições para `http://localhost:8080`, então o back-end precisa estar rodando antes

> O CORS já está liberado no back-end (`@CrossOrigin(origins = "*")`), permitindo que o front-end (rodando em outra origem via Live Server) se comunique com a API.

## Banco de dados

Tabelas utilizadas pela aplicação:

- **tipo** `(id, nome)`
- **ataque** `(id, nome)`
- **time** `(id, nome)`
- **pokemon** `(id, nome, nivel, data_captura, shiny, tipo_id, time_id)`
- **pokemon_ataque** `(pokemon_id, ataque_id)` - tabela associativa (relação N:N entre Pokémon e Ataques)

Script de criação sugerido (`schema.sql`):

```sql
CREATE TABLE tipo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(30) NOT NULL
);

CREATE TABLE ataque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE time (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE pokemon (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(20) NOT NULL,
    nivel INT NOT NULL,
    data_captura DATE NOT NULL,
    shiny BOOLEAN NOT NULL DEFAULT FALSE,
    tipo_id INT NOT NULL,
    time_id INT NOT NULL,
    FOREIGN KEY (tipo_id) REFERENCES tipo(id),
    FOREIGN KEY (time_id) REFERENCES time(id)
);

CREATE TABLE pokemon_ataque (
    pokemon_id INT NOT NULL,
    ataque_id INT NOT NULL,
    PRIMARY KEY (pokemon_id, ataque_id),
    FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE,
    FOREIGN KEY (ataque_id) REFERENCES ataque(id)
);
```

> ️ Este script foi inferido a partir do código (`PokemonController` e classes de modelo). Substitua pelo `schema.sql`/`data.sql` reais do projeto, se já existirem.

## Documentação da API

Base URL: `http://localhost:8080/pokemon`

### `GET /pokemon/tipos`
Retorna todos os tipos de Pokémon cadastrados, usados para popular o dropdown do formulário.

**Resposta - 200 OK**
```json
[
  { "id": 1, "nome": "Fogo" },
  { "id": 2, "nome": "Água" }
]
```

### `GET /pokemon/ataques`
Retorna todos os ataques disponíveis, usados para popular as checkboxes do formulário.

**Resposta - 200 OK**
```json
[
  { "id": 1, "nome": "Lança-Chamas" },
  { "id": 2, "nome": "Investida" }
]
```

### `GET /pokemon/pokemons`
Retorna os Pokémon do time atual (`time_id = 1`).

**Resposta - 200 OK**
```json
[
  {
    "id": 1,
    "nome": "Charizard",
    "nivel": 36,
    "dataCaptura": "2025-01-10",
    "shiny": false,
    "tipo_id": 1,
    "time_id": 1
  }
]
```

### `POST /pokemon/pokemons/criar?ataqueIds=1&ataqueIds=2`
Cadastra um novo Pokémon e associa de 1 a 4 ataques a ele.

**Parâmetros de query:** `ataqueIds` (um ou mais IDs de ataque, repetidos na query string)

**Corpo da requisição**
```json
{
  "nome": "Charizard",
  "nivel": 36,
  "dataCaptura": "2025-01-10",
  "tipo_id": 1,
  "shiny": false
}
```

**Resposta - 201 Created**
```json
{
  "id": 5,
  "nome": "Charizard",
  "nivel": 36,
  "dataCaptura": "2025-01-10",
  "shiny": false,
  "tipo_id": 1,
  "time_id": 1
}
```

**Resposta - 400 Bad Request** (exemplos de validação)
```json
{ "mensagem": "Nome inválido" }
```
```json
{ "mensagem": "Nível deve estar entre 1 e 100" }
```
```json
{ "mensagem": "Ataques inválidos" }
```

Regras de negócio validadas no back-end:
- Nome: entre 2 e 20 caracteres
- Nível: entre 1 e 100
- Data de captura: obrigatória e não pode ser no futuro
- Tipo: deve existir na tabela `tipo`
- Ataques: entre 1 e 4, sem duplicados, e todos precisam existir na tabela `ataque`

### `DELETE /pokemon/pokemons/{id}`
Remove um Pokémon do time.

**Resposta - 204 No Content**: remoção realizada com sucesso
**Resposta - 404 Not Found**: Pokémon com o `id` informado não existe

### `PUT /pokemon/time/atualizarNome`
Atualiza o nome do time (fixo em `id = 1`).

**Corpo da requisição**
```json
{ "nome": "Equipe Fogo e Gelo" }
```

**Resposta - 200 OK**
```json
{ "id": 1, "nome": "Equipe Fogo e Gelo" }
```

**Resposta - 400 Bad Request**
```json
{ "mensagem": "Nome inválido" }
```
> Nome do time deve ter entre 2 e 50 caracteres.

## Validações

| Campo | Front-end | Back-end |
|---|---|---|
| Nome do Pokémon | 2–20 caracteres | 2–20 caracteres |
| Nível | 1–100 | 1–100 |
| Data de captura | obrigatória, não futura | obrigatória, não futura |
| Tipo | obrigatório | precisa existir no banco |
| Ataques | 1 a 4 selecionados | 1 a 4, sem duplicados, precisam existir no banco |
| Shiny | obrigatório (sim/não) | - |
| Nome do time | 2–50 caracteres | 2–50 caracteres |

A validação do front-end existe para dar retorno rápido ao usuário, mas o back-end nunca confia apenas nela - toda requisição é revalidada na API, mesmo vinda de ferramentas como Postman/Insomnia/curl.

## Autora

Isabela Teixeira Rodrigues
