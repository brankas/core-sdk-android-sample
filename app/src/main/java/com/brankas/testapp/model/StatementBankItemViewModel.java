package com.brankas.testapp.model;

import tap.model.statement.Bank;

public class StatementBankItemViewModel {
    private Bank bank;
    private boolean selected;

    public StatementBankItemViewModel(Bank bank, boolean selected) {
        this.bank = bank;
        this.selected = selected;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
