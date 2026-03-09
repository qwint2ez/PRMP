package com.example.calculator.ui

import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.calculator.R
import com.example.calculator.data.Operator
import com.example.calculator.data.ThemeRepository
import com.example.calculator.data.ThemeSettings
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val defaultTheme = ThemeSettings(
            primaryColor = 0xFF6200EE.toInt(),
            textColor = 0xFFFFFFFF.toInt(),
            statusBarColor = 0xFF6200EE.toInt()
        )
        applyTheme(defaultTheme)

        themeRepository = ThemeRepository()

        lifecycleScope.launch {
            val settings = themeRepository.loadTheme()
            settings?.let { applyTheme(it) }
        }

        viewModel.displayText.observe(this) { text ->
            binding.tvResult.text = text
        }

        binding.btn0.setOnClickListener {
            vibrate(100)
            viewModel.onDigitClick("0")
        }
        binding.btn1.setOnClickListener {
            vibrate(200)
            viewModel.onDigitClick("1")
        }
        binding.btn2.setOnClickListener {
            vibrate(300)
            viewModel.onDigitClick("2")
        }
        binding.btn3.setOnClickListener {
            vibrate(400)
            viewModel.onDigitClick("3")
        }
        binding.btn4.setOnClickListener {
            vibrate(500)
            viewModel.onDigitClick("4")
        }
        binding.btn5.setOnClickListener {
            vibrate(600)
            viewModel.onDigitClick("5")
        }
        binding.btn6.setOnClickListener {
            vibrate(700)
            viewModel.onDigitClick("6")
        }
        binding.btn7.setOnClickListener {
            vibrate(800)
            viewModel.onDigitClick("7")
        }
        binding.btn8.setOnClickListener {
            vibrate(900)
            viewModel.onDigitClick("8")
        }
        binding.btn9.setOnClickListener {
            vibrate(1000)
            viewModel.onDigitClick("9")
        }

        binding.btnDot.setOnClickListener {
            vibrate(30)
            viewModel.onDotClick()
        }

        binding.btnAdd.setOnClickListener {
            vibrate(50)
            viewModel.onOperatorClick(Operator.ADD)
        }
        binding.btnSubtract.setOnClickListener {
            vibrate(50)
            viewModel.onOperatorClick(Operator.SUBTRACT)
        }
        binding.btnMultiply.setOnClickListener {
            vibrate(50)
            viewModel.onOperatorClick(Operator.MULTIPLY)
        }
        binding.btnDivide.setOnClickListener {
            vibrate(50)
            viewModel.onOperatorClick(Operator.DIVIDE)
        }

        binding.btnEquals.setOnClickListener {
            vibrate(100)
            viewModel.onEqualClick()
            val expression = viewModel.getCurrentExpression() // получаем выражение
            val result = binding.tvResult.text.toString()     // результат уже обновлён
            viewModel.saveToHistory(expression, result)
        }

        binding.btnClear.setOnClickListener {
            vibrate(50)
            viewModel.onClearClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_choose_theme -> {
                showThemeSelectionDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showThemeSelectionDialog() {
        val colors = arrayOf(
            "Фиолетовый" to 0xFF6200EE.toInt(),
            "Синий" to 0xFF2196F3.toInt(),
            "Красный" to 0xFFF44336.toInt()
        )
        val colorNames = colors.map { it.first }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Выберите цвет темы")
            .setItems(colorNames) { _, which ->
                val selectedColor = colors[which].second
                val newSettings = ThemeSettings(
                    primaryColor = selectedColor,
                    textColor = 0xFFFFFFFF.toInt(),
                    statusBarColor = selectedColor
                )
                applyTheme(newSettings)
                saveTheme(newSettings)
            }
            .show()
    }

    private fun applyTheme(settings: ThemeSettings) {
        applyThemeToAllButtons(settings)
        window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = settings.statusBarColor
        }
    }

    private fun applyThemeToAllButtons(settings: ThemeSettings) {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btnDot, binding.btnAdd,
            binding.btnSubtract, binding.btnMultiply, binding.btnDivide,
            binding.btnEquals, binding.btnClear
        )
        buttons.forEach { button ->
            button.setBackgroundColor(settings.primaryColor)
            button.setTextColor(settings.textColor)
        }
    }

    private fun saveTheme(settings: ThemeSettings) {
        lifecycleScope.launch {
            themeRepository.saveTheme(settings)
        }
    }

    private fun vibrate(duration: Long = 50) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(duration)
    }
}