# Calculadora de Estacionamento — Verificação e Validação

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
│   │   ├── VerivalApplication.java
│   │   └── CalculadoraEstacionamento.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/br/com/pucrs/verival/
    │   ├── VerivalApplicationTests.java
    │   └── CalculadoraEstacionamentoTest.java
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



Resultado 


Durante o desenvolvimento, utilizei testes parametrizados para aplicar a técnica de análise do valor limite. Na primeira etapa da implementação, o foco foi validar os horários de entrada, estruturando testes em torno dos limites de 08:00 e 23:59, garantindo que o sistema gerasse os resultados esperados para o controle de acesso.

Em seguida, avancei para a validação dos horários de saída. Nesse momento, identifiquei a necessidade de adotar estritamente o formato de 24 horas aliado à data completa (dia, mês e ano) de entrada e saída. Essa mudança corrigiu ambiguidades críticas de validação que ocorriam ao pensar apenas em ciclos de 12 horas — por exemplo, a incapacidade do sistema de diferenciar 01:22 da madrugada de 13:22 da tarde sem um contexto temporal maior.

Ao comparar as instâncias exatas de tempo, tornou-se possível verificar facilmente se a saída ocorria no dia seguinte. Essa abordagem arquitetural foi fundamental para implementar com precisão o bloqueio de saídas no intervalo restrito das 02:00 às 07:59. Após garantir o funcionamento integral e a robustez das regras de restrição de horário, a última etapa do desenvolvimento consistiu em aplicar e testar a lógica de cálculo das tarifas finais a serem pagas.

Na abordagem de tempo de pernanencia e tarifas , tive um problema chato com o a tarifa fixa e o adicional , inicialmente eu nao estava arredondando para cima e sim truncando o valor, e assim usando os casos de testes conseguir perceber que estava errado e corrigi.