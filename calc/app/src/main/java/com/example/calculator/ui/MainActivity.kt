package com.example.calculator.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.calculator.R
import com.example.calculator.data.Operator
import com.example.calculator.data.ThemeRepository
import com.example.calculator.data.ThemeSettings
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.security.BiometricKeyManager
import com.example.calculator.security.PinManager
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

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
            backgroundColor = 0xFFFFFFFF.toInt(),
            textColor = 0xFFFFFFFF.toInt(),
            statusBarColor = 0xFF6200EE.toInt()
        )
        applyTheme(defaultTheme)

        themeRepository = ThemeRepository()

        lifecycleScope.launch {
            val settings = themeRepository.loadTheme()
            settings?.let { applyTheme(it) }
        }

        if (!BiometricKeyManager.keyExists() && isDeviceSecure() && BiometricKeyManager.isBiometricAvailable(this)) {
            BiometricKeyManager.createKey(this)
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
            val expression = viewModel.getCurrentExpression()
            val result = binding.tvResult.text.toString()
            viewModel.saveToHistory(expression, result)
        }

        binding.btnClear.setOnClickListener {
            vibrate(50)
            viewModel.onClearClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.add(0, 1001, 0, "История")
        menu?.add(0, 1002, 0, "Сбросить PIN")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_choose_theme -> {
                showThemeSelectionDialog()
                true
            }
            1001 -> {
                showAuthDialog()
                true
            }
            1002 -> {
                PinManager.clearPin(this)
                Toast.makeText(this, "PIN сброшен. При следующем входе создайте новый.", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAuthDialog() {
        val items = mutableListOf<String>()
        val biometricAvailable = BiometricKeyManager.isBiometricAvailable(this)
        val pinSet = PinManager.isPinSet(this)

        if (biometricAvailable) {
            items.add("Использовать биометрию")
        }
        if (pinSet) {
            items.add("Ввести PIN-код")
        }
        if (!pinSet) {
            items.add("Установить PIN-код")
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "Нет доступных способов входа", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Выберите способ входа")
            .setItems(items.toTypedArray()) { _, which ->
                when (items[which]) {
                    "Использовать биометрию" -> showBiometricAuth()
                    "Ввести PIN-код" -> startActivity(Intent(this, EnterPinActivity::class.java))
                    "Установить PIN-код" -> startActivity(Intent(this, SetupPinActivity::class.java))
                }
            }
            .show()
    }

    private fun showThemeSelectionDialog() {
        val colors = arrayOf(
            "Фиолетовый" to Pair(0xFF6200EE.toInt(), 0xFFD1C4E9.toInt()),
            "Синий" to Pair(0xFF2196F3.toInt(), 0xFFBBDEFB.toInt()),
            "Красный" to Pair(0xFFF44336.toInt(), 0xFFFFCDD2.toInt())
        )
        val colorNames = colors.map { it.first }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Выберите цвет темы")
            .setItems(colorNames) { _, which ->
                val (primary, background) = colors[which].second
                val newSettings = ThemeSettings(
                    primaryColor = primary,
                    backgroundColor = background,
                    textColor = 0xFFFFFFFF.toInt(),
                    statusBarColor = primary
                )
                applyTheme(newSettings)
                saveTheme(newSettings)
            }
            .show()
    }

    private fun applyTheme(settings: ThemeSettings) {
        applyThemeToAllButtons(settings)
        binding.root.setBackgroundColor(settings.backgroundColor)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun isDeviceSecure(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager.isDeviceSecure
        } else {
            keyguardManager.isKeyguardSecure
        }
    }

    private fun showBiometricAuth() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val biometricManager = BiometricManager.from(this)

        when (val result = biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                startBiometricPrompt(authenticators)
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                startBiometricPrompt(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            }
            else -> {
                Toast.makeText(this, "Биометрия недоступна", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startBiometricPrompt(authenticators: Int) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    openHistory()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@MainActivity, "Ошибка: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Аутентификация")
            .setSubtitle("Подтвердите личность для доступа к истории")

        if (authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL == 0) {
            promptBuilder.setNegativeButtonText("Отмена")
        }

        val promptInfo = promptBuilder
            .setAllowedAuthenticators(authenticators)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun openHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
    }
}