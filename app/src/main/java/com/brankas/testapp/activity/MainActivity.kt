package com.brankas.testapp.activity

import `as`.brank.sdk.core.CoreError
import `as`.brank.sdk.core.CoreListener
import `as`.brank.sdk.tap.balance.BalanceTapSDK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brankas.testapp.Constants
import com.brankas.testapp.R
import com.brankas.testapp.adapter.BalanceBanksAdapter
import com.brankas.testapp.viewmodel.BalanceBankItemViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.Observables
import tap.model.BankCode
import tap.model.Country
import tap.model.DismissalDialog
import tap.model.Reference
import tap.model.balance.Account
import tap.model.balance.Bank
import tap.request.balance.BalanceTapRequest

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

class MainActivity : FragmentActivity() {

    private lateinit var scrollView: NestedScrollView
    private lateinit var useRememberMe: SwitchCompat
    private lateinit var actionBarText: TextInputEditText
    private lateinit var showActionBar: SwitchCompat
    private lateinit var enableAutoConsent: SwitchCompat
    private lateinit var apiKey: TextInputEditText
    private lateinit var orgName: TextInputEditText
    private lateinit var externalId: TextInputEditText
    private lateinit var successURL: TextInputEditText
    private lateinit var failURL: TextInputEditText
    private lateinit var countrySpinner: AppCompatSpinner
    private lateinit var checkBoxLayout: LinearLayout
    private lateinit var lstBanks: RecyclerView
    private lateinit var lstCorpBanks: RecyclerView
    private lateinit var checkout: AppCompatButton

    private var country = Country.UNKNOWN
    private val bankItems = ArrayList<BalanceBankItemViewModel>()
    private var scrollToBottom = false

    private var subscriber: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollView = findViewById(R.id.scrollView)
        useRememberMe = findViewById(R.id.useRememberMe)
        actionBarText = findViewById(R.id.action_bar_text)
        showActionBar = findViewById(R.id.showActionBar)
        enableAutoConsent = findViewById(R.id.enableAutoConsent)
        apiKey = findViewById(R.id.apiKey)
        orgName = findViewById(R.id.orgName)
        externalId = findViewById(R.id.externalId)
        successURL = findViewById(R.id.successURL)
        failURL = findViewById(R.id.failURL)
        countrySpinner = findViewById(R.id.countrySpinner)
        checkBoxLayout = findViewById(R.id.checkBoxLayout)
        lstBanks = findViewById(R.id.lstBanks)
        lstCorpBanks = findViewById(R.id.lstCorpBanks)
        checkout = findViewById(R.id.checkout)

        checkout.isEnabled = false
        updateAPIKey()
        initBankList()
        initCountrySpinner()
        addListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriber?.let { it.dispose() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 4000) {
            if(resultCode == RESULT_OK) {
                val accounts = data?.getParcelableExtra<Reference<List<Account>>>(
                    BalanceTapSDK.ACCOUNTS)!!.get!!

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

            else {
                val error = data?.getStringExtra(BalanceTapSDK.ERROR)
                val errorCode = data?.getStringExtra(BalanceTapSDK.ERROR_CODE)
                Toast.makeText(this, "$error ($errorCode)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initBankList() {
        lstBanks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = BalanceBanksAdapter(this@MainActivity, bankItems, false)
        }
        lstCorpBanks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = BalanceBanksAdapter(this@MainActivity, bankItems, true)
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
                    0 -> Country.PH
                    1 -> Country.ID
                    else -> Country.TH
                }
                if (selected == country) return

                country = selected
                bankItems.clear()
                lstBanks.adapter?.notifyDataSetChanged()
                lstCorpBanks.adapter?.notifyDataSetChanged()

                BalanceTapSDK.initialize(this@MainActivity, apiKey.text.toString(), null, false)
                BalanceTapSDK.getEnabledBanks(country, object : CoreListener<List<Bank>> {
                    override fun onResult(data: List<Bank>?, error: CoreError?) {
                        data?.let { banks ->
                            bankItems.addAll(banks.sortedBy { it.title.lowercase() }.map {
                                BalanceBankItemViewModel(it)
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

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        countrySpinner.setSelection(0)
    }

    private fun addListeners() {
        findViewById<AppCompatButton>(R.id.autoFill).setOnClickListener {
            updateAPIKey()
            orgName.setText("Organization")
            externalId.setText("External ID")
            successURL.setText("https://google.com")
            failURL.setText("https://hello.com")
            if(showActionBar.isChecked)
                actionBarText.setText("Balance Tap")
            checkout.isEnabled = true
        }

        subscriber?.let { it.dispose() }
        subscriber = Observables.combineLatest(
            Observables.combineLatest(orgName.textChanges(), externalId.textChanges(), successURL.textChanges()),
            failURL.textChanges()
        ).subscribe {
            enableCheckout()
        }

        showActionBar.setOnCheckedChangeListener { buttonView, isChecked ->
            actionBarText.isEnabled = isChecked
        }

        checkout.setOnClickListener {
            val request = BalanceTapRequest.Builder()
                .country(country)
                .externalId(externalId.text.toString())
                .successURL(successURL.text.toString())
                .failURL(failURL.text.toString())
                .organizationName(orgName.text.toString())
                .dismissalDialog(
                    DismissalDialog("Do you want to close the application?",
                        "Yes", "No")
                ).apply {
                    bankCodes(getBankCodes())
                }.build()

            BalanceTapSDK.initialize(this, apiKey.text.toString(), null, false)
            BalanceTapSDK.checkout(this, request, object:
                CoreListener<String?> {
                override fun onResult(data: String?, error: CoreError?) {
                    error?.let {
                        Toast.makeText(this@MainActivity, it.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }, useRememberMe = useRememberMe.isChecked, isAutoConsent = enableAutoConsent.isChecked, requestCode = 4000,
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

}