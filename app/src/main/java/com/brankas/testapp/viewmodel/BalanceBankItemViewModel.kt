package com.brankas.testapp.viewmodel

import android.view.View
import tap.model.balance.Bank

data class BalanceBankItemViewModel (
    var bank: Bank,
    var selected: Boolean = true
) {

    val onCheckChangedListener = View.OnClickListener {
        selected = !selected
    }

}