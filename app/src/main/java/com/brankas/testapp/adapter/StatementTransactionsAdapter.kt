package com.brankas.testapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.databinding.ItemTransactionBinding
import com.brankas.testapp.model.TransactionItemViewModel

class StatementTransactionsAdapter(private val context: Context,
                                   private val transactions: List<TransactionItemViewModel>)
    : RecyclerView.Adapter<StatementTransactionsAdapter.StatementTransactionItemViewHolder>() {

    override fun onBindViewHolder(holder: StatementTransactionItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementTransactionItemViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(context), parent,
            false)
        return StatementTransactionItemViewHolder(binding)
    }

    override fun getItemCount() = transactions.size

    inner class StatementTransactionItemViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.item = transactions[position]
            binding.executePendingBindings()
        }
    }
}