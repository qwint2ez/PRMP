package com.example.calculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.data.Calculator
import com.example.calculator.data.HistoryEntry
import com.example.calculator.data.HistoryRepository
import com.example.calculator.data.Operator
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {

    private val calculator = Calculator()
    private val historyRepository = HistoryRepository()

    private val _displayText = MutableLiveData("0")
    val displayText: LiveData<String> = _displayText

    private var currentInput = ""
    private var firstOperand: BigDecimal? = null
    private var currentOperator: Operator? = null
    private var newInput = true
    private var clearOnNextDigit = false
    private var lastExpression = ""
    private var lastResult = ""

    fun onDigitClick(digit: String) {
        if (clearOnNextDigit) {
            clearAll()
            clearOnNextDigit = false
        }

        if (newInput) {
            currentInput = ""
            newInput = false
        }
        if (currentInput.length < 15) {
            currentInput += digit
            _displayText.value = currentInput
        }
    }

    fun onDotClick() {
        if (clearOnNextDigit) {
            clearAll()
            clearOnNextDigit = false
        }

        if (newInput) {
            currentInput = "0."
            newInput = false
        } else {
            if (!currentInput.contains(".")) {
                currentInput += "."
            }
        }
        _displayText.value = currentInput
    }

    fun onOperatorClick(op: Operator) {
        if (clearOnNextDigit) {
            clearOnNextDigit = false
        }

        if (currentInput.isNotEmpty()) {
            if (firstOperand == null) {
                firstOperand = currentInput.toBigDecimal()
            } else if (currentOperator != null) {
                try {
                    val result = calculator.calculate(
                        currentOperator!!,
                        firstOperand!!,
                        currentInput.toBigDecimal()
                    )
                    firstOperand = result
                    _displayText.value = formatResult(result)
                } catch (e: ArithmeticException) {
                    _displayText.value = "Ошибка"
                    clearAll()
                    return
                }
            }
            currentOperator = op
            newInput = true
        }
    }

    fun onEqualClick() {
        if (firstOperand != null && currentOperator != null && currentInput.isNotEmpty()) {
            try {
                val result = calculator.calculate(
                    currentOperator!!,
                    firstOperand!!,
                    currentInput.toBigDecimal()
                )
                val resultStr = formatResult(result)
                _displayText.value = resultStr

                lastExpression = buildExpression()
                lastResult = resultStr

                firstOperand = result
                currentOperator = null
                currentInput = resultStr
                newInput = true
                clearOnNextDigit = true
            } catch (e: ArithmeticException) {
                _displayText.value = "Ошибка"
                clearAll()
            }
        }
    }

    fun onClearClick() {
        clearAll()
        _displayText.value = "0"
        clearOnNextDigit = false
    }

    private fun clearAll() {
        firstOperand = null
        currentOperator = null
        currentInput = ""
        newInput = true
        clearOnNextDigit = false
    }

    private fun formatResult(value: BigDecimal): String {
        return value.stripTrailingZeros().toPlainString()
    }

    fun getCurrentExpression(): String {
        return if (lastExpression.isNotEmpty()) lastExpression else buildExpression()
    }

    private fun buildExpression(): String {
        val first = firstOperand?.let {
            if (it.stripTrailingZeros().scale() <= 0) it.toBigInteger().toString()
            else it.toString()
        } ?: ""
        val op = when (currentOperator) {
            Operator.ADD -> "+"
            Operator.SUBTRACT -> "-"
            Operator.MULTIPLY -> "×"
            Operator.DIVIDE -> "÷"
            null -> ""
        }
        val second = if (!newInput && currentInput.isNotEmpty()) {
            val num = currentInput.toBigDecimalOrNull()
            if (num != null && num.stripTrailingZeros().scale() <= 0) num.toBigInteger().toString()
            else currentInput
        } else ""
        return "$first $op $second".trim()
    }

    fun saveToHistory(expression: String, result: String) {
        viewModelScope.launch {
            val entry = HistoryEntry(expression, result)
            historyRepository.addEntry(entry)
        }
    }
}