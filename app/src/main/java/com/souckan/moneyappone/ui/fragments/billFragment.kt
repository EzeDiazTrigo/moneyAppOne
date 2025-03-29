package com.souckan.moneyappone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.souckan.moneyappone.databinding.FragmentBillBinding


class billFragment : Fragment() {

    private lateinit var _binding: FragmentBillBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}