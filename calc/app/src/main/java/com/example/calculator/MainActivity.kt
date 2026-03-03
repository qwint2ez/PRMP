package com.example.calculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.data.Operator
import com.example.calculator.viewmodel.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.displayText.observe(this) { text ->
            binding.tvResult.text = text
        }

        binding.btn0.setOnClickListener { viewModel.onDigitClick("0") }
        binding.btn1.setOnClickListener { viewModel.onDigitClick("1") }
        binding.btn2.setOnClickListener { viewModel.onDigitClick("2") }
        binding.btn3.setOnClickListener { viewModel.onDigitClick("3") }
        binding.btn4.setOnClickListener { viewModel.onDigitClick("4") }
        binding.btn5.setOnClickListener { viewModel.onDigitClick("5") }
        binding.btn6.setOnClickListener { viewModel.onDigitClick("6") }
        binding.btn7.setOnClickListener { viewModel.onDigitClick("7") }
        binding.btn8.setOnClickListener { viewModel.onDigitClick("8") }
        binding.btn9.setOnClickListener { viewModel.onDigitClick("9") }

        binding.btnAdd.setOnClickListener { viewModel.onOperatorClick(Operator.ADD) }
        binding.btnSubtract.setOnClickListener { viewModel.onOperatorClick(Operator.SUBTRACT) }
        binding.btnMultiply.setOnClickListener { viewModel.onOperatorClick(Operator.MULTIPLY) }
        binding.btnDivide.setOnClickListener { viewModel.onOperatorClick(Operator.DIVIDE) }

        binding.btnEquals.setOnClickListener { viewModel.onEqualClick() }
        binding.btnClear.setOnClickListener { viewModel.onClearClick() }
    }
}