package com.brankas.testapp.fragment

import com.brankas.testapp.R
import com.brankas.testapp.TestAppApplication
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.customview.BoxedEditText
import java.util.*

class TransferDetailsFragment: BaseFragment() {
    private lateinit var destinationAccountId: BoxedEditText
    private lateinit var memo: BoxedEditText
    private lateinit var amount: BoxedEditText
    private lateinit var referenceId: BoxedEditText

    override fun getLayoutId(): Int {
        return R.layout.fragment_transfer_details
    }

    override fun initDetails() {
        destinationAccountId = parentLayout.findViewById(R.id.destinationAccountId)
        memo = parentLayout.findViewById(R.id.memo)
        amount = parentLayout.findViewById(R.id.amount)
        referenceId = parentLayout.findViewById(R.id.referenceId)
        initDestinationAccountIdMaxLength()
    }

    override fun showError(tag: String?) {
        tag?.let {
            destinationAccountId.showError(getString(R.string.invalid_destination_account))
        } ?: run {
            destinationAccountId.hideError()
        }
    }

    override fun autoFill() {
        destinationAccountId.setText(TestAppApplication.instance.getDestinationAccountId())
        memo.setText("Bank Transfer")
        amount.setText("100")
        referenceId.setText(UUID.randomUUID().toString())
    }

    override fun getFieldsMap(): HashMap<String, Any> {
        val hashMap = hashMapOf<String, Any>()
        hashMap[DESTINATION_ACCOUNT_ID] = destinationAccountId
        hashMap[MEMO] = memo
        hashMap[AMOUNT] = amount
        hashMap[REFERENCE_ID] = referenceId
        return hashMap
    }

    override fun getOptionalFields(): List<String> {
        return emptyList()
    }

    override fun getPage(): Int {
        return 1
    }

    private fun initDestinationAccountIdMaxLength() {
        destinationAccountId.updateMaxLength(MAX_DESTINATION_ACCOUNT_ID)
    }

    fun addAmountPrefix(currency: String) {
        amount.showPrefix(currency)
    }

    companion object {
        const val MAX_DESTINATION_ACCOUNT_ID = 36
        const val DESTINATION_ACCOUNT_ID = "destination_account_id"
        const val MEMO = "memo"
        const val AMOUNT = "amount"
        const val REFERENCE_ID = "reference_id"

        fun newInstance(screenListener: ScreenListener): TransferDetailsFragment {
            return newInstance<TransferDetailsFragment>(screenListener)
        }
    }
}