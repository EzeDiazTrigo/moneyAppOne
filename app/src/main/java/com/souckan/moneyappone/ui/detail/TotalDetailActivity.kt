package com.souckan.moneyappone.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.databinding.ActivityTotalDetailBinding
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class TotalDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotalDetailBinding
    private val totalViewModel: TotalViewModel by viewModels()
    private lateinit var totalDetailAdapter: TotalDetailAdapter
    private var idAccount by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTotalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        idAccount = (intent.getStringExtra("idAccount") ?: return) as Int

        // Configurar RecyclerView
        totalDetailAdapter = TotalDetailAdapter(mutableListOf())
        findViewById<RecyclerView>(R.id.rvBills).apply {
            layoutManager = LinearLayoutManager(this@TotalDetailActivity)
            adapter = totalDetailAdapter
        }

        // Obtener ViewModel y observar las `bills`
        //totalViewModel = ViewModelProvider(this).get(TotalViewModel::class.java)
        totalViewModel.getAllBillByAccount(idAccount).observe(this) { bills ->
            totalDetailAdapter.updateBills(bills.toMutableList())
        }
    }
}