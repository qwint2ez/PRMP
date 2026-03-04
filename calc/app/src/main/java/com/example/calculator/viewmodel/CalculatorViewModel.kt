package com.example.calculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.data.Calculator
import com.example.calculator.data.Operator
//import android.content.Context
//import android.os.Vibrator
//import android.os.VibratorManager
//import android.os.Build
//import androidx.lifecycle.AndroidViewModel

class CalculatorViewModel : ViewModel() {

    private val calculator = Calculator()

    private val _displayText = MutableLiveData("0")
    val displayText: LiveData<String> = _displayText

    private var currentInput = ""
    private var firstOperand: Double? = null
    private var currentOperator: Operator? = null
    private var newInput = true

    fun onDigitClick(digit: String) {
        if (newInput) {
            currentInput = ""
            newInput = false
        }
        if (currentInput.length < 12) {
            currentInput += digit
            _displayText.value = currentInput
        }
    }

    fun onOperatorClick(op: Operator) {
        if (currentInput.isNotEmpty()) {
            if (firstOperand == null) {
                firstOperand = currentInput.toDouble()
            } else if (currentOperator != null) {
                // выполнить предыдущую операцию
                try {
                    val result = calculator.calculate(
                        currentOperator!!,
                        firstOperand!!,
                        currentInput.toDouble()
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
                    currentInput.toDouble()
                )
                _displayText.value = formatResult(result)
                firstOperand = result
                currentOperator = null
                currentInput = result.toString()
                newInput = true
            } catch (e: ArithmeticException) {
                _displayText.value = "Ошибка"
                clearAll()
            }
        }
    }

    fun onClearClick() {
        clearAll()
        _displayText.value = "0"
    }

    private fun clearAll() {
        firstOperand = null
        currentOperator = null
        currentInput = ""
        newInput = true
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.8f", value).trimEnd('0').trimEnd('.')
        }
    }
}