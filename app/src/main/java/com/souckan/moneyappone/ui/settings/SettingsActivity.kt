package com.souckan.moneyappone.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.souckan.moneyappone.MainActivity
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.data.database.utilities.DatabaseUtils
import com.souckan.moneyappone.data.database.utilities.DatabaseUtils.copyDatabaseFiles
import com.souckan.moneyappone.data.database.utilities.DatabaseUtils.exportDatabase
import com.souckan.moneyappone.databinding.ActivitySettingsBinding
import com.souckan.moneyappone.di.RoomModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        binding.btnChangePin.setOnClickListener {
            val intent = Intent(this, PinActivity::class.java)
            intent.putExtra(PinActivity.ACTION_CHANGE_PIN, true)
            startActivity(intent)
        }
        binding.ivBackToTotal.setOnClickListener {
            onBackPressed()
            pinManager.setUserAuthenticated(true)
        }
        binding.btnExport.setOnClickListener {
            startExportDatabase()
        }


        /*binding.btnImport.setOnClickListener {
            startImportDatabase()
        }*/
        binding.tvTitleSettings.text = getString(R.string.settings)
    }

    suspend fun forceCheckpointAndClose(context: Context) {
        withContext(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                context,
                TotalDatabase::class.java,
                "total_database"
            ).build()

            try {
                db.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL)")
            } catch (e: Exception) {
                Log.e("Checkpoint", "Error haciendo checkpoint", e)
            } finally {
                db.close()
            }
        }
    }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                forceCheckpointAndClose(applicationContext)
                delay(500) // Le damos tiempo a que se complete el cierre
                try {
                    copyDatabaseFiles(applicationContext, uri)
                    Toast.makeText(applicationContext, "Base de datos exportada con éxito", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("ExportDB", "Error exportando la base de datos", e)
                    Toast.makeText(applicationContext, "Error al exportar la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Exportación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            DatabaseUtils.importDatabase(this, uri)
            RoomModule.closeDatabase()
            restartApp()
        } else {
            Toast.makeText(this, "Importación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startImportDatabase() {
        RoomModule.closeDatabase()
        importLauncher.launch(arrayOf("*/*"))
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
        Runtime.getRuntime().exit(0) // opción extra si sigue fallando
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