package com.souckan.moneyappone.ui.fragments.Adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.souckan.moneyappone.R
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.databinding.ItamCardBillBinding
import com.souckan.moneyappone.domain.model.BillWithDetails
import java.text.SimpleDateFormat
import java.util.Locale

class BillAdapter(private val bills: MutableList<BillWithDetails>, private val onDeleteClick: (Int) -> Unit) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    class BillViewHolder(val binding: ItamCardBillBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding =
            ItamCardBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        val accountLabel = holder.itemView.context.getString(R.string.account)
        val descriptionLabel = holder.itemView.context.getString(R.string.description)
        val dateLabel = holder.itemView.context.getString(R.string.date)
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


        holder.binding.tvAccount.text = "$accountLabel: ${bill.accountName}"
        holder.binding.tvDescription.text = "$descriptionLabel: ${bill.description}"

        val formattedDate = try {
            val date = inputFormat.parse(bill.billDate)
            date?.let { outputFormat.format(it) } ?: bill.billDate
        } catch (e: Exception) {
            bill.billDate
        }

        holder.binding.tvDate.text = "$dateLabel: ${formattedDate}"
        holder.binding.imRemove.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(holder.itemView.context.getString(R.string.warning))
                    .setMessage(holder.itemView.context.getString(R.string.ask_remove))
                    .setPositiveButton(holder.itemView.context.getString(R.string.delete)) { _, _ ->
                        onDeleteClick(bill.idBill)
                    }
                    .setNegativeButton(holder.itemView.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun getItemCount(): Int = bills.size

    fun updateBills(newBills: MutableList<BillWithDetails>) {
        bills.clear()
        bills.addAll(newBills)
        notifyDataSetChanged()
    }
}

