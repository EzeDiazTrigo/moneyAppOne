package com.souckan.moneyappone.ui.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import androidx.lifecycle.Observer
import com.souckan.moneyappone.data.SharedPreferences.Pin.PinManager
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.databinding.ActivityTotalDetailBinding
import com.souckan.moneyappone.ui.fragments.Adapters.BillAdapter
import com.souckan.moneyappone.ui.fragments.Adapters.TotalDetailAdapter
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class TotalDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotalDetailBinding
    private val totalViewModel: TotalViewModel by viewModels()
    private lateinit var totalDetailAdapter: TotalDetailAdapter
    private var idAccount: Int = -1
    private lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        enableEdgeToEdge()
        binding = ActivityTotalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pinManager = PinManager(this)
        idAccount = intent.getIntExtra("idAccount", -1)
        Log.d("ACCOUNT ID", idAccount.toString())

        initUI()

        // Inicializar el RecyclerView y el Adapter
        totalDetailAdapter = TotalDetailAdapter(mutableListOf())
        binding.rvTotalDetail.layoutManager = LinearLayoutManager(this)
        binding.rvTotalDetail.adapter = totalDetailAdapter

        // Observar los datos del ViewModel
        totalViewModel.getBillsByAccount(idAccount).observe(this, Observer { bills ->
            totalDetailAdapter.updateTotalDetails(bills.toMutableList())
        })


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
            pinManager.setUserAuthenticated(true)
            onBackPressed()
        }
        totalViewModel.getAccountNameById(idAccount).observe(this, Observer { accountName ->
            binding.tvTitleTotalDetail.text = "$accountName"
        })

    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}