package com.souckan.moneyappone.ui.fragments.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        if (bill.amount >= 0) {
            holder.binding.tvAmount.text = "+ ${bill.amount}  ${bill.currencyName}"
        } else {
            holder.binding.tvAmount.text = "- ${bill.amount * (-1)}  ${bill.currencyName}"
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
            onDeleteClick(bill.idBill)
        }
    }

    override fun getItemCount(): Int = bills.size

    fun updateBills(newBills: MutableList<BillWithDetails>) {
        bills.clear()
        bills.addAll(newBills)
        notifyDataSetChanged()
    }
}

