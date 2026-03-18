package com.example.calculator.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.R
import com.example.calculator.security.PinManager

class SetupPinActivity : AppCompatActivity() {

    private lateinit var tvPrompt: TextView
    private lateinit var tvPinDisplay: TextView
    private var firstPin = ""
    private var secondPin = ""
    private var isFirstEntry = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_setup)

        tvPrompt = findViewById(R.id.tvPrompt)
        tvPinDisplay = findViewById(R.id.tvPinDisplay)

        setupNumberPad()
    }

    private fun setupNumberPad() {
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                onNumberClick((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearInput()
        }

        findViewById<Button>(R.id.btnBackspace).setOnClickListener {
            backspace()
        }
    }

    private fun onNumberClick(digit: String) {
        val currentPin = if (isFirstEntry) firstPin else secondPin
        if (currentPin.length < 4) {
            if (isFirstEntry) {
                firstPin += digit
                tvPinDisplay.text = "*".repeat(firstPin.length)
            } else {
                secondPin += digit
                tvPinDisplay.text = "*".repeat(secondPin.length)
            }
        }
        if ((isFirstEntry && firstPin.length == 4) || (!isFirstEntry && secondPin.length == 4)) {
            if (isFirstEntry) {
                isFirstEntry = false
                tvPrompt.text = "Повторите новый PIN-код"
                tvPinDisplay.text = ""
            } else {
                if (firstPin == secondPin) {
                    PinManager.savePin(this, firstPin)
                    Toast.makeText(this, "PIN успешно установлен", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "PIN-коды не совпадают", Toast.LENGTH_SHORT).show()
                    reset()
                }
            }
        }
    }

    private fun clearInput() {
        if (isFirstEntry) firstPin = "" else secondPin = ""
        tvPinDisplay.text = ""
    }

    private fun backspace() {
        if (isFirstEntry) {
            if (firstPin.isNotEmpty()) {
                firstPin = firstPin.dropLast(1)
                tvPinDisplay.text = "*".repeat(firstPin.length)
            }
        } else {
            if (secondPin.isNotEmpty()) {
                secondPin = secondPin.dropLast(1)
                tvPinDisplay.text = "*".repeat(secondPin.length)
            }
        }
    }

    private fun reset() {
        firstPin = ""
        secondPin = ""
        isFirstEntry = true
        tvPrompt.text = "Придумайте новый PIN-код (4 цифры)"
        tvPinDisplay.text = ""
    }
}