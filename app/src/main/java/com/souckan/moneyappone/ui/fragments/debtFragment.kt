package com.souckan.moneyappone.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.souckan.moneyappone.data.database.utilities.DatabaseUtils
import com.souckan.moneyappone.databinding.FragmentDebtBinding


class debtFragment : Fragment() {

    private lateinit var _binding: FragmentDebtBinding
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnExport.setOnClickListener {
            startExportDatabase()
        }


        binding.btnImport.setOnClickListener {
            startImportDatabase()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DatabaseUtils.handleImportResult(requestCode, resultCode, data, requireActivity())
    }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            DatabaseUtils.exportDatabase(requireContext(), uri)
        } else {
            Toast.makeText(requireContext(), "Exportación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            DatabaseUtils.importDatabase(requireContext(), uri)
        } else {
            Toast.makeText(requireContext(), "Importación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startImportDatabase() {
        importLauncher.launch(arrayOf("*/*"))
    }


    fun startExportDatabase() {
        exportLauncher.launch("backup_total_database.db")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDebtBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}