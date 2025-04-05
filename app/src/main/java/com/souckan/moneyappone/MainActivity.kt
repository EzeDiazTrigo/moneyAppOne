package com.souckan.moneyappone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.ui.settings.PinActivity
import com.souckan.moneyappone.ui.settings.SettingsActivity
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
    private lateinit var pinManager: PinManager
    private var totalUSD: Float = 0f
    private var totalBTC: Float = 0f
    private var totalARS: Float = 0f
    private var totalSumUSD: Float = 0f
    private var totalSumARS: Float = 0f
    private var dollarRate = 1f
    private var bitcoinRate = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        pinManager = PinManager(this)

        Log.d("ESTA AUTENTICADO?", pinManager.isUserAuthenticated().toString())



        if (!pinManager.isUserAuthenticated()) {
            Log.d("ENTRO OTRA VEZZ", pinManager.isUserAuthenticated().toString())
            startActivity(Intent(this, PinActivity::class.java))
            finish()
        } else {
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

        //Color de barra de navegación y de status
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryDark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            decorView.systemUiVisibility = 0  // vuelve al default, íconos claros
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            decorView.systemUiVisibility = 0 // Íconos claros
        }

        lifecycleScope.launch {
            dollarRate = totalViewModel.getDollarPrice()
            bitcoinRate = totalViewModel.getBitcoinPrice()
            actualizarTotales() // Llama una vez en caso de que ya haya datos en los LiveData
        }
        totalViewModel.getTotalOnlyBTC().observe(this) { btc ->
            totalBTC = btc ?: 0f
            actualizarTotales()
        }

        totalViewModel.getTotalNonARS().observe(this) { usd ->
            totalUSD = usd ?: 0f
            actualizarTotales()
        }

        totalViewModel.getTotalOnlyARS().observe(this) { ars ->
            totalARS = ars ?: 0f
            actualizarTotales()
        }

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
            Log.d("DOLAR HOY", currentDollarPrice.toString())
            val currentBitcoinPrice = totalViewModel.getBitcoinPrice()
            Log.d("BITCOIN HOY", currentBitcoinPrice.toString())
            runOnUiThread {
                totalViewModel.insertCurrency(
                    CurrencyEntity(
                        idCurrency = 5,
                        currencyName = "ARS",
                        dollarPrice = currentDollarPrice
                    )
                )
                totalViewModel.insertCurrency(
                    CurrencyEntity(
                        idCurrency = 6,
                        currencyName = "BTC",
                        dollarPrice = currentBitcoinPrice
                    )
                )
            }
        }


    }

    private fun actualizarTotales() {
        if (dollarRate == 0f) return
        val arsName = getString(R.string.ars)
        val usdName = getString(R.string.usd)
        val totalEnPesos = totalARS + (totalUSD * dollarRate) + (totalBTC * bitcoinRate * dollarRate)
        val totalEnDolares = totalUSD + (totalARS / dollarRate) + (totalBTC * bitcoinRate)
        val red = ContextCompat.getColor(this, R.color.red)
        val white = ContextCompat.getColor(this, R.color.white)
        val grey = ContextCompat.getColor(this, R.color.grey)

        val totalPesosFormatted = String.format("%.2f", kotlin.math.abs(totalEnPesos))
        val totalDolaresFormatted = String.format("%.2f", kotlin.math.abs(totalEnDolares))

        binding.tvPesos.text = "$totalPesosFormatted  $arsName"
        binding.tvPesos.setTextColor(if (totalEnPesos != null && totalEnPesos < 0) red else white)

        binding.tvDollar.text = "$totalDolaresFormatted  $usdName"
        binding.tvDollar.setTextColor(if (totalEnDolares != null && totalEnDolares < 0) red else grey)
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.buttonNavView.setupWithNavController(navController)
        binding.imgSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

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

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

}


