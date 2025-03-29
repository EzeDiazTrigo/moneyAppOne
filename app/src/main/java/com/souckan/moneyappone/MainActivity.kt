package com.souckan.moneyappone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.network.DollarAPIService
import com.souckan.moneyappone.data.network.NetworkModule.provideRetrofit
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.domain.model.Total
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val totalViewModel: TotalViewModel by viewModels()

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
        try {
            totalViewModel.insertBill(currencyCode = "USD", accountName = "MP", amount = 200.0F, billDate = "20250329", description = "Compra de prueba")
            Log.d("INSERT", "Bill insertado correctamente")

        }catch (e: Exception) {
            Log.e("INSERT", "Error insertando el Bill", e)
        }
        try {
            val bills = totalViewModel.getAllBills()
            Log.d("GET", bills.toString())
        }catch (e: Exception){
            Log.e("GET", "Error obteniendo los Bills", e)
        }

        /*
        var hola = TotalEntity(1000,0, 152.0F, 0)
        var listita:List<TotalEntity> = listOf(hola)

        // Insertar en la base de datos
        //totalViewModel.insertAll(listita)

        //Log.d("LISTA TOTALES", totalViewModel.getAllTotals().toString())
        val database = Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total_database"
        ).build()
        val totalDao = database.getTotalDao()
        GlobalScope.launch(Dispatchers.IO) {
            val hola = TotalEntity(idCurrency = 0, totalAmount = 123.0F, idAccount = 0)
            totalDao.insertAll(listOf(hola))
        }
        GlobalScope.launch(Dispatchers.IO) {
            val lista = totalDao.getAllTotals()
            lista.forEach {
                Log.d("DB_TEST", "ID: ${it.idTotal}, Moneda: ${it.idCurrency}, Monto: ${it.totalAmount}, Cuenta: ${it.idAccount}")
            }
        }*/


    }

    private fun initUI(){
        initNavigation()
        binding.tvPesos.text = String.format("%.2f", pesos)

        CoroutineScope(Dispatchers.IO).launch {
            dolares = totalViewModel.pesosToDollar(pesos)
            Log.d("DOLAR", dolares.toString())
            runOnUiThread{
                Log.d("DOLAR", dolares.toString())
                binding.tvDollar.text = String.format("%.2f", dolares)
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


