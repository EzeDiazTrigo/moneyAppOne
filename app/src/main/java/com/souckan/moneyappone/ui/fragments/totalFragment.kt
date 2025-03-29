package com.souckan.moneyappone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.souckan.moneyappone.databinding.FragmentTotalBinding


class totalFragment : Fragment() {

    private lateinit var _binding: FragmentTotalBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}