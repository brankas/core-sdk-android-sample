package com.brankas.testapp.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import tap.model.statement.Account;
import tap.model.statement.Transaction;

public class TransactionItemViewModel {

    private Transaction transaction;
    private Account account;
    private boolean isLast;

    public TransactionItemViewModel(Transaction transaction, Account account, boolean isLast) {
        this.transaction = transaction;
        this.account = account;
        this.isLast = isLast;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public String getAccountName() {
        return this.account.getHolderName() + " (" + this.account.getNumber() + ')';
    }

    public String getFormattedAmount() {
        if (null == transaction) return "";

        String currencyName = "";
        if (null != transaction.getAmount() && null != transaction.getAmount().getCurrency())
            currencyName = transaction.getAmount().getCurrency().name();

        Double amount = 0.0;
        try {
            amount = Double.parseDouble(transaction.getAmount().getNumInCents());
        } catch (Exception e) {}

        String type = "";
        if (null != transaction.getType())
            type = transaction.getType().name();

        return currencyName + " " + amount / 100.0 + " - (" + type + ")";
    }

    public String getDateString() {
        if (null == transaction || null == transaction.getDate())
            return "";

        SimpleDateFormat format = new SimpleDateFormat("MMMM d yyyy", Locale.getDefault());
        return format.format(transaction.getDate());
    }

}
