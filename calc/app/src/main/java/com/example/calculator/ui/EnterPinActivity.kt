package com.example.calculator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.R
import com.example.calculator.security.PinManager

class EnterPinActivity : AppCompatActivity() {

    private lateinit var tvPinDisplay: TextView
    private var enteredPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_enter)

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
            enteredPin = ""
            tvPinDisplay.text = ""
        }

        findViewById<Button>(R.id.btnBackspace).setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                tvPinDisplay.text = "*".repeat(enteredPin.length)
            }
        }
    }

    private fun onNumberClick(digit: String) {
        if (enteredPin.length < 4) {
            enteredPin += digit
            tvPinDisplay.text = "*".repeat(enteredPin.length)
        }
        if (enteredPin.length == 4) {
            if (PinManager.validatePin(this, enteredPin)) {
                startActivity(Intent(this, HistoryActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show()
                enteredPin = ""
                tvPinDisplay.text = ""
            }
        }
    }
}