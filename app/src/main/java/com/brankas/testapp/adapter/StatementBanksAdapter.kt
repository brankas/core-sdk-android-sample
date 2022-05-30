package com.brankas.testapp.adapter

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.R
import com.brankas.testapp.custom.SvgDecoder
import com.brankas.testapp.custom.SvgDrawableTranscoder
import com.brankas.testapp.custom.SvgSoftwareLayerSetter
import com.brankas.testapp.databinding.ItemStatementBankBinding
import com.brankas.testapp.model.StatementBankItemViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.StreamEncoder
import com.bumptech.glide.load.resource.file.FileToStreamDecoder
import com.caverock.androidsvg.SVG
import java.io.InputStream

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

            if (item.bank.logoUrl.endsWith(".svg")) {
                Glide.with(context)
                    .using(Glide.buildStreamModelLoader(Uri::class.java, context), InputStream::class.java)
                    .from(Uri::class.java)
                    .`as`(SVG::class.java)
                    .transcode(SvgDrawableTranscoder(), PictureDrawable::class.java)
                    .sourceEncoder(StreamEncoder())
                    .cacheDecoder(FileToStreamDecoder(SvgDecoder()))
                    .decoder(SvgDecoder())
                    .placeholder(R.drawable.ic_banking)
                    .listener(SvgSoftwareLayerSetter<Uri>())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .load(Uri.parse(item.bank.logoUrl))
                    .into(binding.imageView)
            } else {
                Glide.with(context)
                    .load(item.bank.logoUrl)
                    .placeholder(R.drawable.ic_banking)
                    .into(binding.imageView)
            }
        }

    }

}