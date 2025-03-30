package com.souckan.moneyappone.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.InvalidationTracker
import com.souckan.moneyappone.R
import com.souckan.moneyappone.databinding.DialogAddBillBinding
import com.souckan.moneyappone.databinding.FragmentBillBinding
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import java.util.Calendar


class billFragment : Fragment() {

    private val totalViewModel: TotalViewModel by viewModels()
    private lateinit var _binding: FragmentBillBinding
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBillButton.setOnClickListener{
            showBillDialog()
        }
    }



    private fun showBillDialog(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_bill)

        val bindingDialog = DialogAddBillBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% del ancho
            (resources.displayMetrics.heightPixels * 0.5).toInt()  // 80% del alto
        )

        bindingDialog.edDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    bindingDialog.edDate.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val accounts = totalViewModel.getAllAccountsNames()
        val currencies = totalViewModel.getAllCurrencies()
        //val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, accounts)
        /*accounts.observe(viewLifecycleOwner, Observer { optionsNames ->
            if(optionsNames.isNotEmpty()){
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, optionsNames)
                bindingDialog.acAccount.setAdapter(adapter)
            }
        })*/

        bindingDialog.btnAddBill.setOnClickListener{

            dialog.hide()
        }

        dialog.show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}


