package com.souckan.moneyappone.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import androidx.lifecycle.Observer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTotalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.ivBackToTotal.setOnClickListener {
            onBackPressed()
        }
        val title = getString(R.string.account)
        binding.tvTitleTotalDetail.text = "${title}:${idAccount}"
    }
}