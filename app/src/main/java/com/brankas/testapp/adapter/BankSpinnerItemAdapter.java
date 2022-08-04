package com.brankas.testapp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brankas.testapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

import tap.model.direct.Bank;

public class BankSpinnerItemAdapter extends BaseAdapter {

    private final Context context;
    private final List<Bank> items;

    public BankSpinnerItemAdapter(Context context, List<Bank> items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView;
        ImageView image;

        Bank item = (Bank)getItem(position);

        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_bank_spinner, parent, false);
            textView = convertView.findViewById(R.id.textView);
            image = convertView.findViewById(R.id.imageView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Configuration config = context.getResources().getConfiguration();

                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                }
            }

            convertView.setTag(new BankSpinnerItemViewHolder(textView, image));
        } else {
            textView = ((BankSpinnerItemViewHolder)convertView.getTag()).textView;
            image = ((BankSpinnerItemViewHolder)convertView.getTag()).image;
        }

        textView.setText(item.getTitle());

        Glide.with(context)
                .load(item.getLogoUrl())
                .placeholder(R.drawable.ic_banking)
                .into(image);

        return convertView;
    }

    private static final class BankSpinnerItemViewHolder {

        private final TextView textView;
        private final ImageView image;

        public BankSpinnerItemViewHolder(TextView textView, ImageView image) {
            this.textView = textView;
            this.image = image;
        }

    }

}