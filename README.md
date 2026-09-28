

## 🛠️ Stack Tecnológica

| Camada        | Tecnologia                    |
|---------------|-------------------------------|
| Linguagem     | Java 17                       |
| Framework     | Spring Boot 3.3.4             |
| Build         | Maven                         |
| Banco (dev)   | H2 (em memória)               |
| Testes        | JUnit 5 + Mockito + AssertJ   |
| Cobertura     | JaCoCo                        |
| Utilitários   | Lombok                        |

## 📂 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/pucrs/verival/
│   │   └── VerivalApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/br/com/pucrs/verival/
    │   └── VerivalApplicationTests.java
    └── resources/
        └── application.properties
```

## 🚀 Como Executar

```bash
# Compilar e rodar os testes
mvn clean test

# Rodar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## 📊 Relatório de Cobertura (JaCoCo)

Após rodar os testes, o relatório de cobertura é gerado em:
```
target/site/jacoco/index.html
```

## 📋 Comandos Úteis

```bash
# Rodar apenas os testes
mvn test

# Gerar relatório de cobertura
mvn test jacoco:report

# Limpar e reconstruir
mvn clean install
```
