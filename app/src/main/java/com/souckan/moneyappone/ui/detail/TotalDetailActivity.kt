package com.souckan.moneyappone.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.souckan.moneyappone.R
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.databinding.ActivityTotalDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TotalDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotalDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTotalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}