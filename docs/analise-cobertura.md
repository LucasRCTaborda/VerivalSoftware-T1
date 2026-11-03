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
