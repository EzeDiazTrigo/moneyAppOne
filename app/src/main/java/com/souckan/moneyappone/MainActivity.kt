package com.souckan.moneyappone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.data.network.DollarAPIService
import com.souckan.moneyappone.data.network.NetworkModule.provideRetrofit
import com.souckan.moneyappone.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    public var pesos:Float = 1468000.0F
    public var dolares:Float = 0.0F
    public var dolarHoy:Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        val database = Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total_database"
        ).build()
        val totalDao = database.getTotalDao()
        GlobalScope.launch(Dispatchers.IO) {
            val hola = TotalEntity(currency = "DDD", totalAmount = 2233.0F, account = "Das")
            totalDao.insertAll(listOf(hola))
        }
        GlobalScope.launch(Dispatchers.IO) {
            val lista = totalDao.getAllTotals()
            lista.forEach {
                Log.d("DB_TEST", "ID: ${it.idTotal}, Moneda: ${it.currency}, Monto: ${it.totalAmount}, Cuenta: ${it.account}")
            }
        }
    }

    private fun initUI(){
        initNavigation()
        binding.tvPesos.text = String.format("%.2f", pesos)
        CoroutineScope(Dispatchers.IO).launch {
            val call = provideRetrofit().create(DollarAPIService::class.java).getDollarPrice("blue")
            val response = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    if (response != null) {
                        dolarHoy = response.venta.toFloat()
                        dolares = pesos / dolarHoy
                        binding.tvDollar.text = String.format("%.2f", dolares)
                    }
                } else {
                    showError()
                }
            }
        }

    }

    private fun initNavigation(){
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.buttonNavView.setupWithNavController(navController)
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
}