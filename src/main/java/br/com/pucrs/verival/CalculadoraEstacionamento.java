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

}
