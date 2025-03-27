package com.souckan.moneyappone.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.souckan.moneyappone.R
import com.souckan.moneyappone.databinding.FragmentServiceBinding


class serviceFragment : Fragment() {

    private lateinit var _binding: FragmentServiceBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}