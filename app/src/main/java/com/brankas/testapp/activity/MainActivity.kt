package com.brankas.testapp.activity

import `as`.brank.sdk.core.CoreError
import `as`.brank.sdk.core.CoreListener
import `as`.brank.sdk.tap.direct.DirectTapSDK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.brankas.testapp.Constants
import com.brankas.testapp.R
import com.brankas.testapp.adapters.BankSpinnerItemAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.widget.textChanges
import fastcheckout.FundTransferFee
import fastcheckout.FundTransferLimit
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Observables
import tap.model.*
import tap.model.Currency
import tap.model.direct.*
import tap.request.direct.DirectTapRequest
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

class MainActivity : FragmentActivity() {
    // Checkout
    private lateinit var useRememberMe: SwitchCompat
    private lateinit var enableLogging: SwitchCompat
    private lateinit var showActionBar: SwitchCompat
    private lateinit var actionBarText: TextInputEditText
    private lateinit var enableExpiryDate: SwitchCompat
    private lateinit var enableLogoURL: SwitchCompat
    private lateinit var apiKey: TextInputEditText
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var emailAddress: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var destinationAccountId: TextInputEditText
    private lateinit var amount: TextInputEditText
    private lateinit var memo: TextInputEditText
    private lateinit var referenceID: TextInputEditText
    private lateinit var orgName: TextInputEditText
    private lateinit var successURL: TextInputEditText
    private lateinit var failURL: TextInputEditText
    private lateinit var logoURLLayout: TextInputLayout
    private lateinit var logoURL: TextInputEditText
    private lateinit var countrySpinner: AppCompatSpinner
    private lateinit var languageSpinner: AppCompatSpinner
    private lateinit var destinationBankLayout: LinearLayout
    private lateinit var destinationBankSpinner: AppCompatSpinner
    private lateinit var sourceBankLayout: LinearLayout
    private lateinit var sourceBankSpinner: AppCompatSpinner
    private lateinit var expiryDateLayout: LinearLayout
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var checkout: AppCompatButton

    //Search
    private lateinit var menuSpinner: AppCompatSpinner
    private lateinit var search: AppCompatButton
    private lateinit var retrieve: AppCompatButton
    private lateinit var apiKeySearch: TextInputEditText
    private lateinit var query: TextInputEditText
    private lateinit var searchBySpinner: AppCompatSpinner

    private var country = Country.PH
    private var language = Language.ENGLISH
    private var banks = ArrayList<Bank>()
    private var selectedDestBank: Bank? = null
    private var selectedSourceBank: Bank? = null
    private var searchBy = 0

    private var subscriber: Disposable? = null
    private var destBankSpinnerAdapter: BankSpinnerItemAdapter? = null

    private var searchSubscriber: Disposable? = null

    private var idBanks = arrayListOf(
        Bank(
            BankCode.UNRECOGNIZED, Country.ID, "None", "",
            FundTransferLimit(
                Currency.UNKNOWN_CURRENCY,
                FundTransferLimit.getDefaultInstance()
            ), FundTransferFee(
                Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance()
            )
        ),
        Bank(
            BankCode.BCA_PERSONAL, Country.ID, "BCA", "",
            FundTransferLimit(
                Currency.UNKNOWN_CURRENCY,
                FundTransferLimit.getDefaultInstance()
            ), FundTransferFee(
                Currency.UNKNOWN_CURRENCY,
                FundTransferFee.getDefaultInstance()
            )
        ),
        Bank(
            BankCode.BNI_PERSONAL, Country.ID, "BNI", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.BRI_PERSONAL, Country.ID, "BRI", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.MANDIRI_PERSONAL, Country.ID, "Mandiri", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        )
    )
    private var phBanks = arrayListOf(
        Bank(
            BankCode.UNRECOGNIZED, Country.PH, "None", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.BDO_PERSONAL, Country.PH, "BDO", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.BPI_PERSONAL, Country.PH, "BPI", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.EASTWEST_PERSONAL, Country.PH, "EastWest Bank", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.LANDBANK_PERSONAL, Country.PH, "LandBank", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.METROBANK_PERSONAL, Country.PH, "MetroBank", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.PNB_PERSONAL, Country.PH, "PNB", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.RCBC_PERSONAL, Country.PH, "RCBC", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        ),
        Bank(
            BankCode.UNIONBANK_PERSONAL, Country.PH, "Union Bank", "", FundTransferLimit(
                Currency.UNKNOWN_CURRENCY, FundTransferLimit.getDefaultInstance()), FundTransferFee(
            Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance())
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Checkout
        useRememberMe = findViewById(R.id.useRememberMe)
        showActionBar = findViewById(R.id.showActionBar)
        actionBarText = findViewById(R.id.action_bar_text)
        enableExpiryDate = findViewById(R.id.enableExpiryDate)
        enableLogoURL = findViewById(R.id.enableLogoURL)
        apiKey = findViewById(R.id.apiKey)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        emailAddress = findViewById(R.id.emailAddress)
        mobileNumber = findViewById(R.id.mobileNumber)
        destinationAccountId = findViewById(R.id.destinationAccountId)
        amount = findViewById(R.id.amount)
        memo = findViewById(R.id.memo)
        referenceID = findViewById(R.id.referenceID)
        orgName = findViewById(R.id.orgName)
        successURL = findViewById(R.id.successURL)
        failURL = findViewById(R.id.failURL)
        logoURLLayout = findViewById(R.id.logoURLLayout)
        logoURL = findViewById(R.id.logoURL)
        countrySpinner = findViewById(R.id.countrySpinner)
        languageSpinner = findViewById(R.id.languageSpinner)
        destinationBankLayout = findViewById(R.id.destinationBankLayout)
        destinationBankSpinner = findViewById(R.id.destinationBankSpinner)
        sourceBankLayout = findViewById(R.id.sourceBankLayout)
        sourceBankSpinner = findViewById(R.id.sourceBankSpinner)
        expiryDateLayout = findViewById(R.id.expiryDateLayout)
        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        checkout = findViewById(R.id.checkout)
        enableLogging = findViewById(R.id.enableLogging)

        menuSpinner = findViewById(R.id.menuSpinner)

        //Search
        search = findViewById(R.id.search)
        retrieve = findViewById(R.id.retrieve)
        apiKeySearch = findViewById(R.id.apiKeySearch)
        query = findViewById(R.id.query)
        searchBySpinner = findViewById(R.id.searchBySpinner)

        search.isEnabled = false
        checkout.isEnabled = false

        initSearchBySpinner()

        updateAPIKey()
        initCountrySpinner()
        initLanguageSpinner()
        initMenuSpinner()
        addListeners()

        addSearchListeners()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 3000) {
            if(resultCode == RESULT_OK) {
                val transaction = data?.getParcelableExtra<Reference<Transaction>>(
                    DirectTapSDK.TRANSACTION)!!.get!!
                showTransaction(transaction)
            } else {
                val error = data?.getStringExtra(DirectTapSDK.ERROR)
                val errorCode = data?.getStringExtra(DirectTapSDK.ERROR_CODE)
                Toast.makeText(this, "$error ($errorCode)", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updateAPIKey() {
        apiKey.setText(Constants.API_KEY)
        destinationAccountId.setText(Constants.DESTINATION_ACCOUNT_ID)
    }

    private fun initSearchBySpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.searchByTransaction,
            R.layout.item_spinner)
        searchBySpinner.adapter = dataAdapter
        searchBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                query.hint = dataAdapter.getItem(position)
                searchBy = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        searchBySpinner.setSelection(0)
    }

    private fun initMenuSpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.menu_items,
            R.layout.item_spinner)
        menuSpinner.adapter = dataAdapter
        menuSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                findViewById<LinearLayout>(R.id.checkoutLayout).isVisible = position == 0
                findViewById<LinearLayout>(R.id.searchLayout).isVisible = position == 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        menuSpinner.setSelection(0)
    }

    private fun initCountrySpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.countries_direct,
            R.layout.item_spinner)
        countrySpinner.adapter = dataAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                country = when(position) {
                    0 -> Country.ID
                    else -> Country.PH
                }
                selectedDestBank = null
                initBankSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        countrySpinner.setSelection(1)
    }

    private fun initLanguageSpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.languages,
            R.layout.item_spinner)
        languageSpinner.adapter = dataAdapter
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                language = when(position) {
                    0 -> Language.ENGLISH
                    else -> Language.INDONESIAN
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        languageSpinner.setSelection(0)
    }

    private fun initBankSpinner() {
        val bankList = if (Country.ID == country) idBanks else phBanks
        destBankSpinnerAdapter = BankSpinnerItemAdapter(this, bankList)
        destinationBankSpinner.adapter = destBankSpinnerAdapter
        destinationBankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val adapter = destBankSpinnerAdapter ?: return
                selectedDestBank = adapter.getItem(position)
                val bankCode = adapter.getItem(position).bankCode

                if (BankCode.UNRECOGNIZED == bankCode) {
                    selectedDestBank = null
                    banks.clear()
                    selectedSourceBank = null
                    initSourceBankSpinner()
                    sourceBankLayout.visibility = View.GONE
                    return
                } else {
                    sourceBankLayout.visibility = View.VISIBLE
                }

                DirectTapSDK.initialize(this@MainActivity, apiKey.text.toString(), isDebug = false, isLoggingEnabled = enableLogging.isChecked)
                DirectTapSDK.getSourceBanks(country, bankCode, object:
                    CoreListener<List<Bank>> {
                    override fun onResult(data: List<Bank>?, error: CoreError?) {
                        data?.let { bankList ->
                            banks.clear()
                            banks.addAll(bankList.filter { it.isEnabled })
                            selectedSourceBank = null
                            initSourceBankSpinner()
                        } ?: run {
                            error?.errorMessage?.let {
                                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initSourceBankSpinner() {
        banks.forEach { bank ->
            idBanks.forEach {
                if (it.bankCode == bank.bankCode) it.logoUrl = bank.logoUrl
            }
            phBanks.forEach {
                if (it.bankCode == bank.bankCode) it.logoUrl = bank.logoUrl
            }
        }
        destBankSpinnerAdapter?.notifyDataSetChanged()

        var bankList = ArrayList<Bank>()
        bankList.add(Bank(BankCode.UNRECOGNIZED, country, "None", "",
            FundTransferLimit(
                Currency.UNKNOWN_CURRENCY,
                FundTransferLimit.getDefaultInstance()
            ),
            FundTransferFee(
                Currency.UNKNOWN_CURRENCY, FundTransferFee.getDefaultInstance()
            )
        ),)
        bankList.addAll(banks)

        val dataAdapter = BankSpinnerItemAdapter(this, bankList)

        sourceBankSpinner.adapter = dataAdapter
        sourceBankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSourceBank = if (0 < position) banks[position - 1] else null
                enableCheckout()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun addSearchListeners() {
        apiKeySearch.setText(Constants.API_KEY)

        searchSubscriber?.let { it.dispose() }

        searchSubscriber = Observables.combineLatest(apiKeySearch.textChanges(),
            query.textChanges()).subscribe {
            enableSearch()
        }

        retrieve.setOnClickListener {
            DirectTapSDK.initialize(this, apiKeySearch.text.toString(), isDebug = false, isLoggingEnabled = enableLogging.isChecked)
            DirectTapSDK.getLastTransaction(object : CoreListener<Transaction?> {
                override fun onResult(data: Transaction?, error: CoreError?) {
                    showTransaction(data, error)
                }
            })
        }

        search.setOnClickListener {
            DirectTapSDK.initialize(this, apiKeySearch.text.toString(), isDebug = false, isLoggingEnabled = enableLogging.isChecked)
            if (searchBy == 0) {
                DirectTapSDK.getTransactionById(query.text?.toString().orEmpty(),
                    object : CoreListener<Transaction?> {
                        override fun onResult(data: Transaction?, error: CoreError?) {
                            showTransaction(data, error)
                        }
                    })
            } else {
                DirectTapSDK.getTransactionByReferenceId(query.text?.toString()
                    .orEmpty(),
                    object : CoreListener<Transaction?> {
                        override fun onResult(data: Transaction?, error: CoreError?) {
                            showTransaction(data, error)
                        }
                    })
            }
        }
    }

    private fun addListeners() {
        findViewById<AppCompatButton>(R.id.autoFill).setOnClickListener {
            updateAPIKey()
            orgName.setText("Organization")
            successURL.setText("https://google.com")
            failURL.setText("https://hello.com")
            firstName.setText("First")
            lastName.setText("Last")
            emailAddress.setText("hello@gmail.com")
            mobileNumber.setText("09123456789")
            amount.setText("10000")
            memo.setText("Memo")
            if(showActionBar.isChecked)
                actionBarText.setText("Direct Test")
            referenceID.setText(Calendar.getInstance().getDateString())
        }

        subscriber?.let { it.dispose() }
        subscriber = Observables.combineLatest(
            Observables.combineLatest(
                Observables.combineLatest(apiKey.textChanges(), firstName.textChanges(), lastName.textChanges()),
                Observables.combineLatest(emailAddress.textChanges(), mobileNumber.textChanges(), destinationAccountId.textChanges())
            ),
            Observables.combineLatest(amount.textChanges(), memo.textChanges(), referenceID.textChanges()),
            Observables.combineLatest(orgName.textChanges(), successURL.textChanges(), failURL.textChanges())
        ).subscribe {
            enableCheckout()
        }

        enableExpiryDate.setOnCheckedChangeListener { buttonView, isChecked ->
            expiryDateLayout.visibility = if(isChecked) View.VISIBLE else View.GONE
        }

        enableLogoURL.setOnCheckedChangeListener { buttonView, isChecked ->
            logoURLLayout.visibility = if(isChecked) View.VISIBLE else View.GONE
        }

        showActionBar.setOnCheckedChangeListener { buttonView, isChecked ->
            actionBarText.isEnabled = isChecked
        }

        checkout.setOnClickListener {
            val request = DirectTapRequest.Builder()
                .sourceAccount(createAccount())
                .destinationAccountId(destinationAccountId.text.toString())
                .amount(createAmount())
                .memo(memo.text.toString())
                .customer(
                    Customer(firstName.text.toString(),
                        lastName.text.toString(), emailAddress.text.toString(),
                        mobileNumber.text.toString())
                )
                .referenceId(referenceID.text.toString())
                .client(
                    Client(orgName.text.toString(),
                        if(enableLogoURL.isChecked) logoURL.text.toString() else null,
                        successURL.text.toString(), failURL.text.toString(), language = language)
                )
                .dismissalDialog(
                    DismissalDialog("Do you want to close the application?",
                        "Yes", "No")
                )

            if(enableExpiryDate.isChecked)
                request.expiryDate(Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                    set(Calendar.MONTH, datePicker.month)
                    set(Calendar.YEAR, datePicker.year)
                    set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    set(Calendar.MINUTE, timePicker.minute)
                })

            DirectTapSDK.initialize(this, apiKey.text.toString(), isDebug = false, isLoggingEnabled = enableLogging.isChecked)
            DirectTapSDK.checkout(this, request.build(), object:
                CoreListener<String?> {
                override fun onResult(data: String?, error: CoreError?) {
                    error?.let {
                        Toast.makeText(this@MainActivity, "${it.errorCode.getCode()} " +
                                "- ${it.errorCode.getErrorMessage()}", Toast.LENGTH_LONG).show()
                    }
                }

            }, 3000, useRememberMe.isChecked, if(showActionBar.isChecked) actionBarText.text.toString() else null)
        }
    }

    private fun createAccount(): Account {
        return selectedSourceBank?.let {
            Account(it.bankCode, country)
        } ?: run {
            Account(country = country)
        }
    }

    private fun createAmount(): Amount {
        return Amount(getCurrency(), (amount.text.toString().toDouble() * 100).toLong().toString())
    }

    private fun getCurrency(): Currency {
        return when(country) {
            Country.PH -> Currency.PHP
            Country.ID -> Currency.IDR
            else -> Currency.UNKNOWN_CURRENCY
        }
    }

    private fun enableCheckout() {
        checkout.isEnabled = formValidation()
    }

    private fun enableSearch() {
        search.isEnabled = apiKeySearch.text!!.isNotEmpty() && query.text!!.isNotEmpty()
    }

    private fun formValidation() : Boolean {
        if (apiKey.text.isNullOrBlank() || firstName.text.isNullOrBlank()
            || lastName.text.isNullOrBlank() || emailAddress.text.isNullOrBlank()
            || mobileNumber.text.isNullOrBlank() || destinationAccountId.text.isNullOrBlank()
            || amount.text.isNullOrBlank() || memo.text.isNullOrBlank()
            || referenceID.text.isNullOrBlank() || successURL.text.isNullOrBlank()
            || failURL.text.isNullOrBlank() || orgName.text.isNullOrBlank()) {
            return false
        }

        val emailAddress = emailAddress.text.toString()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches())
            return false

        val mobileNumber = mobileNumber.text.toString()
        if (!mobileNumber.startsWith("09") || 11 != mobileNumber.length)
            return false

        val successURL = successURL.text.toString()
        if (!successURL.startsWith("http://") && !successURL.startsWith("https://") && !successURL.startsWith("www."))
            return false

        val failURL = failURL.text.toString()
        if (!failURL.startsWith("http://") && !failURL.startsWith("https://") && !failURL.startsWith("www."))
            return false

        var amount = amount.text.toString().toDoubleOrNull() ?: 0.0
        amount *= 100.0

        if (0.0 == amount)
            return false

        if(selectedSourceBank == null)
            return true;

        val destBank = selectedDestBank ?: return false
        val srcBank = selectedSourceBank ?: return false

        val min = if (destBank.bankCode == srcBank.bankCode)
            srcBank.fundTransferLimit.intrabankMinLimit.numInCents.toDoubleOrNull() ?: 0.0
        else
            srcBank.fundTransferLimit.interbankMinLimit.numInCents.toDoubleOrNull() ?: 0.0

        val max = if (destBank.bankCode == srcBank.bankCode)
            srcBank.fundTransferLimit.intrabankMaxLimit.numInCents.toDoubleOrNull() ?: 0.0
        else
            srcBank.fundTransferLimit.interbankMaxLimit.numInCents.toDoubleOrNull() ?: 0.0

        if (amount < min || amount > max)
            return false

        return true
    }

    fun Calendar.getDateString() : String {
        val format = SimpleDateFormat("MMMM d yyyy hh:mm:ss", Locale.getDefault())
        return format.format(timeInMillis)
    }

    private fun showTransaction(data: Transaction?, error: CoreError?) {
        data?.let {
            showTransaction(data)
        } ?: run {
            Toast.makeText(this@MainActivity, "${error?.errorMessage} " +
                    "(${error?.errorCode?.getCode()})", Toast.LENGTH_LONG).show()
        }
    }

    private fun showTransaction(transaction: Transaction) {
        val dialogBuilder = AlertDialog.Builder(this)
        val stringBuilder = StringBuilder()

        stringBuilder.append("TRANSACTION (")
        stringBuilder.append(transaction.id)
        stringBuilder.append("):")
        stringBuilder.appendLine()
        stringBuilder.append("Reference ID: ")
        stringBuilder.append(transaction.referenceId)
        stringBuilder.appendLine()
        stringBuilder.append("Status: ")
        stringBuilder.append(transaction.status.name)
        stringBuilder.appendLine()
        stringBuilder.append("Status Code: ")
        stringBuilder.append(transaction.statusMessage.orEmpty()+" ("+transaction.statusCode+")")
        stringBuilder.appendLine()
        stringBuilder.append("Bank: ")
        stringBuilder.append(transaction.bankCode.name+" "+transaction.country.name)
        stringBuilder.appendLine()
        if(transaction.amount.numInCents.isNotEmpty()) {
            stringBuilder.append("Amount: ")
            stringBuilder.append(transaction.amount.currency.name + " " + ((
                    transaction.amount.numInCents.toInt() / 100).toFloat()))
            stringBuilder.appendLine()
        }
        if(transaction.bankFee.numInCents.isNotEmpty()) {
            stringBuilder.append("Bank Fee: ")
            stringBuilder.append(transaction.bankFee.currency.name + " " + ((
                    transaction.bankFee.numInCents.toInt() / 100).toFloat()))
            stringBuilder.appendLine()
        }
        stringBuilder.append("Date: ")
        stringBuilder.append(transaction.finishedDate.getDateString())
        stringBuilder.appendLine()

        dialogBuilder.setMessage(stringBuilder.toString())
            .setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

}