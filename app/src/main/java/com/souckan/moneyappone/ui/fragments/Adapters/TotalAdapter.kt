package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.databinding.ItemCardTotalBinding

class TotalAdapter(private val totals: MutableList<TotalEntity>) :
    RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {

    class TotalViewHolder(val binding: ItemCardTotalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val binding = ItemCardTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val total = totals[position]
        holder.binding.tvAmount.text = "${total.idAccount}: ${total.totalAmount}"
    }

    override fun getItemCount(): Int = totals.size


    fun updateTotal(newTotals: MutableList<TotalEntity>) {
        totals.clear()
        totals.addAll(newTotals)
        notifyDataSetChanged()  // Notificar que los datos han cambiado
    }

}
