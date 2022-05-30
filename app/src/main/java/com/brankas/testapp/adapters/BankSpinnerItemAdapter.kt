package com.brankas.testapp.adapters

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.brankas.testapp.R
import com.brankas.testapp.custom.SvgDecoder
import com.brankas.testapp.custom.SvgDrawableTranscoder
import com.brankas.testapp.custom.SvgSoftwareLayerSetter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.StreamEncoder
import com.bumptech.glide.load.resource.file.FileToStreamDecoder
import com.caverock.androidsvg.SVG
import tap.model.direct.Bank
import java.io.InputStream

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
        if (item.logoUrl.endsWith(".svg")) {
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
                .load(Uri.parse(item.logoUrl))
                .into(image)
        } else {
            Glide.with(context)
                .load(item.logoUrl)
                .placeholder(R.drawable.ic_banking)
                .into(image)
        }

        return convertView!!
    }

    data class BankSpinnerItemViewHolder(
        val textView: TextView,
        val image: ImageView,
    )

}