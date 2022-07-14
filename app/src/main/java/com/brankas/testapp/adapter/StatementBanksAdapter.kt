package com.brankas.testapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.R
import com.brankas.testapp.databinding.ItemStatementBankBinding
import com.brankas.testapp.model.StatementBankItemViewModel
import com.bumptech.glide.Glide

class StatementBanksAdapter(private var context: Context,
                            private var banks: ArrayList<StatementBankItemViewModel>,
                            private val isCorporate: Boolean)
    : RecyclerView.Adapter<StatementBanksAdapter.StatementBankItemViewHolder>() {

    override fun onBindViewHolder(holder: StatementBankItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementBankItemViewHolder {
        val binding = ItemStatementBankBinding.inflate(LayoutInflater.from(context), parent, false)
        return StatementBankItemViewHolder(binding)
    }

    override fun getItemCount() = banks.filter { it.bank.isCorporate == isCorporate }.size

    inner class StatementBankItemViewHolder(val binding: ItemStatementBankBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = banks.filter { it.bank.isCorporate == isCorporate }[position]

            binding.item = item
            binding.executePendingBindings()

            Glide.with(context)
                .load(item.bank.pngLogoURL)
                .placeholder(R.drawable.ic_banking)
                .into(binding.imageView)
        }

    }

}