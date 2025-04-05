package com.souckan.moneyappone.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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


        if (pinManager.getPin() == null) {
            binding.tvTitlePin.text = "Configurar PIN"
            setupNewPin()
        } else {
            binding.tvTitlePin.text = "Ingresar PIN"
            verifyPin()
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
                    navigateToMain()
                } else {
                    Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(com.souckan.moneyappone.R.string.pin_more_four), Toast.LENGTH_SHORT).show()
            }
        }
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