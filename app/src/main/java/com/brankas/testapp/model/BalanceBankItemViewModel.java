package com.brankas.testapp.model;

import tap.model.balance.Bank;

public class BalanceBankItemViewModel {

    private Bank bank;
    private Boolean selected = true;

    public BalanceBankItemViewModel(Bank bank, boolean selected) {
        this.bank = bank;
        this.selected = selected;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
