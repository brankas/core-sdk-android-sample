package com.brankas.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.R;
import com.brankas.testapp.model.TransactionItemViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StatementTransactionsAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private final Context context;
    private final List<TransactionItemViewModel> transactions;

    public StatementTransactionsAdapter(Context context, List<TransactionItemViewModel> transactions) {
        super();

        this.context = context;
        this.transactions = transactions;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatementTransactionItemViewHolder(inflater.inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            TransactionItemViewModel item = transactions.get(position);

            StatementTransactionItemViewHolder viewHolder = (StatementTransactionItemViewHolder) holder;

            SimpleDateFormat format = new SimpleDateFormat("MMMM d yyyy", Locale.getDefault());
            viewHolder.transactionDate.setText(format.format(item.getTransaction().getDate().getTimeInMillis()));
            viewHolder.transactionAccount.setText(item.getAccountName());
            viewHolder.transactionAmount.setText(item.getFormattedAmount());
            if (item.isLast()) viewHolder.separator.setVisibility(View.GONE);
            else viewHolder.separator.setVisibility(View.VISIBLE);
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private class StatementTransactionItemViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView transactionDate;
        AppCompatTextView transactionAccount;
        AppCompatTextView transactionAmount;
        View separator;

        public StatementTransactionItemViewHolder(View itemView) {
            super(itemView);

            transactionDate = itemView.findViewById(R.id.transactionDate);
            transactionAccount = itemView.findViewById(R.id.transactionAccount);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            separator = itemView.findViewById(R.id.separator);
        }
    }
}
