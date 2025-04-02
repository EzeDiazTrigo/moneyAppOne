package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.databinding.ItamCardBillBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BillAdapter(private val bills: MutableList<BillEntity>, private val onDeleteClick: (Int) -> Unit) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    class BillViewHolder(val binding: ItamCardBillBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding =
            ItamCardBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        val amountLabel = holder.itemView.context.getString(R.string.amount)
        val accountLabel = holder.itemView.context.getString(R.string.account)
        val descriptionLabel = holder.itemView.context.getString(R.string.description)
        val dateLabel = holder.itemView.context.getString(R.string.date)
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.binding.tvAmount.text = "$amountLabel: ${bill.amount}"
        holder.binding.tvAccount.text = "$accountLabel: ${bill.idAccount}"
        holder.binding.tvDescription.text = "$descriptionLabel: ${bill.description}"
        val formattedDate = try {
            val date = inputFormat.parse(bill.billDate)
            date?.let { outputFormat.format(it) } ?: bill.billDate
        } catch (e: Exception) {
            bill.billDate // En caso de error, usa la fecha sin formato
        }
        holder.binding.tvDate.text = "$dateLabel: ${formattedDate}"
    }

    override fun getItemCount(): Int = bills.size

    fun addBill(bill: BillEntity) {
        bills.add(0, bill) // Agregarlo al inicio para que se muestre arriba
        notifyItemInserted(0)
    }

    fun updateBills(newBills: MutableList<BillEntity>) {
        bills.clear()
        bills.addAll(newBills)
        notifyDataSetChanged()  // Notificar que los datos han cambiado
    }

    fun removeBill(billId: Int) {
        val position = bills.indexOfFirst { it.idBill == billId }
        if (position != -1) {
            bills.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}
