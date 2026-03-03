package com.example.calculator.data

class Calculator {

    fun calculate(operator: Operator, first: Double, second: Double): Double {
        return when (operator) {
            Operator.ADD -> first + second
            Operator.SUBTRACT -> first - second
            Operator.MULTIPLY -> first * second
            Operator.DIVIDE -> {
                if (second == 0.0) {
                    throw ArithmeticException("Деление на ноль невозможно")
                } else {
                    first / second
                }
            }
        }
    }
}