package com.example.calculator.data

import java.math.BigDecimal
import java.math.RoundingMode

class Calculator {

    fun calculate(operator: Operator, first: BigDecimal, second: BigDecimal): BigDecimal {
        return when (operator) {
            Operator.ADD -> first.add(second)
            Operator.SUBTRACT -> first.subtract(second)
            Operator.MULTIPLY -> first.multiply(second)
            Operator.DIVIDE -> {
                if (second.compareTo(BigDecimal.ZERO) == 0) {
                    throw ArithmeticException("Деление на ноль невозможно")
                } else {
                    first.divide(second, 20, RoundingMode.HALF_UP)
                }
            }
        }
    }
}