package com.brankas.testapp.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.R;

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
public class BankAdapter extends BaseAdapter {

    private Context context;
    private TypedArray logos;
    private String[] banks;
    private LayoutInflater inflater;

    public BankAdapter(Context context, TypedArray logos, String[] banks) {
        this.context = context;
        this.logos = logos;
        this.banks = banks;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return banks.length;
    }

    @Override
    public String getItem(int position) {
        return banks[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView;
        ItemHolder viewHolder;

        /**
         * Create a custom view holder for recycling views
         */
        if(convertView == null) {
            customView = inflater.inflate(R.layout.spinner_item, parent, false);
            viewHolder = new ItemHolder(customView);
            customView.setTag(viewHolder);
        } else {
            customView = convertView;
            viewHolder = (ItemHolder) customView.getTag();
        }

        int resourceId = logos.getResourceId(position, 0);
        if(resourceId == 0)
            viewHolder.logo.setVisibility(View.GONE);
        else {
            viewHolder.logo.setVisibility(View.VISIBLE);
            viewHolder.logo.setImageResource(resourceId);
        }
        viewHolder.text.setText(banks[position]);
        return customView;
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView logo;

        public ItemHolder(View row) {
            super(row);
            text = row.findViewById(R.id.text);
            logo = row.findViewById(R.id.logo);
        }
    }
}