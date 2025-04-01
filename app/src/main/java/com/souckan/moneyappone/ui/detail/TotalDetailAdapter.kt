package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity

import com.souckan.moneyappone.databinding.ItemTotalDetailBinding


class TotalDetailAdapter(private val bills: MutableList<BillEntity>) :
    RecyclerView.Adapter<TotalDetailAdapter.TotalDetailViewHolder>() {

    class TotalDetailViewHolder(val binding: ItemTotalDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalDetailViewHolder {
        val binding =
            ItemTotalDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalDetailViewHolder, position: Int) {
        val bill = bills[position]
        val amountLabel = holder.itemView.context.getString(R.string.amount)
        val descriptionLabel = holder.itemView.context.getString(R.string.description)
        val dateLabel = holder.itemView.context.getString(R.string.date)

        holder.binding.tvAmount.text = "$amountLabel: ${bill.amount}"
        holder.binding.tvDescription.text = "$descriptionLabel: ${bill.description}"
        holder.binding.tvDate.text = "$dateLabel: ${bill.billDate}"
    }

    override fun getItemCount(): Int = bills.size

    fun addTotalDetail(bill: BillEntity) {
        bills.add(0, bill) // Agregarlo al inicio para que se muestre arriba
        notifyItemInserted(0)
    }

    fun updateTotalDetails(newTotalDetails: MutableList<BillEntity>) {
        bills.clear()
        bills.addAll(newTotalDetails)
        notifyDataSetChanged()  // Notificar que los datos han cambiado
    }

}
