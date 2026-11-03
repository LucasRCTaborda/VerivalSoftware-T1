package br.com.pucrs.verival;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Casos de teste novos do T2, criados a partir do relatorio do JaCoCo.
 * Cada teste cobre um trecho que os testes do T1 nao passavam.
 */
public class CalculadoraEstacionamentoCoberturaTest {

    private CalculadoraEstacionamento calc;

    @BeforeEach
    void setUp() {
        calc = new CalculadoraEstacionamento();
    }

    @Test
    void testarCT01_entradaNula() {
        // linha 69: entrada == null
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(null, LocalDateTime.of(2026, 10, 1, 10, 0), false));
    }

    @Test
    void testarCT02_saidaNula() {
        // linha 69: entrada ok mas saida == null (segundo lado do ||)
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(LocalDateTime.of(2026, 10, 1, 10, 0), null, false));
    }

    @Test
    void testarCT03_saidaAntesDaEntrada() {
        // linha 72: saida 1 minuto antes da entrada
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularTarifa(
                        LocalDateTime.of(2026, 10, 1, 10, 0),
                        LocalDateTime.of(2026, 10, 1, 9, 59), false));
    }

    @Test
    void testarCT04_saidaIgualEntrada() {
        // limite do isBefore: saida == entrada nao pode dar erro, 0 min e cortesia
        double resultado = calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 10, 0), false);
        assertEquals(0.0, resultado, 0.01);
    }

    @Test
    void testarCT05_pernoiteVip() {
        // linha 47: pernoite com VIP -> R$ 50,00 * 0.5
        double resultado = calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 22, 0),
                LocalDateTime.of(2026, 10, 2, 9, 0), true);
        assertEquals(25.0, resultado, 0.01);
    }

    @Test
    void testarCT06_vipComHoraAdicional() {
        // VIP fora da tarifa fixa: 121 min = 15 + 2*5 = 25 -> 12,50
        double resultado = calc.calcularTarifa(
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 12, 1), true);
        assertEquals(12.5, resultado, 0.01);
    }
}
