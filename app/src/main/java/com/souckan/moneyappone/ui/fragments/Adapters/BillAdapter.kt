package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.databinding.ItamCardBillBinding

class BillAdapter(private val bills: MutableList<BillEntity>) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    class BillViewHolder(val binding: ItamCardBillBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItamCardBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        val amountLabel = holder.itemView.context.getString(R.string.amount)
        val accountLabel = holder.itemView.context.getString(R.string.account)
        val descriptionLabel = holder.itemView.context.getString(R.string.description)
        val dateLabel = holder.itemView.context.getString(R.string.date)

        holder.binding.tvAmount.text = "$amountLabel: ${bill.amount}"
        holder.binding.tvAccount.text = "$accountLabel: ${bill.idAccount}"
        holder.binding.tvDescription.text = "$descriptionLabel: ${bill.description}"
        holder.binding.tvDate.text = "$dateLabel: ${bill.billDate}"
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

}
