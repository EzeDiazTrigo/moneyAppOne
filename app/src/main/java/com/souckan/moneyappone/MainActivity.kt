package com.souckan.moneyappone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.ui.settings.PinActivity
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val totalViewModel: TotalViewModel by viewModels()
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val FIRST_KEY: String = "FIRST"
    var pesos: Float = 1468000.0F
    var dolares: Float = 0.0F
    var dolarHoy: Float = 0.0F
    var isFirstTime: Boolean = true
    private lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

         // Clase que maneja el PIN
        pinManager = PinManager(this)

        Log.d("ESTA AUTENTICADO?", pinManager.isUserAuthenticated().toString())
        if (!pinManager.isUserAuthenticated()) {
            Log.d("ENTRO OTRA VEZZ", pinManager.isUserAuthenticated().toString())
            startActivity(Intent(this, PinActivity::class.java))
            finish()
        } else{
            Log.d("NO ENTRE NOOOOO", pinManager.isUserAuthenticated().toString())
            setContentView(binding.root)
        }

        initUI()
        totalViewModel.getAllAccountsNames().observe(this) { totals ->
            totals.forEach { total ->
                Log.d("CUENTAS NOMBRE", "Nombre cuenta -> Cuenta: ${total}")
            }
        }
        totalViewModel.getAllCurrencies().observe(this) { totals ->
            totals.forEach { total ->
                Log.d(
                    "MONEDAS",
                    "Moneda -> Cuenta: ${total.idCurrency}, Nombre: ${total.currencyName}, Precio dolar: ${total.dollarPrice}"
                )
            }
        }
        totalViewModel.getAllBills().observe(this) { totals ->
            totals.forEach { total ->
                Log.d(
                    "GASTOS",
                    "Gasto -> Fecha: ${total.billDate}, Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Gasto: ${total.idBill}, Monto: ${total.amount}, Descripcion: ${total.description}"
                )
            }
        }
        totalViewModel.getAllAccounts().observe(this) { totals ->
            totals.forEach { total ->
                Log.d(
                    "CUENTAS",
                    "Cuenta -> Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Nombre: ${total.accountName}"
                )
            }
        }
        totalViewModel.getAllTotals().observe(this) { totals ->
            totals.forEach { total ->
                Log.d(
                    "TOTALES",
                    "Total -> Cuenta: ${total.idAccount}, Moneda: ${total.idCurrency}, Total: ${total.idTotal}, Monto total: ${total.totalAmount}"
                )
            }
        }

    }



    private fun initUI() {
        initNavigation()
        val red = ContextCompat.getColor(this, R.color.red)
        val white = ContextCompat.getColor(this, R.color.white)
        val blue = ContextCompat.getColor(this, R.color.sky)
        totalViewModel.getTotalNonARS().observe(this) { sum ->
            val totalFormatted = sum?.let { String.format("%.2f", kotlin.math.abs(it)) } ?: "0.00"
            binding.tvDollar.text = if (sum != null && sum < 0) "-$$totalFormatted" else "$$totalFormatted"
            binding.tvDollar.setTextColor(if (sum != null && sum < 0) red else blue)
        }

        totalViewModel.getTotalOnlyARS().observe(this) { sum ->
            val pesosFormatted = sum?.let { String.format("%.2f", kotlin.math.abs(it)) } ?: "0.00"
            binding.tvPesos.text = if (sum != null && sum < 0) "-$$pesosFormatted" else "$$pesosFormatted"
            binding.tvPesos.setTextColor(if (sum != null && sum < 0) red else white)
        }

        /*totalViewModel.getTotalNonARS().observe(this) { sum ->
            val totalFormatted = sum?.let { String.format("%.2f", it) } ?: "0.00"
            binding.tvDollar.text = "$$totalFormatted"}

        totalViewModel.getTotalOnlyARS().observe(this) { sum ->
            val pesos = sum?.let { String.format("%.2f", it) } ?: "0.00"
            binding.tvPesos.text = "$$pesos"
        }*/

        totalViewModel.insertCurrency(
            CurrencyEntity(
                idCurrency = 1,
                currencyName = "USD",
                dollarPrice = 1.0F
            )
        )
        totalViewModel.insertCurrency(
            CurrencyEntity(
                idCurrency = 2,
                currencyName = "USDT",
                dollarPrice = 1.0F
            )
        )
        totalViewModel.insertCurrency(
            CurrencyEntity(
                idCurrency = 3,
                currencyName = "USDC",
                dollarPrice = 1.0F
            )
        )
        totalViewModel.insertCurrency(
            CurrencyEntity(
                idCurrency = 4,
                currencyName = "DAI",
                dollarPrice = 1.0F
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            val currentDollarPrice = totalViewModel.getDollarPrice()
            Log.d("DOLARHOY", currentDollarPrice.toString())
            runOnUiThread {
                totalViewModel.insertCurrency(
                    CurrencyEntity(
                        idCurrency = 5,
                        currencyName = "ARS",
                        dollarPrice = currentDollarPrice
                    )
                )
            }
        }


    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.buttonNavView.setupWithNavController(navController)
    }

    // Volver a pedir PIN cada vez que la app se reabre
    override fun onResume() {
        super.onResume()
        val pinManager = PinManager(this)
        if (!pinManager.isUserAuthenticated()) {
            startActivity(Intent(this, PinActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("AUTH", "Se borra la autenticación porque la app se fue al fondo")
        pinManager.setUserAuthenticated(false)
        pinManager.clearAuthentication()
    }

}


