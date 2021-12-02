package com.brankas.testapp.activity

import `as`.brank.sdk.core.CoreError
import `as`.brank.sdk.tap.TapListener
import `as`.brank.sdk.tap.direct.DirectTapSDK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.badoualy.stepperindicator.BuildConfig
import com.brankas.testapp.*
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.adapter.CustomPagerAdapter
import com.brankas.testapp.fragment.BaseFragment
import com.brankas.testapp.fragment.ClientDetailsFragment
import com.brankas.testapp.fragment.SourceAccountFragment
import com.brankas.testapp.fragment.TransferDetailsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import tap.common.*
import tap.common.direct.Account
import tap.common.direct.Amount
import tap.common.direct.Client
import tap.common.direct.Currency
import tap.common.direct.Customer
import tap.direct.DirectTapRequest
import java.util.*
import kotlin.collections.HashMap

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

class MainActivity : FragmentActivity() {
    /**
     * Pertains to the field map to be used for checkout
     */
    private val map = hashMapOf<String, String>()

    /**
     * Pertains to the rotation animation for the progress image
     */
    private val rotateAnimation = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )

    /**
     * Constants pertaining to pages or screens of the [viewPager]
     */
    private val sourceAccountInfo = 0
    private val transferDetailsInfo = 1
    private val clientDetailsInfo = 2

    private var isCheckoutClicked = false

    private val requestCode = 2005

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Provide API KEY
        if(Constants.API_KEY_DIRECT.isEmpty()) {
            showMessage("Please provide API Key inside Constants class!")
            Handler().postDelayed({
                finish()
            }, 3000)
        }
        else {
            initViewPager()
            addConfirmButtonListener()
            addBackButtonListener()
            addSwitchListener()
            addAutoFillListener()
        }
    }

    override fun onResume() {
        super.onResume()
        resetFields()
    }

    override fun onStop() {
        super.onStop()
        showProgress(false)
    }

    /**
     * This function is used to initialize the [viewPager]. It sets the adapter and PageChangeListener
     *
     */
    private fun initViewPager() {
        viewPager.offscreenPageLimit = 0

        viewPager.adapter = CustomPagerAdapter(supportFragmentManager, object : ScreenListener {
            override fun onFieldsFilled(isFilled: Boolean, map: HashMap<String, String>, page: Int) {
                /**
                 * Enables the [confirmButton] only if all of the required fields are filled up and
                 * the current sender fragment is visible
                 */
                if(page == viewPager.currentItem)
                    enableConfirmButton(isFilled)

                map.entries.forEach {
                    this@MainActivity.map[it.key] = it.value
                }
            }
        })

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                back.visibility = if(position > 0) View.VISIBLE else View.GONE
                fillText.text = when (position) {
                    sourceAccountInfo -> getString(R.string.enter_source_account_information)
                    transferDetailsInfo -> getString(R.string.enter_transfer_details)
                    clientDetailsInfo -> getString(R.string.enter_pidp_details)
                    else -> ""
                }
                confirmButton.text = getString(if(position == clientDetailsInfo)
                    R.string.checkout else R.string.next
                )

                if(position == transferDetailsInfo)
                    (getViewPagerFragment(transferDetailsInfo) as TransferDetailsFragment)
                        .addAmountPrefix(getCurrency(map[SourceAccountFragment.COUNTRY]!!).name)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        stepper.setViewPager(viewPager)
    }

    private fun addConfirmButtonListener() {
        confirmButton.setOnClickListener {
            when (viewPager.currentItem) {
                sourceAccountInfo -> {
                    if(Patterns.EMAIL_ADDRESS.matcher(map[SourceAccountFragment.EMAIL]).matches())
                        showPage(transferDetailsInfo)
                    else
                        showError(SourceAccountFragment.EMAIL)
                }
                transferDetailsInfo -> {
                    if(map[TransferDetailsFragment.DESTINATION_ACCOUNT_ID]?.length ==
                        TransferDetailsFragment.MAX_DESTINATION_ACCOUNT_ID)
                            showPage(clientDetailsInfo)
                    else
                        showError(TransferDetailsFragment.DESTINATION_ACCOUNT_ID)
                }
                clientDetailsInfo -> {
                    val logoUrl = map[ClientDetailsFragment.LOGO_URL]
                    val returnUrl = map[ClientDetailsFragment.RETURN_URL]
                    val failUrl = map[ClientDetailsFragment.FAIL_URL]

                    var counter = 3

                    counter = checkWebPattern(logoUrl, ClientDetailsFragment.LOGO_URL, counter)
                    counter = checkWebPattern(returnUrl, ClientDetailsFragment.RETURN_URL, counter)
                    counter = checkWebPattern(failUrl, ClientDetailsFragment.FAIL_URL, counter)

                    if(counter == 3)
                        checkout()
                }
            }
        }
    }

    private fun checkWebPattern(url: String?, key: String, counter: Int): Int {
        url?.let {
            if(it.isEmpty())
                return counter
            if(!Patterns.WEB_URL.matcher(url).matches()) {
                showError(key)
                return counter - 1
            }
        }
        return counter
    }

    private fun showError(tag: String) {
        getViewPagerFragment().showError(tag)
    }

    private fun addBackButtonListener() {
        back.setOnClickListener {
            showPage(viewPager.currentItem - 1)
        }
    }

    private fun enableConfirmButton(isEnabled: Boolean) {
        confirmButton.isEnabled = isEnabled
        confirmButton.setBackgroundColor(
            resources.getColor(
                if (isEnabled)
                    R.color.colorPrimary else R.color.disabledButton
            )
        )
    }

    private fun showPage(page: Int) {
        viewPager.setCurrentItem(page, true)
    }

    private fun checkout() {
        showProgress(true)
        val request = DirectTapRequest.Builder()
            .sourceAccount(createAccount())
            .destinationAccountId(map[TransferDetailsFragment.DESTINATION_ACCOUNT_ID]!!)
            .amount(createAmount(map[SourceAccountFragment.COUNTRY]!!,
                map[TransferDetailsFragment.AMOUNT]!!))
            .memo(map[TransferDetailsFragment.MEMO]!!)
            .customer(createCustomer(map[SourceAccountFragment.FIRST_NAME]!!,
                map[SourceAccountFragment.LAST_NAME]!!, map[SourceAccountFragment.EMAIL]!!,
                map[SourceAccountFragment.MOBILE_NUMBER]!!))
            .referenceId(map[TransferDetailsFragment.REFERENCE_ID]!!)
            .client(createClient(map[ClientDetailsFragment.DISPLAY_NAME],
                map[ClientDetailsFragment.LOGO_URL], map[ClientDetailsFragment.RETURN_URL],
                map[ClientDetailsFragment.FAIL_URL]))
            .showInBrowser(true)
            .dismissalDialog(
                DismissalDialog("Do you want to close the application?",
                "Yes", "No")
            )
            .expiryDate(Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 3)
            })

        isCheckoutClicked = true

        DirectTapSDK.checkout(this, request.build(), object: TapListener<String?> {
            override fun onResult(data: String?, error: CoreError?) {
                error?.let {
                    showProgress(false)
                    showMessage(error?.errorMessage)
                } ?: run {
                    showMessage("Transaction Successful! Here is the transaction id: $data")
                }
            }

            override fun onTapStarted() {
                showProgress(false)
                rootLayout.bringToFront()
                confirmButton.visibility = View.GONE
                rootLayout.visibility = View.VISIBLE
            }

            override fun onTapEnded() {
                confirmButton.visibility = View.VISIBLE
                rootLayout.visibility = View.GONE
                resetFields()
            }
            
        }, requestCode)
    }

    private fun createClient(displayName: String?, logoUrl: String?, returnUrl: String?,
                             failUrl: String?): Client {
        return Client(displayName, logoUrl, returnUrl, failUrl)
    }

    private fun createCustomer(firstName: String, lastName: String, email: String,
                               mobileNumber: String): Customer {
        return Customer(firstName, lastName, email, mobileNumber)
    }

    private fun createAccount(): Account {
        val bankCode = getBankCode(map[SourceAccountFragment.BANK_CODE]!!)
        val country = getCountry(map[SourceAccountFragment.COUNTRY]!!)

        return bankCode?.let {
            Account(it, country)
        } ?: run {
            Account(country = country)
        }
    }

    private fun createAmount(country: String, amount: String): Amount {
        return Amount(getCurrency(country), (amount.toDouble() * 100).toInt().toString())
    }

    private fun getCurrency(country: String): Currency {
        return when(country) {
            "Philippines" -> Currency.PHP
            "Indonesia" -> Currency.IDR
            else -> Currency.UNKNOWN_CURRENCY
        }
    }

    private fun getCountry(country: String): Country {
        return when(country) {
            "Philippines" -> Country.PH
            "Indonesia" -> Country.ID
            else -> Country.UNKNOWN
        }
    }

    private fun getBankCode(bankCode: String): BankCode? {
        return when(bankCode) {
            "BDO" -> BankCode.BDO_PERSONAL
            "BPI" -> BankCode.BPI_PERSONAL
            "MetroBank" -> BankCode.METROBANK_PERSONAL
            "PNB" -> BankCode.PNB_PERSONAL
            "RCBC" -> BankCode.RCBC_PERSONAL
            "Union Bank" -> BankCode.UNIONBANK_PERSONAL
            "BCA" -> BankCode.BCA_PERSONAL
            "BNI" -> BankCode.BNI_PERSONAL
            "BRI" -> BankCode.BRI_PERSONAL
            "Mandiri" -> BankCode.MANDIRI_PERSONAL
            else -> null
        }
    }

    private fun addAutoFillListener() {
        imgLogo.setOnClickListener {
            getViewPagerFragment().autoFill()
        }
    }

    private fun getViewPagerFragment(position: Int? = null): BaseFragment {
        var index = viewPager.currentItem
        position?.let {
            index = it
        }
        return (viewPager.adapter as CustomPagerAdapter).getItem(index)
    }

    private fun showProgress(isShown: Boolean) {
        if (isShown) {
            progressLayout.visibility = View.VISIBLE
            rotateAnimation.duration = 900
            rotateAnimation.repeatCount = Animation.INFINITE
            progress.startAnimation(rotateAnimation)
        } else {
            progressLayout.visibility = View.GONE
            progress.clearAnimation()
        }
    }

    private fun showMessage(message: String?) {
        Snackbar.make(window.decorView.findViewById<ViewGroup>(android.R.id.content),
            message.orEmpty(), Snackbar.LENGTH_LONG).apply {
            setActionTextColor(Color.WHITE)
            view.setBackgroundResource(R.color.colorPrimary)
            view.findViewById<TextView>(R.id.snackbar_text).setTextColor(
                ContextCompat.getColor(
                this@MainActivity, android.R.color.white))
        }.show()
    }

    private fun resetFields() {
        if(isCheckoutClicked) {
            map.clear()
            (viewPager.adapter as CustomPagerAdapter).fragments.clear()
            (viewPager.adapter as CustomPagerAdapter).notifyDataSetChanged()
            viewPager.currentItem = sourceAccountInfo
            (getViewPagerFragment(sourceAccountInfo) as SourceAccountFragment).clearFields()
        }

        isCheckoutClicked = false
    }

    override fun onBackPressed() {
        if(viewPager.currentItem > 0)
            showPage(viewPager.currentItem - 1)
        else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(this@MainActivity.requestCode == requestCode) {
            if(resultCode == RESULT_OK) {
                val transactionId = data?.getStringExtra(DirectTapSDK.TRANSACTION_ID)
                showMessage("Transaction Successful! Here is the transaction id: $transactionId")
                // Call this to clear the saved credentials within Tap Web Application
                // Call this when you detect that there is a different user
                // TapSDK.clearRememberMe(this@MainActivity)
            }

            else {
                data?.getStringExtra(DirectTapSDK.ERROR)?.let {
                    showMessage("$it - ${data.getStringExtra(DirectTapSDK.ERROR_CODE)}")
                }
            }
        }
    }

    private fun addSwitchListener() {
        switchEnv.setOnCheckedChangeListener { _, isChecked ->
            TestAppApplication.instance.updateTap(!isChecked)
        }
    }
}