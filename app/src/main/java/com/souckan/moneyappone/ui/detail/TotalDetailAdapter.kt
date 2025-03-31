package com.souckan.moneyappone.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.databinding.ItemTotalDetailBinding

class TotalDetailAdapter(private val bills: MutableList<BillEntity>) :
    RecyclerView.Adapter<TotalDetailAdapter.TotalDetailViewHolder>() {

    class TotalDetailViewHolder(val binding: ItemTotalDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalDetailViewHolder {
        val binding = ItemTotalDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalDetailViewHolder, position: Int) {
        val bill = bills[position]
        holder.binding.tvAmount.text = "Monto: ${bill.amount}"
        holder.binding.tvDescription.text = bill.description
    }

    override fun getItemCount(): Int = bills.size

    fun updateBills(newBills: MutableList<BillEntity>) {
        bills.clear()
        bills.addAll(newBills)
        notifyDataSetChanged()
    }
}
