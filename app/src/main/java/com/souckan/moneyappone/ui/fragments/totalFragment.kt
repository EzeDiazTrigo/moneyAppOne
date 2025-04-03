package com.souckan.moneyappone.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.souckan.moneyappone.databinding.FragmentTotalBinding
import com.souckan.moneyappone.ui.detail.TotalDetailActivity
import com.souckan.moneyappone.ui.fragments.Adapters.TotalAdapter
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class totalFragment : Fragment() {

    private lateinit var _binding: FragmentTotalBinding
    private val binding get() = _binding!!
    private val totalViewModel: TotalViewModel by viewModels()
    private lateinit var totalAdapter: TotalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Inicializar el RecyclerView y el Adapter
        totalAdapter = TotalAdapter(mutableListOf()) { idAccount ->
            val intent = Intent(requireContext(), billFragment::class.java).apply {
                putExtra("idAccount", idAccount)
            }
            startActivity(intent)
        }
        binding.rvTotal.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTotal.adapter = totalAdapter

        // Observar los datos del ViewModel
        totalViewModel.totalsWithDetails.observe(viewLifecycleOwner) { totals ->
            totalAdapter.updateTotal(totals.toMutableList())
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}