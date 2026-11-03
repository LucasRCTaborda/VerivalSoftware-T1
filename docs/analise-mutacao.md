# Análise de mutação (PIT)

Ferramenta: PIT 1.30.1 com o plugin `pitest-junit5-plugin` 1.2.3.

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

Relatório gerado em `target/pit-reports/index.html`.

Configuração usada:
- classe mutada: só a `CalculadoraEstacionamento` (é onde estão as regras)
- testes: as classes `CalculadoraEstacionamento*` (o teste do contexto do Spring não chama a calculadora e só deixa a execução mais lenta)
- mutadores: grupo `STRONGER`. Escolhi esse grupo em vez do `DEFAULTS` porque ele também tira as condições
  dos `if` (`RemoveConditionalMutator`), e isso é bem útil aqui, já que quase toda a regra está em `if`

## Resultado com os testes do T1 (antes dos testes de cobertura)

40 mutantes gerados, **31 mortos (78%)**, 1 sem cobertura.

| Linha | Mutador | Status | Por que sobreviveu |
|------:|---------|--------|--------------------|
| 35 | VoidMethodCall: remove a chamada `validarParametros` | SURVIVED | nenhum teste passava parâmetro inválido |
| 41 | RemoveConditional_EQUAL_IF | SURVIVED | ver abaixo |
| 47 | Math: troca `*` por `/` no VIP do pernoite | NO_COVERAGE | não tinha pernoite VIP |
| 47 | RemoveConditional_EQUAL_ELSE (`vip` vira sempre falso) | SURVIVED | idem |
| 58 | ConditionalsBoundary (`<= 60` vira `< 60`) | SURVIVED | equivalente |
| 58 | RemoveConditional_ORDER_ELSE (`<= 60` vira sempre falso) | SURVIVED | equivalente |
| 69 | RemoveConditional_EQUAL_IF / _ELSE (teste de `null`) | SURVIVED (2) | nenhum teste com `null` |
| 72 | RemoveConditional_EQUAL_ELSE (`saida.isBefore`) | SURVIVED | nenhum teste com saída antes da entrada |

## Resultado depois dos testes de cobertura (CT01 a CT06)

40 mutantes, **37 mortos (93%)**. Os testes que fecharam a cobertura já mataram 6 dos 9.
Sobraram 3:

| Linha | Mutador | Mutação |
|------:|---------|---------|
| 41 | RemoveConditional_EQUAL_IF | a parte `!saida...isBefore(08:00)` do pernoite vira sempre verdadeira |
| 58 | ConditionalsBoundary | `minutos <= 60` vira `minutos < 60` |
| 58 | RemoveConditional_ORDER_ELSE | `minutos <= 60` vira sempre falso |

### Linha 41: problema nos testes

Com esse mutante, qualquer saída em outro dia já conta como pernoite, mesmo que seja de madrugada
(antes das 02:00). Por exemplo, entrada 23:59 e saída 01:59 do dia seguinte:
- original: 120 min = R$ 15 + R$ 5 = **R$ 20,00**
- mutante: 1 pernoite = **R$ 50,00**

O T1 até tinha um teste com esse cenário (`testarSaidaValida_0159`), mas ele só chama o método e não
confere o valor (não tem `assertEquals`). Ele só verifica que não lança exceção. O mesmo acontece com
`testarSaidaValida_0800` e com o parametrizado `testarEntradaValida`. Isso é uma coisa que a cobertura
não mostra: a linha é executada, mas o resultado não é verificado.

### Linha 58: mutantes equivalentes

- `<` no lugar de `<=`: com exatamente 60 min o código vai pro `else`, e lá
  `ceil((60 - 60) / 60.0) = 0` horas adicionais, ou seja, R$ 15,00, igual ao original.
- `if` sempre falso: entre 21 e 60 min tudo cai no `else`, mas `(minutos - 60) / 60.0` fica entre
  -0,65 e 0, e o `ceil` disso dá 0. De novo R$ 15,00.

Nos dois casos o comportamento é idêntico ao original para qualquer entrada, então nenhum teste
consegue matar esses mutantes. Eles ficam de fora do cálculo do score "real".
