# ChatIA - API Inteligente de Orcamento Financeiro

## O que o projeto faz

O ChatIA e uma API inteligente construida com **Spring Boot** e **Spring AI** que permite gerenciar transacoes financeiras por meio de comandos de voz. O fluxo principal e:

1. O usuario fala um comando no navegador (Web Speech API)
2. O navegador transcreve a fala em texto automaticamente
3. A IA interpreta a intencao e seleciona a ferramenta adequada
4. A transacao e persistida ou consultada no banco de dados
5. A IA gera uma resposta em texto que pode ser lida em voz alta pelo navegador

A interface de voz funciona diretamente no navegador do celular ou PC, sem necessidade de chaves de API de audio.

## Como executar a aplicacao

### Pre-requisitos
- Java 17+
- [Ollama](https://ollama.com/) instalado

### Passo a passo

```bash
# 1. Instalar o Ollama (caso nao tenha)
curl -fsSL https://ollama.com/install.sh | sh

# 2. Baixar o modelo (primeira vez, ~2GB)
ollama pull llama3.2

# 3. Iniciar o Ollama (em um terminal)
ollama serve

# 4. Em outro terminal, executar a aplicacao
cd chatia
./mvnw spring-boot:run
```

A aplicacao estara disponivel em `http://localhost:8080` para fins de estudos

### Acessar pelo celular (via ngrok)

```bash
# Instalar ngrok
sudo snap install ngrok

# Expor a porta 8080
ngrok http 8080
```

Copie a URL HTTPS gerada e abra no navegador do celular.

## Melhorias implementadas

Sobre o projeto base da DIO, foram implementadas as seguintes evolucoes:

1. **Mais categorias financeiras**: Adicionadas categorias TRAVEL, EDUCATION, ENTERTAINMENT, HOUSING e HEALTH alem das originais (GROCERIES, PHARMA, AUTO)

2. **Resumo de gastos**: Novo use case `GetTransactionSummaryUseCase` que retorna totais por categoria e por categoria especifica, disponivel tanto via AI Tool Calling quanto REST

3. **Listagem completa**: Endpoint `GET /transactions` para listar todas as transacoes registradas

4. **Exclusao de transacoes**: Endpoint `DELETE /transactions/{id}` para remover transacoes

5. **Tratamento global de erros**: `GlobalExceptionHandler` com tratamento padronizado de excecoes com responses JSON

6. **Data de criacao**: Campo `createdAt` adicionado as transacoes para rastrear quando foram registradas

7. **System prompt melhorado**: Instrucoes mais detalhadas para a IA com categorias descritas e regras de apresentacao

8. **Interface de voz no navegador**: Pagina HTML com Web Speech API para reconhecimento de voz e sintese de voz, sem necessidade de API keys de audio

9. **100% gratuito**: Utiliza Ollama (LLM local) e Web Speech API (navegador), sem custos de API

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring AI 2.0.0** (Ollama integration)
- **Spring Data JPA** (persistencia)
- **H2 Database** (banco em memoria)
- **Ollama** com modelo llama3.2 (LLM local)
- **Web Speech API** (reconhecimento de voz e sintese de voz no navegador)
- **HTML/CSS/JavaScript** (interface de teste para celular)

## Como testar o fluxo principal

### Criar transacao via REST
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Supermercado Extra",
    "category": "GROCERIES",
    "amount": 15000
  }'
```

### Listar transacoes por categoria
```bash
curl http://localhost:8080/transactions/GROCERIES
```

### Listar todas as transacoes
```bash
curl http://localhost:8080/transactions
```

### Obter resumo de gastos
```bash
curl http://localhost:8080/transactions/summary
```

### Deletar uma transacao
```bash
curl -X DELETE http://localhost:8080/transactions/{id}
```

### Testar via voz (AI)
1. Acesse a pagina inicial no navegador (`http://localhost:8080` ou via ngrok)
2. Toque em **"Falar"** e diga o comando
3. Toque em **"Enviar para IA"**
4. A IA responde e voce pode ouvir com **"Ouvir resposta"**

### Exemplos de comandos por voz
- "Registre um gasto de 50 reais no mercado"
- "Paguei 30 reais na farmacia"
- "Quanto gastei com alimentacao?"
- "Me resuma todos os gastos"

## Arquitetura do projeto

```
src/main/java/com/chatia/
├── ChatiaApplication.java
├── ai/
│   └── AiChatService.java             # Servico de chat com Ollama
├── domain/
│   ├── Category.java                  # Enum de categorias financeiras
│   ├── Transaction.java               # Entidade de dominio
│   ├── TransactionId.java             # Identificador tipado
│   └── TransactionRepository.java     # Contrato do repositorio
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
    │       └── ErrorResponse.java
    └── persistence/
        ├── entity/
        │   └── TransactionEntity.java
        └── repository/
            ├── TransactionEntityRepository.java
            └── JpaTransactionRepository.java

src/main/resources/
├── application.properties
├── prompts/
│   └── system-message.st
└── static/
    └── index.html                     # Interface de teste por voz
```

## O que aprendi durante o desafio

- Integracao de IA com Spring Boot usando Spring AI
- Configuracao de ChatClient com Tool Calling para execucao de funcoes reais
- Arquitetura em camadas (Domain, Application, Infrastructure)
- Persistencia com JPA
- Tratamento padronizado de erros em APIs REST
- Web Speech API para reconhecimento de voz e sintese de voz
- Configuracao de Ollama como LLM local
r/local/bin/ollama ~/.ollama

```

## Baseado em

- [DIO Spring Boot Learning Track - Spring AI](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md)
- Projeto base do expert Poiani
