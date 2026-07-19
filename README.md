# ChatIA - API Inteligente de Orçamento Financeiro

## O que o projeto faz

O ChatIA é uma API inteligente construída com **Spring Boot** e **Spring AI** que permite gerenciar transações financeiras por meio de comandos de voz. O fluxo principal é:

1. O cliente envia um arquivo de áudio
2. O áudio é transcrito em texto (Whisper)
3. A IA interpreta a intenção e seleciona a ferramenta adequada
4. A transação é persistida ou consultada no banco de dados
5. Uma resposta em áudio é gerada para o usuário (TTS)

Além da interface por voz, a API também disponibiliza endpoints REST tradicionais para manipulação direta das transações.

## Como executar a aplicação

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Chave de API da OpenAI (`OPENAI_API_KEY`)

### Passo a passo

```bash
# 1. Iniciar o banco de dados MySQL
docker compose up -d

# 2. Configurar a chave da OpenAI
export OPENAI_API_KEY="sua_chave_aqui"

# 3. Executar a aplicação
./mvnw spring-boot:run

# 4. Executar os testes
./mvnw test
```

A aplicação estará disponível em `http://localhost:8080`

## Melhorias implementadas

Sobre o projeto base da DIO, foram implementadas as seguintes evoluções:

1. **Mais categorias financeiras**: Adicionadas categorias TRAVEL, EDUCATION, ENTERTAINMENT, HOUSING e HEALTH além das originais (GROCERIES, PHARMA, AUTO)

2. **Resumo de gastos**: Novo use case `GetTransactionSummaryUseCase` que retorna totais por categoria e por categoria específica, disponível tanto via AI Tool Calling quanto REST

3. **Listagem completa**: Endpoint `GET /transactions` para listar todas as transações registradas

4. **Exclusão de transações**: Endpoint `DELETE /transactions/{id}` para remover transações

5. **Tratamento global de erros**: `GlobalExceptionHandler` com tratamento padronizado de exceções com responses JSON

6. **Data de criação**: Campo `createdAt` adicionado às transações para rastrear quando foram registradas

7. **System prompt melhorado**: Instruções mais detalhadas para a IA com categorias descritas e regras de apresentação

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring AI 2.0.0** (OpenAI integration)
- **Spring Data JPA** (persistência)
- **MySQL 9.6** (banco de dados)
- **Lombok** (redução de boilerplate)
- **Docker Compose** (orçamento de serviços)
- **OpenAI API**:
  - GPT-4o-mini (chat/Tool Calling)
  - Whisper-1 (Speech-to-Text)
  - gpt-4o-mini-tts (Text-to-Speech)

## Como testar o fluxo principal

### Criar transação via REST
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Supermercado Extra",
    "category": "GROCERIES",
    "amount": 15000
  }'
```

### Listar transações por categoria
```bash
curl http://localhost:8080/transactions/GROCERIES
```

### Listar todas as transações
```bash
curl http://localhost:8080/transactions
```

### Obter resumo de gastos
```bash
curl http://localhost:8080/transactions/summary
```

### Deletar uma transação
```bash
curl -X DELETE http://localhost:8080/transactions/{id}
```

### Testar via áudio (AI)
```bash
curl -X POST http://localhost:8080/transactions/ai \
  -F "file=@audio_teste.m4a" \
  --output resposta.mp3
```

## Arquitetura do projeto

```
src/main/java/com/chatia/
├── ChatiaApplication.java
├── domain/
│   ├── Category.java              # Enum de categorias financeiras
│   ├── Transaction.java           # Entidade de domínio
│   ├── TransactionId.java         # Identificador tipado
│   └── TransactionRepository.java # Contrato do repositório
├── application/
│   ├── PersistTransactionUseCase.java
│   ├── ListTransactionsByCategoryUseCase.java
│   ├── ListAllTransactionsUseCase.java
│   ├── GetTransactionSummaryUseCase.java
│   ├── input/
│   │   └── PersistTransactionInput.java
│   └── output/
│       ├── TransactionOutput.java
│       └── SummaryOutput.java
└── infrastructure/
    ├── http/
    │   ├── TransactionController.java
    │   ├── GlobalExceptionHandler.java
    │   ├── request/
    │   │   └── TransactionRequest.java
    │   └── response/
    │       ├── TransactionResponse.java
    │       ├── SummaryResponse.java
    │       └── ErrorResponse.java
    └── persistence/
        ├── entity/
        │   └── TransactionEntity.java
        └── repository/
            ├── TransactionEntityRepository.java
            └── JpaTransactionRepository.java
```

## O que aprendi durante o desafio

- Integração de IA com Spring Boot usando Spring AI
- Configuração de ChatClient com Tool Calling para execução de funções reais
- Transcrição de áudio em texto com Whisper
- Geração de voz a partir de texto com TTS
- Arquitetura em camadas (Domain, Application, Infrastructure)
- Persistência com JPA e MySQL
- Tratamento padronizado de erros em APIs REST

## Baseado em

- [DIO Spring Boot Learning Track - Spring AI](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md)
- Projeto base do expert Poiani
