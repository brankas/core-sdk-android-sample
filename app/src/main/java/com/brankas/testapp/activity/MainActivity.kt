package com.brankas.testapp.activity

import `as`.brank.sdk.core.CoreError
import `as`.brank.sdk.core.CoreListener
import `as`.brank.sdk.tap.statement.StatementTapSDK
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.Constants
import com.brankas.testapp.R
import com.brankas.testapp.adapter.StatementBanksAdapter
import com.brankas.testapp.adapter.StatementTransactionsAdapter
import com.brankas.testapp.extension.getDateString
import com.brankas.testapp.model.StatementBankItemViewModel
import com.brankas.testapp.model.TransactionItemViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Observables
import tap.model.BankCode
import tap.model.Country
import tap.model.DismissalDialog
import tap.model.Reference
import tap.model.balance.Account
import tap.model.statement.Bank
import tap.model.statement.Statement
import tap.model.statement.StatementResponse
import tap.request.statement.StatementRetrievalRequest
import tap.request.statement.StatementTapRequest
import java.util.*

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

class MainActivity : AppCompatActivity() {

    private lateinit var scrollView: NestedScrollView
    private lateinit var useRememberMe: SwitchCompat
    private lateinit var actionBarText: TextInputEditText
    private lateinit var showActionBar: SwitchCompat
    private lateinit var enableAutoConsent: SwitchCompat
    private lateinit var retrieveStatements: SwitchCompat
    private lateinit var apiKey: TextInputEditText
    private lateinit var orgName: TextInputEditText
    private lateinit var externalId: TextInputEditText
    private lateinit var successURL: TextInputEditText
    private lateinit var failURL: TextInputEditText
    private lateinit var countrySpinner: AppCompatSpinner
    private lateinit var checkBoxLayout: LinearLayout
    private lateinit var lstBanks: RecyclerView
    private lateinit var lstCorpBanks: RecyclerView
    private lateinit var statementRetrievalLayout: LinearLayout
    private lateinit var datePickerStart: DatePicker
    private lateinit var datePickerEnd: DatePicker
    private lateinit var checkout: AppCompatButton
    private lateinit var enableLogging: SwitchCompat
    private lateinit var retrieveBalance: SwitchCompat
    private lateinit var enablePdfUpload: SwitchCompat

    private var country = Country.UNKNOWN

    private val bankItems = ArrayList<StatementBankItemViewModel>()
    private var scrollToBottom = false
    private val statementRetrievalBuilder = StatementRetrievalRequest.Builder()

    private var subscriber: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollView = findViewById(R.id.scrollView)
        useRememberMe = findViewById(R.id.useRememberMe)
        actionBarText = findViewById(R.id.action_bar_text)
        showActionBar = findViewById(R.id.showActionBar)
        enableAutoConsent = findViewById(R.id.enableAutoConsent)
        retrieveStatements = findViewById(R.id.retrieveStatements)
        apiKey = findViewById(R.id.apiKey)
        orgName = findViewById(R.id.orgName)
        externalId = findViewById(R.id.externalId)
        successURL = findViewById(R.id.successURL)
        failURL = findViewById(R.id.failURL)
        countrySpinner = findViewById(R.id.countrySpinner)
        checkBoxLayout = findViewById(R.id.checkBoxLayout)
        lstBanks = findViewById(R.id.lstBanks)
        lstCorpBanks = findViewById(R.id.lstCorpBanks)
        statementRetrievalLayout = findViewById(R.id.statementRetrievalLayout)
        datePickerStart = findViewById(R.id.datePickerStart)
        datePickerEnd = findViewById(R.id.datePickerEnd)
        checkout = findViewById(R.id.checkout)
        enableLogging = findViewById(R.id.enableLogging)
        retrieveBalance = findViewById(R.id.retrieveBalance)
        enablePdfUpload = findViewById(R.id.enablePdfUpload)

        checkout.isEnabled = false
        updateAPIKey()
        initBankList()
        initCountrySpinner()
        initDates()
        addListeners()

        StatementTapSDK.initDownload(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriber?.let { it.dispose() }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2000) {
            if(resultCode == RESULT_OK) {
                val statementResponse = data?.getParcelableExtra<Reference<StatementResponse>>(
                    StatementTapSDK.STATEMENTS)
                val transactionList = mutableListOf<TransactionItemViewModel>()
                var statementId = ""

                statementResponse?.get?.let { response ->
                    response.statementList?.let {
                        statementId = response.statementId
                        it.forEach {
                            it.transactions.forEach { transaction ->
                                transactionList.add(TransactionItemViewModel(transaction, it.account,
                                    false))
                            }
                        }
                    } ?: run {
                        statementId = response.statementId
                    }
                } ?: run {
                    statementId = data?.getStringExtra(StatementTapSDK.STATEMENT_ID)!!
                }

                transactionList.sortBy {
                    it.transaction.date.getDateString()
                }

                if(transactionList.isNotEmpty())
                    transactionList.last().isLast = true

                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                val contentView = layoutInflater.inflate(R.layout.dialog_statement, null)
                val recyclerView = contentView.findViewById<RecyclerView>(R.id.list)
                val closeButton = contentView.findViewById<AppCompatButton>(R.id.closeButton)
                val downloadButton = contentView.findViewById<AppCompatButton>(R.id.downloadButton)
                val statementIdText = contentView.findViewById<AppCompatTextView>(R.id.statementId)

                statementIdText.text = "Statement ID: $statementId"

                if(transactionList.isEmpty())
                    statementIdText.text = statementIdText.text.toString() + "\n\n\nStatement List is Empty"

                val adapter = StatementTransactionsAdapter(this, transactionList)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                dialogBuilder.setView(contentView)
                val dialog = dialogBuilder.create()
                dialog.show()

                val accounts: List<Account>? = statementResponse?.get?.accountList
                closeButton.setOnClickListener {
                    dialog.dismiss()
                    accounts?.let {
                        showAccounts(it)
                    }
                }

                downloadButton.setOnClickListener {
                    dialog.dismiss()
                    accounts?.let {
                        showAccounts(it)
                    }
                    StatementTapSDK.downloadStatement(this@MainActivity, statementId,
                        object: CoreListener<Pair<String?, ByteArray>> {
                            override fun onResult(data: Pair<String?, ByteArray>?, error: CoreError?) {
                                error?.let {
                                    if (Looper.myLooper() == null)
                                        Looper.prepare()
                                    Toast.makeText(this@MainActivity, it.errorMessage,
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }, true)
                }
            }
            else {
                val error = data?.getStringExtra(StatementTapSDK.ERROR)
                val errorCode = data?.getStringExtra(StatementTapSDK.ERROR_CODE)
                Toast.makeText(this, "$error ($errorCode)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initDates() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        datePickerStart.updateDate(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerEnd.maxDate = Calendar.getInstance().timeInMillis
    }

    private fun initBankList() {
        lstBanks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = StatementBanksAdapter(this@MainActivity, bankItems, false)
        }

        lstCorpBanks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = StatementBanksAdapter(this@MainActivity, bankItems, true)
        }
    }

    private fun initCountrySpinner() {
        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.countries,
            R.layout.item_spinner)
        countrySpinner.adapter = dataAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                val selected = when(position) {
                    0 -> Country.ID
                    1 -> Country.PH
                    else -> Country.TH
                }
                if (selected == country) return

                country = selected
                retrieveBanks()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        countrySpinner.setSelection(1)
    }

    private fun retrieveBanks() {
        bankItems.clear()
        lstBanks.adapter?.notifyDataSetChanged()
        lstCorpBanks.adapter?.notifyDataSetChanged()

        StatementTapSDK.initialize(this@MainActivity, apiKey.text.toString(), null, false, enableLogging.isChecked)
        StatementTapSDK.getEnabledBanks(country, retrieveBalance.isChecked, object : CoreListener<List<Bank>> {
            override fun onResult(data: List<Bank>?, error: CoreError?) {
                data?.let { banks ->
                    bankItems.addAll(banks.sortedBy { it.title.lowercase() }.map {
                        StatementBankItemViewModel(it)
                    })
                    lstBanks.adapter?.notifyDataSetChanged()
                    lstCorpBanks.adapter?.notifyDataSetChanged()
                    if (scrollToBottom) {
                        scrollView.postDelayed({
                            scrollView.fullScroll(View.FOCUS_DOWN)
                        }, 100)
                    } else {
                        scrollToBottom = true
                    }
                } ?: run {
                    Toast.makeText(this@MainActivity, error?.errorMessage.orEmpty(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun addListeners() {
        findViewById<AppCompatButton>(R.id.autoFill).setOnClickListener {
            updateAPIKey()
            orgName.setText("Organization")
            externalId.setText("External ID")
            successURL.setText("https://google.com")
            failURL.setText("https://hello.com")
            if(showActionBar.isChecked)
                actionBarText.setText("Statement Tap")
            checkout.isEnabled = true
        }

        subscriber?.let { it.dispose() }
        subscriber = Observables.combineLatest(
            Observables.combineLatest(orgName.textChanges(), externalId.textChanges(), successURL.textChanges()),
            failURL.textChanges()
        ).subscribe {
            enableCheckout()
        }

        retrieveStatements.setOnCheckedChangeListener { buttonView, isChecked ->
            statementRetrievalLayout.visibility = if(isChecked) View.VISIBLE else View.GONE
        }

        retrieveBalance.setOnCheckedChangeListener { buttonView, isChecked ->
            retrieveBanks()
        }

        datePickerStart.setOnDateChangedListener { _, year, month, day ->
            statementRetrievalBuilder.startDate(Calendar.getInstance().apply {
                set(year, month, day)
            })
        }

        datePickerEnd.setOnDateChangedListener { _, year, month, day ->
            statementRetrievalBuilder.endDate(Calendar.getInstance().apply {
                set(year, month, day)
            })
        }

        showActionBar.setOnCheckedChangeListener { buttonView, isChecked ->
            actionBarText.isEnabled = isChecked
        }

        checkout.setOnClickListener {
            val request = StatementTapRequest.Builder()
                .country(country)
                .externalId(externalId.text.toString())
                .successURL(successURL.text.toString())
                .failURL(failURL.text.toString())
                .organizationName(orgName.text.toString())
                .dismissalDialog(
                    DismissalDialog("Do you want to close the application?",
                        "Yes", "No")
                ).apply {
                    if(retrieveStatements.isChecked)
                        statementRetrievalRequest(statementRetrievalBuilder.build())

                    bankCodes(getBankCodes())
                }.includeBalance(retrieveBalance.isChecked)
                .hasPdfUpload(enablePdfUpload.isChecked)
                .build()

            StatementTapSDK.initialize(this, apiKey.text.toString(), null, false, enableLogging.isChecked)
            StatementTapSDK.checkout(this, request, object:
                CoreListener<String?> {
                override fun onResult(data: String?, error: CoreError?) {
                    error?.let {
                        Toast.makeText(this@MainActivity, it.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }, useRememberMe = useRememberMe.isChecked, isAutoConsent = enableAutoConsent.isChecked, requestCode = 2000,
                actionBarText = if(showActionBar.isChecked) actionBarText.text.toString() else null)
        }
    }

    private fun getBankCodes(): List<BankCode> {
        return bankItems
            .filter { it.selected }
            .map { it.bank.bankCode }
    }

    private fun updateAPIKey() {
        apiKey.setText(Constants.API_KEY)
    }

    private fun enableCheckout() {
        checkout.isEnabled = formValidation()
    }

    private fun formValidation() : Boolean {
        if (apiKey.text.isNullOrBlank() || successURL.text.isNullOrBlank()
            || failURL.text.isNullOrBlank() || externalId.text.isNullOrBlank()) {
            return false
        }

        val successURL = successURL.text.toString()
        if (!successURL.startsWith("http://") && !successURL.startsWith("https://") && !successURL.startsWith("www."))
            return false

        val failURL = failURL.text.toString()
        if (!failURL.startsWith("http://") && !failURL.startsWith("https://") && !failURL.startsWith("www."))
            return false

        return true
    }

    private fun showAccounts(accounts: List<Account>) {
        val dialogBuilder = AlertDialog.Builder(this)
        val stringBuilder = StringBuilder()

        accounts.forEach {
            stringBuilder.append("Account: ${it.holderName} - ${it.number}: " +
                    "${it.balance.currency}${it.balance.numInCents.toLong() / 100}")
            stringBuilder.appendLine()
        }

        dialogBuilder.setMessage(stringBuilder.toString())
            .setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

}
