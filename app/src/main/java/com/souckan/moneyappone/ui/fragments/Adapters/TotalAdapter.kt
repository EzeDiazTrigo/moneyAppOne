package com.souckan.moneyappone.ui.fragments.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.databinding.ItemCardTotalBinding
import com.souckan.moneyappone.domain.model.TotalWithDetails
import com.souckan.moneyappone.ui.detail.TotalDetailActivity

class TotalAdapter(
    private val totals: MutableList<TotalWithDetails>,
    private val onItemClick: (Int) -> Unit  // Callback con el ID de la cuenta
) : RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {

    class TotalViewHolder(val binding: ItemCardTotalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val binding =
            ItemCardTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val total = totals[position]
        holder.binding.tvAmount.text = "${total.accountName}: $${total.totalAmount}"

        // Manejar el clic para abrir la nueva pantalla con las `bills`
        holder.itemView.setOnClickListener { view ->
            val navController = Navigation.findNavController(view)
            navController.navigate(
                R.id.action_totalFragment_to_totalDetailActivity2,
                bundleOf("idAccount" to total.idAccount)
            )
        }

    }

    override fun getItemCount(): Int = totals.size

    fun updateTotal(newTotals: MutableList<TotalWithDetails>) {
        totals.clear()
        totals.addAll(newTotals)
        notifyDataSetChanged()
    }
}


/*class TotalAdapter(private val totals: MutableList<TotalEntity>, private val onItemSelected:(BillEntity)->Unit) :
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

}*/


