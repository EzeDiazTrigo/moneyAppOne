package com.souckan.moneyappone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.souckan.moneyappone.data.SettingsData
import com.souckan.moneyappone.data.database.TotalDatabase
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val totalViewModel: TotalViewModel by viewModels()
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val FIRST_KEY: String = "FIRST"
    var pesos:Float = 1468000.0F
    var dolares:Float = 0.0F
    var dolarHoy:Float = 0.0F
    var isFirstTime: Boolean = true

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

    }

    private fun initUI(){
        initNavigation()
        binding.tvPesos.text = "$" + String.format("%.2f", pesos)

        CoroutineScope(Dispatchers.IO).launch {
            dolares = totalViewModel.pesosToDollar(pesos)
            Log.d("DOLAR", dolares.toString())

            runOnUiThread{
                binding.tvDollar.text = "$" + String.format("%.2f", dolares)
            }
        }
        totalViewModel.insertCurrency(CurrencyEntity(idCurrency = 1, currencyName = "USD", dollarPrice = 1.0F))
        totalViewModel.insertCurrency(CurrencyEntity(idCurrency = 2, currencyName = "USDT", dollarPrice = 1.0F))
        totalViewModel.insertCurrency(CurrencyEntity(idCurrency = 3, currencyName = "USDC", dollarPrice = 1.0F))
        totalViewModel.insertCurrency(CurrencyEntity(idCurrency = 4, currencyName = "DAI", dollarPrice = 1.0F))

        CoroutineScope(Dispatchers.IO).launch {
            val currentDollarPrice = totalViewModel.getDollarPrice()
            Log.d("DOLARHOY", currentDollarPrice.toString())
            runOnUiThread{
                totalViewModel.insertCurrency(CurrencyEntity(idCurrency = 5, currencyName = "ARS", dollarPrice = currentDollarPrice))
            }
        }




    }

    private fun initNavigation(){
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.buttonNavView.setupWithNavController(navController)
    }


    private fun getSettings(): Flow<SettingsData?> {
        return dataStore.data.map { preferences ->
            SettingsData(
                isFirstTime = preferences[booleanPreferencesKey(FIRST_KEY)] ?: false
            )
        }
    }

    private suspend fun saveTheme(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    /*private fun setCurrenciesOnce(){
        CoroutineScope(Dispatchers.IO).launch {
            getSettings().collect { settingsModel ->
                if (settingsModel != null) {
                    if(settingsModel.isFirstTime){
                        var usdPrice = totalViewModel.getDollarPrice()
                        runOnUiThread {
                            val usd = CurrencyEntity(currencyName = "USD", dollarPrice = 1.0F)
                            val usdt = CurrencyEntity(currencyName = "USDT", dollarPrice = 1.0F)
                            val usdc = CurrencyEntity(currencyName = "USDC", dollarPrice = 1.0F)
                            val dai = CurrencyEntity(currencyName = "DAI", dollarPrice = 1.0F)
                            val ars = CurrencyEntity(currencyName = "ARS", dollarPrice = usdPrice)
                            totalViewModel.insertCurrency(usd)
                            totalViewModel.insertCurrency(usdt)
                            totalViewModel.insertCurrency(usdc)
                            totalViewModel.insertCurrency(dai)
                            totalViewModel.insertCurrency(ars)

                        }
                        isFirstTime = false
                        saveTheme(FIRST_KEY, isFirstTime)
                    }
                }

            }
        }
    }*/
}


