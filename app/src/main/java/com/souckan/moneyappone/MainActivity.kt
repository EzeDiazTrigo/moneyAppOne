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
import com.souckan.moneyappone.data.database.entity.BillEntity
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


        totalViewModel.getAllAccountsNames().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("CUENTAS NOMBRE", "Nombre cuenta -> Cuenta: ${total}")
            }
        }

        totalViewModel.getAllCurrencies().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("MONEDAS", "Moneda -> Cuenta: ${total.idCurrency}, Nombre: ${total.currencyName}, Precio dolar: ${total.dollarPrice}")
            }
        }

        totalViewModel.getAllBills().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("GASTOS", "Gasto -> Fecha: ${total.billDate}, Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Gasto: ${total.idBill}, Monto: ${total.amount}, Descripcion: ${total.description}")
            }
        }

        totalViewModel.getAllAccounts().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("CUENTAS", "Cuenta -> Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Nombre: ${total.accountName}")
            }
        }

        totalViewModel.getAllTotals().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("TOTALES", "Total -> Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Total: ${total.idTotal}, Monto total: ${total.totalAmount}")
            }
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
        binding.tvPesos.text = "$" + String.format("%.2f", pesos)

        CoroutineScope(Dispatchers.IO).launch {
            dolares = totalViewModel.pesosToDollar(pesos)
            Log.d("DOLAR", totalViewModel.getDollarPrice().toString() )
            runOnUiThread{
                binding.tvDollar.text = "$" + String.format("%.2f", dolares)
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


