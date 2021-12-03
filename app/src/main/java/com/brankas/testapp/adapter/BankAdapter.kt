package com.brankas.testapp.adapter

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.brankas.testapp.R

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

/**
 * Custom Spinner Adapter that shows a list of banks
 *
 * @property context Activity context
 * @property logos [TypedArray] containing drawable resource id's
 * @property banks array containing bank names
 */
class BankAdapter(private val context: Context, private val logos: TypedArray,
                  private val banks: Array<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun getCount(): Int {
        return banks.size
    }

    override fun getItem(position: Int): Any {
        return banks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        var customView: View
        var viewHolder: ItemHolder

        /**
         * Create a custom view holder for recycling views
         */
        if(view == null) {
            customView = inflater.inflate(R.layout.spinner_item, viewGroup, false)
            viewHolder = ItemHolder(customView)
            customView?.tag = viewHolder
        } else {
            customView = view
            viewHolder = customView.tag as ItemHolder
        }

        val resourceId = logos.getResourceId(position, 0)
        if(resourceId == 0)
            viewHolder.logo.visibility = View.GONE
        else {
            viewHolder.logo.visibility = View.VISIBLE
            viewHolder.logo.setImageResource(resourceId)
        }
        viewHolder.text.text = banks[position]
        return customView
    }

    private class ItemHolder(row: View?) {
        val text: TextView = row?.findViewById(R.id.text) as TextView
        val logo: ImageView = row?.findViewById(R.id.logo) as ImageView
    }
}