package com.brankas.testapp.model

import tap.model.statement.Account
import tap.model.statement.Transaction

data class TransactionItemViewModel(val transaction: Transaction, val account: Account, var isLast: Boolean) {

    fun getAccountName(): String {
        return "${account.holderName} (${account.number})"
    }

    fun getFormattedAmount(): String {
        return "${transaction.amount.currency.name} ${(transaction.amount.numInCents.toDouble() / 100)} " +
                "- (${transaction.type.name})"
    }
}