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

    @SuppressWarnings("unused")
    private static Stream<Arguments> fornecerCasosDeTeste() {
        return Stream.of(
                // CA 08: Cortesia — tempo == 19 min
                // Bug em V1: retornaria 19.0 (minutos * 1).
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 19), false, 0.0),
                // CA 09: Cortesia — tempo == 20 min (Limite da cortesia)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 20), false, 0.0),
                // CA 10: Tarifa Fixa — tempo == 21 min (Acima da cortesia)

                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 21), false, 15.0),
                // CA 11: Tarifa Fixa — tempo == 59 min
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 10, 59), false, 15.0),
                // CA 12: Tarifa Fixa — tempo == 60 min (Limite da 1ª hora)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 0), false, 15.0),
                // CA 13: Adicional de 1h — tempo == 61 min (Início da 2ª hora)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 1), false, 20.0),
                // CA 14: Adicional de 1h — tempo == 119 min
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 11, 59), false, 20.0),
                // CA 15: Adicional de 1h — tempo == 120 min (Limite da 2ª hora)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 12, 0), false, 20.0),
                // CA 16: Adicional de 2h — tempo == 121 min (Início da 3ª hora)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 12, 1), false, 25.0),
                // CA 17: Adicional de 3h — tempo == 240 min (4 horas, mesmo dia)
                Arguments.of(LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 14, 0), false, 30.0),
                // CA 18: Pernoite — data_saida > data_entrada && hora_saida > 08:00
                Arguments.of(LocalDateTime.of(2026, 10, 1, 22, 0),
                        LocalDateTime.of(2026, 10, 2, 8, 0), false, 50.0)
        );
    }

    @ParameterizedTest(name = "CA {index + 7}: entrada={0}, saída={1}, VIP={2} → R$ {3}")
    @MethodSource("fornecerCasosDeTeste")
    void testarCalculoTarifa(LocalDateTime entrada, LocalDateTime saida, boolean vip, double tarifaEsperada) {
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        assertEquals(tarifaEsperada, calc.calcularTarifa(entrada, saida, vip), 0.01);
    }

    @Test
    void testarCA19_clienteVIP() {
        // CA 19: cliente vip — isVIP == true → valor total * 0.5
        // Cenário: 30 min de permanência, tarifa fixa R$ 15,00
        // Esperado: R$ 15,00 * 0.5 = R$ 7,50
        // Bug em V1–V4: sem desconto VIP, retornaria R$ 15,00 em vez de R$ 7,50.
        // Corrigido em V5 com multiplicador VIP 0.5.
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        double resultado = calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 10, 30),
                true);
        assertEquals(7.5, resultado, 0.01);
    }

    @Test
    void testarCA20_clienteComum() {
        // CA 20: cliente comum — isVIP == false → valor total não alterado
        // Cenário: 30 min de permanência, tarifa fixa R$ 15,00
        // Esperado: R$ 15,00 (sem alteração)
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        double resultado = calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 10, 30),
                false);
        assertEquals(15.0, resultado, 0.01);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> fornecerCasosEntradaValida() {
        return Stream.of(
                // Entrada 08:00 — limite inferior válido
                Arguments.of(LocalDateTime.of(2026, 10, 1, 8, 0),
                        LocalDateTime.of(2026, 10, 1, 9, 0), false),
                // Entrada 23:59 — limite superior válido
                Arguments.of(LocalDateTime.of(2026, 10, 1, 23, 59),
                        LocalDateTime.of(2026, 10, 2, 0, 30), false)
        );
    }

    @ParameterizedTest(name = "Entrada válida {index}: entrada={0}")
    @MethodSource("fornecerCasosEntradaValida")
    void testarEntradaValida(LocalDateTime entrada, LocalDateTime saida, boolean vip) {
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        calc.calcularTarifa(entrada, saida, vip);
    }

    @Test
    void testarEntradaInvalida_0759() {
        // Entrada 07:59 — antes do horário permitido
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(
                        LocalDateTime.of(2026, 10, 1, 7, 59),
                        LocalDateTime.of(2026, 10, 1, 9, 0), false));
    }

    @Test
    void testarEntradaInvalida_0000() {
        // Entrada 00:00 — fora do horário permitido

        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(
                        LocalDateTime.of(2026, 10, 1, 0, 0),
                        LocalDateTime.of(2026, 10, 1, 1, 0), false));
    }

    @Test
    void testarSaidaValida_0159() {
        // Saída 01:59 — antes do bloqueio
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 23, 59),
                LocalDateTime.of(2026, 10, 2, 1, 59), false);
    }

    @Test
    void testarSaidaValida_0800() {
        // Saída 08:00 — após o bloqueio
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 22, 0),
                LocalDateTime.of(2026, 10, 2, 8, 0), false);
    }

    @Test
    void testarSaidaInvalida_0200() {
        // Saída 02:00 — início do bloqueio
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(
                        LocalDateTime.of(2026, 10, 1, 22, 0),
                        LocalDateTime.of(2026, 10, 2, 2, 0), false));
    }

    @Test
    void testarSaidaInvalida_0759() {
        // Saída 07:59 — fim do bloqueio
        CalculadoraEstacionamento calc = new CalculadoraEstacionamento();
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(
                        LocalDateTime.of(2026, 10, 1, 22, 0),
                        LocalDateTime.of(2026, 10, 2, 7, 59), false));
    }
}
