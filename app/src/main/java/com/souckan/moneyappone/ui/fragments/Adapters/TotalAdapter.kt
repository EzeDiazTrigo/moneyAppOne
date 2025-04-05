package com.souckan.moneyappone.ui.fragments.Adapters

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private val onItemClick: (Int) -> Unit,  // Callback con el ID de la cuenta
    private val onEditClick: (TotalWithDetails) -> Unit,
    private val onDeleteClick: (TotalWithDetails) -> Unit
) : RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {

    class TotalViewHolder(val binding: ItemCardTotalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val binding =
            ItemCardTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val total = totals[position]
        val context = holder.itemView.context
        val red = ContextCompat.getColor(context, R.color.red)
        val green = ContextCompat.getColor(context, R.color.green)
        val zero = ContextCompat.getColor(context, R.color.white)
        when{
            (total.totalAmount < 0) -> {
                holder.binding.tvAmount.setTextColor(red)
                holder.binding.tvAmount.text = "-$${total.totalAmount}"
            }
            (total.totalAmount > 0) -> {
                holder.binding.tvAmount.setTextColor(green)
                holder.binding.tvAmount.text = "$${total.totalAmount}"
            }
            (total.totalAmount.toFloat() == 0.0F) -> {
                holder.binding.tvAmount.setTextColor(zero)
                holder.binding.tvAmount.text = "$${total.totalAmount}"
            }
        }

        holder.binding.tvAccount.text = "${total.accountName}"
        holder.binding.tvCurrency.text = "(${total.currencyName})"

        holder.itemView.setOnClickListener { view ->
            val navController = Navigation.findNavController(view)
            navController.navigate(
                R.id.action_totalFragment_to_totalDetailActivity2,
                bundleOf("idAccount" to total.idAccount)
            )
        }

        holder.itemView.setOnLongClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_total_item, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(total)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(total)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }

    }

    override fun getItemCount(): Int = totals.size

    fun updateTotal(newTotals: MutableList<TotalWithDetails>) {
        totals.clear()
        totals.addAll(newTotals)
        notifyDataSetChanged()
    }
}




