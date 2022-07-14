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
import com.brankas.testapp.model.StatementBankItemViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class StatementBanksAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private final Context context;
    private final List<StatementBankItemViewModel> banks;
    private final Boolean isCorporate;

    public StatementBanksAdapter(Context context, List<StatementBankItemViewModel> banks, Boolean isCorporate) {
        super();

        this.context = context;
        this.banks = banks;
        this.isCorporate = isCorporate;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatementBankItemViewHolder(inflater.inflate(R.layout.item_statement_bank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ArrayList<StatementBankItemViewModel> filtered = new ArrayList();
            for (StatementBankItemViewModel item : banks) {
                if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                    filtered.add(item);
            }

            StatementBankItemViewModel item = filtered.get(position);
            StatementBankItemViewHolder viewHolder = (StatementBankItemViewHolder) holder;
            viewHolder.checkbox.setChecked(item.isSelected());
            viewHolder.checkbox.setText(item.getBank().getTitle());
            viewHolder.checkbox.setOnClickListener(view -> {
                item.setSelected(!item.isSelected());
            });

            Glide.with(context)
                    .load(item.getBank().getPngLogoURL())
                    .placeholder(R.drawable.ic_banking)
                    .into(viewHolder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (StatementBankItemViewModel item : banks) {
            if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                count ++;
        }
        return count;
    }

    private class StatementBankItemViewHolder extends RecyclerView.ViewHolder{
        AppCompatCheckBox checkbox;
        ImageView imageView;

        public StatementBankItemViewHolder(View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.checkbox);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
