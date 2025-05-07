package com.souckan.moneyappone.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.InvalidationTracker
import com.souckan.moneyappone.R
import com.souckan.moneyappone.databinding.ActivityMainBinding
import com.souckan.moneyappone.databinding.DialogAddBillBinding
import com.souckan.moneyappone.databinding.FragmentBillBinding
import com.souckan.moneyappone.ui.fragments.Adapters.BillAdapter
import com.souckan.moneyappone.ui.viewmodels.TotalViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class billFragment : Fragment() {

    private val totalViewModel: TotalViewModel by viewModels()
    private lateinit var billAdapter: BillAdapter
    private lateinit var _binding: FragmentBillBinding
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        billAdapter = BillAdapter(mutableListOf()) { billId ->
            totalViewModel.deleteBill(billId)
        }
        binding.rvBills.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBills.adapter = billAdapter

        // Observar los datos del ViewModel
        totalViewModel.allBillsWithDetails.observe(viewLifecycleOwner) { bills ->
            billAdapter.updateBills(bills.toMutableList())
        }



        binding.addBillButton.setOnClickListener {
            showBillDialog()
        }

    }


    private fun showBillDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogAddBillBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% del ancho
            (resources.displayMetrics.heightPixels * 0.5).toInt()  // 50% del alto
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
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Desplegable Account
        val accounts = totalViewModel.getAllAccountsNames()
        bindingDialog.acAccount.setOnClickListener {
            bindingDialog.acAccount.showDropDown()
        }
        accounts.observe(viewLifecycleOwner, Observer { optionsNames ->
            val uniqueNames = optionsNames.distinct() // Filtra nombres repetidos
            if (uniqueNames.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    uniqueNames
                )
                bindingDialog.acAccount.setAdapter(adapter)
            }
        })

        // Desplegable Currency
        val currency = totalViewModel.getAllCurrenciesNames()
        currency.observe(viewLifecycleOwner, Observer { optionsNames ->
            if (optionsNames.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    optionsNames
                )
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

                bindingDialog.acCurrency.setOnClickListener {
                    bindingDialog.acCurrency.showDropDown()
                }
            }
        })

        // Habilitar el botón solo si los campos obligatorios están completos
        val goldOff = ContextCompat.getColor(requireContext(), R.color.gold)
        val goldOn = ContextCompat.getColor(requireContext(), R.color.secondary)
        bindingDialog.btnAddBill.isEnabled = false
        bindingDialog.btnAddBill.setBackgroundColor(goldOff)

        val requiredFields = listOf(
            bindingDialog.acAccount,
            bindingDialog.acCurrency,
            bindingDialog.edAmount,
            bindingDialog.edDate
        )

        fun validateFields() {
            val isValid = requiredFields.all { it.text.isNotEmpty() }
            if(isValid){
                bindingDialog.btnAddBill.setBackgroundColor(goldOn)
            }
            bindingDialog.btnAddBill.isEnabled = isValid
        }

        requiredFields.forEach { field ->
            when (field) {
                is EditText -> field.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) = validateFields()
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                is AutoCompleteTextView -> field.setOnItemClickListener { _, _, _, _ -> validateFields() }
            }
        }

        bindingDialog.btnAddBill.setOnClickListener {
            val currencyCode = bindingDialog.acCurrency.text.toString()
            val accountName = bindingDialog.acAccount.text.toString()
            val amountText = bindingDialog.edAmount.text.toString()
            val amount: Float = amountText.toFloatOrNull() ?: 0f
            val billDate = formattedDate.toString()
            val description = bindingDialog.edDescription.text.toString()

            totalViewModel.insertBill(
                currencyCode = currencyCode,
                accountName = accountName,
                amount = amount,
                billDate = billDate,
                description = description
            )

            dialog.dismiss()
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


