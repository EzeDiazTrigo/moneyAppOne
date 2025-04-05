package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.domain.model.BillWithDetails
import com.souckan.moneyappone.databinding.ItemTotalDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TotalDetailAdapter(private val bills: MutableList<BillWithDetails>) :
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
        val accountLabel = holder.itemView.context.getString(R.string.account)
        val currencyLabel = holder.itemView.context.getString(R.string.currency)
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val context = holder.itemView.context
        val red = ContextCompat.getColor(context, R.color.red)
        val green = ContextCompat.getColor(context, R.color.green)
        if(bill.currencyName == "BTC"){
            if (bill.amount < 0) {
                holder.binding.tvAmount.setTextColor(red)
                holder.binding.tvAmount.text = "- ${String.format("%.8f", kotlin.math.abs(bill.amount * (-1)))}  ${bill.currencyName}"

            } else {
                holder.binding.tvAmount.setTextColor(green)
                holder.binding.tvAmount.text = "+ ${String.format("%.8f", kotlin.math.abs(bill.amount))}  ${bill.currencyName}"
            }
        }else{
            if (bill.amount < 0) {
                holder.binding.tvAmount.setTextColor(red)
                holder.binding.tvAmount.text = "- ${String.format("%.2f", kotlin.math.abs(bill.amount * (-1)))}  ${bill.currencyName}"

            } else {
                holder.binding.tvAmount.setTextColor(green)
                holder.binding.tvAmount.text = "+ ${String.format("%.2f", kotlin.math.abs(bill.amount))}  ${bill.currencyName}"
            }
        }
        holder.binding.tvDescription.text = "$descriptionLabel: ${bill.description}"

        val formattedDate = try {
            val date = inputFormat.parse(bill.billDate)
            date?.let { outputFormat.format(it) } ?: bill.billDate
        } catch (e: Exception) {
            bill.billDate
        }
        holder.binding.tvDate.text = "$dateLabel: ${formattedDate}"
    }

    override fun getItemCount(): Int = bills.size

    fun addTotalDetail(bill: BillWithDetails) {
        bills.add(0, bill) // Agregarlo al inicio para que se muestre arriba
        notifyItemInserted(0)
    }

    fun updateTotalDetails(newTotalDetails: MutableList<BillWithDetails>) {
        bills.clear()
        bills.addAll(newTotalDetails)
        notifyDataSetChanged()  // Notificar que los datos han cambiado
    }
}
