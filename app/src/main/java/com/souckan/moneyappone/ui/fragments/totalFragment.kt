package com.souckan.moneyappone.ui.fragments

import android.R
import android.accounts.Account
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.Observer
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.souckan.moneyappone.data.database.entity.AccountEntity
import com.souckan.moneyappone.databinding.DialogAddAccountBinding
import com.souckan.moneyappone.databinding.DialogAddBillBinding
import com.souckan.moneyappone.databinding.FragmentTotalBinding
import com.souckan.moneyappone.domain.model.TotalWithDetails
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

        totalAdapter = TotalAdapter(
            mutableListOf(),
            onItemClick = { idAccount ->
                val intent = Intent(requireContext(), billFragment::class.java).apply {
                    putExtra("idAccount", idAccount)
                }
                startActivity(intent)
            },
            onEditClick = { total ->
                showEditDialog(total)
            },
            onDeleteClick = { total ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar cuenta")
                    .setMessage("¿Seguro que querés eliminar '${total.accountName}' y todos sus gastos?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        totalViewModel.deleteAccountWithBills(total.idAccount)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        binding.rvTotal.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTotal.adapter = totalAdapter

        totalViewModel.totalsWithDetails.observe(viewLifecycleOwner) { totals ->
            totalAdapter.updateTotal(totals.toMutableList())
        }

        binding.addAccountButton.setOnClickListener {
            showAddAccountDialog()
        }


    }

    private fun showAddAccountDialog() {
        val dialogBinding = DialogAddAccountBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Llenar el AutoCompleteTextView de monedas
        val currency = totalViewModel.getAllCurrenciesNames()
        currency.observe(viewLifecycleOwner, Observer { optionsNames ->
            if (optionsNames.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    optionsNames
                )
                dialogBinding.acCurrency.setAdapter(adapter)

                // Evitar que el usuario escriba valores que no estén en la lista
                dialogBinding.acCurrency.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val selectedText = dialogBinding.acCurrency.text.toString()
                        if (!optionsNames.contains(selectedText)) {
                            dialogBinding.acCurrency.setText("") // Borra el texto si no es válido
                        }
                    }
                }

                dialogBinding.acCurrency.setOnClickListener {
                    dialogBinding.acCurrency.showDropDown()
                }
            }
        })


        // Habilitar el botón solo si los campos obligatorios están completos
        val goldOff = ContextCompat.getColor(requireContext(), com.souckan.moneyappone.R.color.gold)
        val goldOn = ContextCompat.getColor(requireContext(), com.souckan.moneyappone.R.color.secondary)
        dialogBinding.btnAddAccoutn.isEnabled = false
        dialogBinding.btnAddAccoutn.setBackgroundColor(goldOff)

        val requiredFields = listOf(
            dialogBinding.acAccount,
            dialogBinding.acCurrency
        )

        fun validateFields() {
            val isValid = requiredFields.all { it.text.isNotEmpty() }
            if(isValid){
                dialogBinding.btnAddAccoutn.setBackgroundColor(goldOn)
            }
            dialogBinding.btnAddAccoutn.isEnabled = isValid
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

        dialogBinding.btnAddAccoutn.setOnClickListener {
            val currencyCode = dialogBinding.acCurrency.text.toString()
            val accountName = dialogBinding.acAccount.text.toString()
            totalViewModel.addAccount(
                accountName = accountName,
                currencyName = currencyCode
            )
            dialog.dismiss()
        }
        dialog.show()
    }




    private fun showEditDialog(total: TotalWithDetails) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Editar nombre de cuenta")

        val input = EditText(requireContext())
        input.setText(total.accountName)
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                totalViewModel.updateAccountName(total.idAccount, newName)
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}