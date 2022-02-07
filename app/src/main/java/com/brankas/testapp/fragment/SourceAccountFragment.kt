package com.brankas.testapp.fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.brankas.testapp.R
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.adapter.BankAdapter
import com.brankas.testapp.customview.BoxedEditText


class SourceAccountFragment: BaseFragment() {
    private var currentSelectedCountry = 0
    private var currentSelectedBank = 0

    private lateinit var firstName: BoxedEditText
    private lateinit var lastName: BoxedEditText
    private lateinit var email: BoxedEditText
    private lateinit var mobileNumber: BoxedEditText
    private lateinit var countrySpinner: Spinner
    private lateinit var bankSpinner: Spinner

    override fun getLayoutId(): Int {
        return R.layout.fragment_source_account
    }

    override fun initDetails() {
        firstName = parentLayout.findViewById(R.id.firstName)
        lastName = parentLayout.findViewById(R.id.lastName)
        email = parentLayout.findViewById(R.id.email)
        mobileNumber = parentLayout.findViewById(R.id.mobileNumber)
        countrySpinner = parentLayout.findViewById(R.id.countrySpinner)
        bankSpinner = parentLayout.findViewById(R.id.bankSpinner)
        initCountrySpinner()
    }

    override fun showError(tag: String?) {
        tag?.let {
            when(it) {
                EMAIL -> email.showError(getString(R.string.invalid_email))
            }
        } ?: run {
            email.hideError()
        }
    }

    override fun autoFill() {
        firstName.setText("First")
        lastName.setText("Last")
        email.setText("hello@gmail.com")
        mobileNumber.setText("0912345678")
    }

    override fun getFieldsMap(): HashMap<String, Any> {
        val hashMap = hashMapOf<String, Any>()
        hashMap[FIRST_NAME] = firstName
        hashMap[LAST_NAME] = lastName
        hashMap[EMAIL] = email
        hashMap[MOBILE_NUMBER] = mobileNumber
        hashMap[COUNTRY] = countrySpinner
        hashMap[BANK_CODE] = bankSpinner
        return hashMap
    }

    override fun getOptionalFields(): List<String> {
        return emptyList()
    }

    override fun getPage(): Int {
        return 0
    }

    private fun initCountrySpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.countries,
            android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = dataAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                id: Long) {
                map[COUNTRY] = dataAdapter.getItem(position).toString()
                currentSelectedCountry = position
                initBankSpinner(map[COUNTRY]!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initBankSpinner(country: String) {
        val logos = resources.obtainTypedArray(if (country == "Philippines") R.array.phLogos else R.array.idLogos)
        val banks = resources.getStringArray(if (country == "Philippines") R.array.phBanks else R.array.idBanks)
        val bankAdapter = BankAdapter(requireContext(), logos, banks)
        bankSpinner.adapter = bankAdapter
        bankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                map[BANK_CODE] = bankAdapter.getItem(position).toString()
                currentSelectedBank = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    companion object {
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val EMAIL = "email"
        const val MOBILE_NUMBER = "mobile_number"
        const val COUNTRY = "country"
        const val BANK_CODE = "bank_code"

        fun newInstance(screenListener: ScreenListener): SourceAccountFragment {
            return newInstance<SourceAccountFragment>(screenListener)
        }
    }
}