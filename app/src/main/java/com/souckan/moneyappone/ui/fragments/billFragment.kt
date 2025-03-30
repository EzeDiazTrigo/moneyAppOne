package com.souckan.moneyappone.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
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
        var formattedDate: String? = null
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
                            formattedDate = String.format("%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay)
                            Log.d("DatePicker", "Fecha seleccionada en formato yyyymmdd: $formattedDate")
                        },
                        year, month, day

                    )
                    datePickerDialog.show()
                }
        //Desplegable Account
        val accounts = totalViewModel.getAllAccountsNames()
        bindingDialog.acAccount.setOnClickListener {
            bindingDialog.acAccount.showDropDown()
        }
        accounts.observe(viewLifecycleOwner, Observer { optionsNames ->
            Log.d("AutoComplete", "Opciones recibidas: $optionsNames") // Verificar datos
            if(optionsNames.isNotEmpty()){
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, optionsNames)
                bindingDialog.acAccount.setAdapter(adapter)
            }
        })

        //Desplegable Currency
        val currency = totalViewModel.getAllCurrenciesNames()
        currency.observe(viewLifecycleOwner, Observer { optionsNames ->
            Log.d("AutoComplete", "Opciones recibidas: $optionsNames") // Verificar datos

            if (optionsNames.isNotEmpty()) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, optionsNames)
                bindingDialog.acCurrency.setAdapter(adapter)

                // Evitar que el usuario escriba valores que no estén en la lista
                bindingDialog.acCurrency.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val selectedText = bindingDialog.acCurrency.text.toString()
                        if (!optionsNames.contains(selectedText)) {
                            bindingDialog.acCurrency.setText("") // Borra el texto si no es válido
                        }
                    }
                }



                // Mostrar la lista al hacer clic
                bindingDialog.acCurrency.setOnClickListener {
                    bindingDialog.acCurrency.showDropDown()
                }
            }
        })


        bindingDialog.btnAddBill.setOnClickListener{
            val currencyCode = bindingDialog.acCurrency.text.toString()
            val accountName = bindingDialog.acAccount.text.toString()
            val amountText = bindingDialog.edAmount.text.toString()
            val amount: Float = amountText.toFloatOrNull() ?: 0f
            val billDate = formattedDate.toString()
            val description = bindingDialog.edDescription.text.toString()
            totalViewModel.insertBill(currencyCode = currencyCode, accountName = "${accountName} (${currencyCode})", amount = amount, billDate = billDate, description = description)
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


