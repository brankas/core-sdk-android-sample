package com.brankas.testapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.R
import com.brankas.testapp.databinding.ItemBalanceBankBinding
import com.brankas.testapp.viewmodel.BalanceBankItemViewModel
import com.bumptech.glide.Glide

class BalanceBanksAdapter(private var context: Context,
                          private var banks: ArrayList<BalanceBankItemViewModel>,
                          private val isCorporate: Boolean)
    : RecyclerView.Adapter<BalanceBanksAdapter.BalanceBankItemViewHolder>() {

    override fun onBindViewHolder(holder: BalanceBankItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceBankItemViewHolder {
        val binding = ItemBalanceBankBinding.inflate(LayoutInflater.from(context), parent, false)
        return BalanceBankItemViewHolder(binding)
    }

    override fun getItemCount() = banks.filter { it.bank.isCorporate == isCorporate }.size

    inner class BalanceBankItemViewHolder(val binding: ItemBalanceBankBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = banks.filter { it.bank.isCorporate == isCorporate }[position]

            binding.item = item
            binding.executePendingBindings()

            Glide.with(context)
                .load(item.bank.pngLogoUrl)
                .placeholder(R.drawable.ic_banking)
                .into(binding.imageView)
        }

    }

}