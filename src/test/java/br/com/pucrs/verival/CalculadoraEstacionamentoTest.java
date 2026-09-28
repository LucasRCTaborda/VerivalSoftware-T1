package br.com.pucrs.verival;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class CalculadoraEstacionamentoTest {

    // TABELA DE CASOS DE TESTE — Tempo de Permanência e Tarifas
    private static Stream<Arguments> fornecerCasosDeTeste() {
        return Stream.of(
                // Caso 1: Cortesia — 19 min (<= 20)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 19), false, 0.0),
                // Caso 2: Cortesia — 20 min (== 20, limite)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 20), false, 0.0),
                // Caso 3: Passou cortesia — 21 min (> 20)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 21), false, 15.0),
                // Caso 4: Tarifa fixa — 59 min (<= 60)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 59), false, 15.0),
                // Caso 5: Tarifa fixa — 60 min (== 60, limite)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 0), false, 15.0),
                // Caso 6: Adicional — 61 min (> 60, 1ª hora adicional)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 1), false, 20.0),
                // Caso 7: Adicional — 1h59 (119 min, ainda na 1ª hora adicional)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 59), false, 20.0),
                // Caso 8: Adicional — 2h00 (120 min, == limite da 1ª hora adicional)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 12, 0), false, 20.0),
                // Caso 9: Adicional — 2h01 (121 min, > limite, 2ª hora adicional)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 12, 1), false, 25.0),
                // Caso 10: Pernoite — saída no dia seguinte às 08:00
                Arguments.of(LocalDateTime.of(2026, 10, 1, 22, 0),
                        LocalDateTime.of(2026, 10, 2, 8, 0), false, 50.0),
                // Caso 11: VIP + Cortesia — 20 min, VIP (0 × 0.5 = 0)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 20), true, 0.0),
                // Caso 12: VIP + Tarifa Fixa — 30 min, VIP (15 × 0.5 = 7.50)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 30), true, 7.5),
                // Caso 13: VIP + Adicional — 2h01, VIP (25 × 0.5 = 12.50)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 12, 1), true, 12.5),
                // Caso 14: VIP + Pernoite — pernoite, VIP (50 × 0.5 = 25.00)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 22, 0),
                        LocalDateTime.of(2026, 10, 2, 8, 0), true, 25.0)
        );
    }

}
