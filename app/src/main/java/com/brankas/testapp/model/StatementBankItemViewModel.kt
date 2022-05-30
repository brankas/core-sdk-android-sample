package com.brankas.testapp.model

import android.view.View
import tap.model.statement.Bank

data class StatementBankItemViewModel (
    var bank: Bank,
    var selected: Boolean = true
) {

    val onCheckChangedListener = View.OnClickListener {
        selected = !selected
    }

}