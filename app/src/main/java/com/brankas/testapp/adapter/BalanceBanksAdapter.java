package com.brankas.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.R;
import com.brankas.testapp.model.BalanceBankItemViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BalanceBanksAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private final Context context;
    private final List<BalanceBankItemViewModel> banks;
    private final Boolean isCorporate;

    public BalanceBanksAdapter(Context context, List<BalanceBankItemViewModel> banks, Boolean isCorporate) {
        super();

        this.context = context;
        this.banks = banks;
        this.isCorporate = isCorporate;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BalanceBankItemViewHolder(inflater.inflate(R.layout.item_balance_bank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ArrayList<BalanceBankItemViewModel> filtered = new ArrayList();
            for (BalanceBankItemViewModel item : banks) {
                if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                    filtered.add(item);
            }

            BalanceBankItemViewModel item = filtered.get(position);
            BalanceBankItemViewHolder viewHolder = (BalanceBankItemViewHolder) holder;
            viewHolder.checkbox.setChecked(item.getSelected());
            viewHolder.checkbox.setText(item.getBank().getTitle());
            viewHolder.checkbox.setOnClickListener(view -> {
                item.setSelected(!item.getSelected());
            });

            Glide.with(context)
                    .load(item.getBank().getPngLogoUrl())
                    .placeholder(R.drawable.ic_banking)
                    .into(viewHolder.imageView);

        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (BalanceBankItemViewModel item : banks) {
            if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                count ++;
        }
        return count;
    }

    private class BalanceBankItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatCheckBox checkbox;
        ImageView imageView;

        public BalanceBankItemViewHolder(View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.checkbox);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
