package br.com.pucrs.verival;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Calculadora de tarifas de estacionamento.
 *
 * Regras: - Horário de Entrada: permitido apenas das 08:00 às 23:59. - Horário
 * de Saída: bloqueado das 02:00 às 07:59. - Cortesia: até 20 min → R$ 0,00. -
 * Tarifa fixa: de 21 min até 60 min → R$ 15,00. - Adicional: +R$ 5,00 a cada
 * hora adicional (fração arredondada para cima). - Pernoite: saída no dia
 * seguinte após as 08:00 → R$ 50,00 por pernoite. - VIP: 50% de desconto sobre
 * o valor final (multiplicador 0.5).
 */
public class CalculadoraEstacionamento {

    private static final double TARIFA_FIXA = 15.00;
    private static final double VALOR_HORA_ADICIONAL = 5.00;
    private static final double VALOR_PERNOITE = 50.00;
    private static final double MULTIPLICADOR_VIP = 0.5;
    private static final int CORTESIA_MINUTOS = 20;

    /**
     * Calcula a tarifa do estacionamento.
     *
     * @param entrada horário de entrada
     * @param saida horário de saída
     * @param vip se o cliente é VIP (50% de desconto)
     * @return valor a pagar em reais
     * @throws IllegalArgumentException se os horários forem inválidos
     */
    public double calcularTarifa(LocalDateTime entrada, LocalDateTime saida, boolean vip) {
        validarParametros(entrada, saida);
        validarHorarioEntrada(entrada);
        validarHorarioSaida(saida);

        // Verifica pernoite: saída em dia diferente e após as 08:00
        boolean pernoite = !entrada.toLocalDate().equals(saida.toLocalDate())
                && !saida.toLocalTime().isBefore(LocalTime.of(8, 0));

        if (pernoite) {
            long dias = Duration.between(entrada.toLocalDate().atStartOfDay(),
                    saida.toLocalDate().atStartOfDay()).toDays();
            double valor = dias * VALOR_PERNOITE;
            return vip ? valor * MULTIPLICADOR_VIP : valor;
        }

        long minutos = Duration.between(entrada, saida).toMinutes();

        if (minutos <= CORTESIA_MINUTOS) {
            return 0.0;
        }

        double valor;

        if (minutos <= 60) {
            valor = TARIFA_FIXA;
        } else {
            long horasAdicionais = (long) Math.ceil((minutos - 60) / 60.0);
            valor = TARIFA_FIXA + (horasAdicionais * VALOR_HORA_ADICIONAL);
        }

        return vip ? valor * MULTIPLICADOR_VIP : valor;
    }

    private void validarParametros(LocalDateTime entrada, LocalDateTime saida) {
        if (entrada == null || saida == null) {
            throw new IllegalArgumentException("Entrada e saída não podem ser nulos");
        }
        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("Horário de saída não pode ser anterior ao horário de entrada");
        }
    }

    /**
     * Horário de entrada permitido: 08:00 às 23:59.
     */
    private void validarHorarioEntrada(LocalDateTime entrada) {
        LocalTime hora = entrada.toLocalTime();
        if (hora.isBefore(LocalTime.of(8, 0))) {
            throw new IllegalArgumentException("Horário de entrada inválido: permitido apenas das 08:00 às 23:59");
        }
    }

    /**
     * Horário de saída bloqueado: 02:00 às 07:59.
     */
    private void validarHorarioSaida(LocalDateTime saida) {
        LocalTime hora = saida.toLocalTime();
        if (!hora.isBefore(LocalTime.of(2, 0)) && hora.isBefore(LocalTime.of(8, 0))) {
            throw new IllegalArgumentException("Horário de saída inválido: bloqueado das 02:00 às 07:59");
        }
    }
}
