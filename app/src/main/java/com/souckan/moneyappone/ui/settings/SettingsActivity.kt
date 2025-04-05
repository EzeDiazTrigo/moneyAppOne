package com.souckan.moneyappone.ui.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.data.database.utilities.DatabaseUtils
import com.souckan.moneyappone.databinding.ActivitySettingsBinding

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinManager = PinManager(this)

        initUI()



    }

    private fun initUI() {

        //Color de barra de navegación y de status
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

        binding.ivBackToTotal.setOnClickListener {
            onBackPressed()
            pinManager.setUserAuthenticated(true)
        }
        binding.btnExport.setOnClickListener {
            startExportDatabase()
        }


        binding.btnImport.setOnClickListener {
            startImportDatabase()

        }
        binding.tvTitleSettings.text = getString(R.string.settings)
    }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            DatabaseUtils.exportDatabase(this, uri)
        } else {
            Toast.makeText(this, "Exportación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            DatabaseUtils.importDatabase(this, uri)
        } else {
            Toast.makeText(this, "Importación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startImportDatabase() {
        importLauncher.launch(arrayOf("*/*"))
    }


    fun startExportDatabase() {
        exportLauncher.launch("backup_total_database.db")
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}