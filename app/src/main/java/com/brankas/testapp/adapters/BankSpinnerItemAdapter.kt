package com.brankas.testapp.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.brankas.testapp.R
import com.bumptech.glide.Glide
import tap.model.direct.Bank

class BankSpinnerItemAdapter(private val context: Context, private val items: List<Bank>) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        val textView: TextView
        val image: ImageView

        val item = getItem(position)

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.item_bank_spinner, parent, false)
            textView = convertView.findViewById(R.id.textView)
            image = convertView.findViewById(R.id.imageView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val config = context.resources.configuration

                if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    textView.textDirection = View.TEXT_DIRECTION_RTL
                }
            }
            convertView.tag = BankSpinnerItemViewHolder(textView, image)
        } else {
            textView = (convertView.tag as BankSpinnerItemViewHolder).textView
            image = (convertView.tag as BankSpinnerItemViewHolder).image
        }

        textView.text = item.title
        Glide.with(context)
            .load(item.logoUrl)
            .placeholder(R.drawable.ic_banking)
            .into(image)
        return convertView!!
    }

    data class BankSpinnerItemViewHolder(
        val textView: TextView,
        val image: ImageView,
    )

}