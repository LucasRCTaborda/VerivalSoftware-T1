# Análise de cobertura (JaCoCo)

Ferramenta: JaCoCo 0.8.16 rodando junto com o `mvn test`.
Relatório gerado em `target/site/jacoco/index.html`.

Deixei a `VerivalApplication` fora da medição porque ela só tem o `main` que sobe o Spring,
não tem nenhuma regra do estacionamento. Antes de excluir ela aparecia com 2 linhas não cobertas
e puxava a porcentagem pra baixo sem motivo.

## Situação inicial (testes do T1)

Classe `CalculadoraEstacionamento`:

| Métrica      | Cobertos | Total | %      |
|--------------|---------:|------:|-------:|
| Instruções   | 137      | 151   | 90,7%  |
| Ramos        | 22       | 26    | 84,6%  |
| Linhas       | 30       | 32    | 93,8%  |
| Métodos      | 5        | 5     | 100%   |

### O que ficou sem cobrir

| Linha | Trecho | Problema |
|------:|--------|----------|
| 47 | `return vip ? valor * MULTIPLICADOR_VIP : valor;` (pernoite) | só o lado `vip == false` foi executado. Nenhum teste do T1 tinha pernoite com VIP |
| 69-70 | `if (entrada == null \|\| saida == null) throw ...` | nenhum teste passava `null`, então a exceção nunca foi lançada (2 ramos perdidos) |
| 72-73 | `if (saida.isBefore(entrada)) throw ...` | nunca testamos saída antes da entrada |

Ou seja, no T1 a gente focou bastante nos limites de horário e nas faixas de tarifa, mas
esqueceu da validação dos parâmetros e da combinação pernoite + VIP.

## Novos casos de teste (classe `CalculadoraEstacionamentoCoberturaTest`)

| ID   | Objetivo (trecho) | Entrada | Saída | VIP | Resultado esperado |
|------|-------------------|---------|-------|-----|--------------------|
| CT01 | entrada nula (l. 69) | `null` | 01/10/2026 10:00 | não | `IllegalArgumentException` |
| CT02 | saída nula, 2º lado do `\|\|` (l. 69) | 01/10/2026 10:00 | `null` | não | `IllegalArgumentException` |
| CT03 | saída antes da entrada (l. 72) | 01/10/2026 10:00 | 01/10/2026 09:59 | não | `IllegalArgumentException` |
| CT04 | limite: saída igual à entrada (l. 72) | 01/10/2026 10:00 | 01/10/2026 10:00 | não | R$ 0,00 |
| CT05 | pernoite com VIP (l. 47) | 01/10/2026 22:00 | 02/10/2026 09:00 | sim | R$ 25,00 |
| CT06 | VIP com hora adicional | 01/10/2026 10:00 | 01/10/2026 12:01 | sim | R$ 12,50 |

CT04 e CT06 não eram necessários pra fechar a cobertura, mas coloquei porque o CT03 testa
só um lado do limite do `isBefore`, e o VIP só tinha sido testado na tarifa fixa.

## Depois dos testes novos

| Métrica      | Antes  | Depois |
|--------------|-------:|-------:|
| Instruções   | 90,7%  | 100%   |
| Ramos        | 84,6%  | 100%   |
| Linhas       | 93,8%  | 100%   |

Cobertura 100% não quer dizer que os testes são bons, só que todo código foi executado.
Por isso o próximo passo é o teste de mutação com o PIT.
