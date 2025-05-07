package com.souckan.moneyappone.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.souckan.moneyappone.MainActivity
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.databinding.ActivityPinBinding
import com.souckan.moneyappone.databinding.ActivitySettingsBinding

class PinActivity : AppCompatActivity() {

    private lateinit var pinManager: PinManager
    private lateinit var binding: ActivityPinBinding

    companion object {
        const val ACTION_CHANGE_PIN = "CHANGE_PIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        enableEdgeToEdge()
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinManager = PinManager(this)
        Log.d("PIN DEBUG", "PinActivity iniciado - PIN Guardado: ${pinManager.getPin()}")
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            decorView.systemUiVisibility = 0  // vuelve al default, íconos claros
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            decorView.systemUiVisibility = 0 // Íconos claros
        }
        val isChangingPin = intent.getBooleanExtra(ACTION_CHANGE_PIN, false)

        binding.tvForgotPin.setOnClickListener {
            showRecoveryDialog()
        }

        if (isChangingPin) {
            binding.tvTitlePin.text = getString(com.souckan.moneyappone.R.string.confirm_pin)
            binding.tvForgotPin.visibility = View.GONE
            verifyCurrentPinBeforeChange()
        } else {
            if (pinManager.getPin() == null) {
                binding.tvTitlePin.text = getString(com.souckan.moneyappone.R.string.configure_pin)
                binding.tvForgotPin.visibility = View.GONE
                setupNewPin()

            } else {
                binding.tvTitlePin.text = getString(com.souckan.moneyappone.R.string.write_pin)
                verifyPin()
            }
        }
    }

    private fun verifyCurrentPinBeforeChange() {
        binding.btnConfirm.setOnClickListener {
            val currentPin = binding.edtPin.text.toString()
            if (currentPin == pinManager.getPin()) {
                binding.edtPin.text?.clear()
                binding.tvTitlePin.text = getString(com.souckan.moneyappone.R.string.change_pin_title)
                setupNewPin() // reutilizamos la función que ya tenés
            } else {
                Toast.makeText(this, getString(R.string.invalid_pin), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupNewPin() {
        binding.btnConfirm.setOnClickListener {

            val newPin = binding.edtPin.text.toString()

            if (newPin.length >= 4) {
                pinManager.savePin(newPin)

                // Verificar si se guardó correctamente
                val savedPin = pinManager.getPin()
                Log.d("PIN DEBUG", "Nuevo PIN: $newPin - Guardado: $savedPin")

                if (savedPin == newPin) {
                    Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_saved), Toast.LENGTH_SHORT).show()
                    showSecurityQuestionSetupDialog()
                } else {
                    Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_more_four), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showRecoveryDialog() {
        val question = pinManager.getSecurityQuestion()
        if (question == null) {
            Toast.makeText(this, getString(R.string.security_question_not_found), Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.restore_pint))
            .setMessage(question)
            .setView(input)
            .setPositiveButton(getString(R.string.accept)) { _, _ ->
                val answer = input.text.toString()
                if (pinManager.isSecurityAnswerCorrect(answer)) {
                    Toast.makeText(this, getString(R.string.insert_new_pin), Toast.LENGTH_SHORT).show()
                    binding.tvTitlePin.text = getString(R.string.configure_pin)
                    pinManager.clearPin()
                    setupNewPin()
                } else {
                    Toast.makeText(this, getString(R.string.incorrect_answer), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    @SuppressLint("ResourceAsColor")
    private fun showSecurityQuestionSetupDialog() {
        val currentQuestion = pinManager.getSecurityQuestion()
        val currentAnswer = pinManager.getSecurityAnswer()

        val inputQuestion = EditText(this).apply {
            hint = getString(R.string.example)
            setText(currentQuestion ?: "")
        }

        val inputAnswer = EditText(this).apply {
            hint = getString(R.string.answer)
            inputType = InputType.TYPE_CLASS_TEXT
            setText(currentAnswer ?: "")
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(inputQuestion)
            addView(inputAnswer)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.new_question))
            .setView(layout)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val question = inputQuestion.text.toString()
                val answer = inputAnswer.text.toString()
                if (question.isNotBlank() && answer.isNotBlank()) {
                    pinManager.saveSecurityQuestion(question, answer)
                    pinManager.setUserAuthenticated(true)
                    navigateToMain()
                } else {
                    Toast.makeText(this, getString(R.string.complete_spaces), Toast.LENGTH_SHORT).show()
                    showSecurityQuestionSetupDialog() // Volver a mostrar si falta algo
                }
            }
            .setCancelable(false)
            .show()
    }





    private fun verifyPin() {
        binding.btnConfirm.setOnClickListener {
            val enteredPin = binding.edtPin.text.toString()
            val savedPin = pinManager.getPin()
            Log.d("ENTERED PIN", enteredPin)
            Log.d("SAVED PIN", savedPin.toString())
            if (enteredPin == savedPin.toString()) {
                pinManager.setUserAuthenticated(true)
                navigateToMain()
            } else {
                Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_wrong), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}