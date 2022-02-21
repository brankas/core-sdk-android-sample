package com.brankas.testapp.activity

import `as`.brank.sdk.core.CoreError
import `as`.brank.sdk.core.CoreListener
import `as`.brank.sdk.tap.statement.StatementTapSDK
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
import com.brankas.testapp.*
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.adapter.CustomPagerAdapter
import com.brankas.testapp.fragment.BaseFragment
import com.brankas.testapp.fragment.ClientDetailsFragment
import com.brankas.testapp.fragment.SourceAccountFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import tap.common.*
import tap.common.statement.Statement
import tap.statement.StatementRetrievalRequest
import tap.statement.StatementTapRequest
import java.util.*

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
    private val clientDetailsInfo = 1

    private var isCheckoutClicked = false

    private val requestCode = 2005

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Provide API KEY
        if(Constants.API_KEY.isEmpty()) {
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
            if (BuildConfig.AUTO_FILL_ENABLED)
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
                    clientDetailsInfo -> getString(R.string.enter_pidp_details)
                    else -> ""
                }
                confirmButton.text = getString(if(position == clientDetailsInfo)
                    R.string.checkout else R.string.next
                )
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        stepper.setViewPager(viewPager)
    }

    private fun addConfirmButtonListener() {
        confirmButton.setOnClickListener {
            when (viewPager.currentItem) {
                sourceAccountInfo -> {
                    showPage(clientDetailsInfo)
                }
                clientDetailsInfo -> {
                    val returnUrl = map[ClientDetailsFragment.RETURN_URL]
                    val failUrl = map[ClientDetailsFragment.FAIL_URL]

                    var counter = 2

                    counter = checkWebPattern(returnUrl, ClientDetailsFragment.RETURN_URL, counter)
                    counter = checkWebPattern(failUrl, ClientDetailsFragment.FAIL_URL, counter)

                    if(counter == 2)
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

        StatementTapSDK.getEnabledBanks(getCountry(map[SourceAccountFragment.COUNTRY]!!),
            object: CoreListener<List<BankCode>> {
            override fun onResult(data: List<BankCode>?, error: CoreError?) {
                data?.let {
                    val request = StatementTapRequest.Builder()
                        .country(getCountry(map[SourceAccountFragment.COUNTRY]!!))
                        .externalId(map[ClientDetailsFragment.EXTERNAL_ID]!!)
                        .successURL(map[ClientDetailsFragment.RETURN_URL]!!)
                        .failURL(map[ClientDetailsFragment.FAIL_URL]!!)
                        .organizationName(map[ClientDetailsFragment.DISPLAY_NAME]!!)
                        .bankCodes(it)
                        // Comment this part if you do not want to do a Statement Retrieval
                        // Default start date is the day before the current day and end date is the current day
                        .statementRetrievalRequest(StatementRetrievalRequest.Builder().build())

                    isCheckoutClicked = true

                    StatementTapSDK.checkout(this@MainActivity, request.build(), object: CoreListener<String?> {
                        override fun onResult(data: String?, error: CoreError?) {
                            resetFields()
                        }
                    }, requestCode)
                }
            }
        })
    }

    private fun getCountry(country: String): Country {
        return when(country) {
            "Philippines" -> Country.PH
            "Indonesia" -> Country.ID
            else -> Country.TH
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
                val statements = data?.getParcelableExtra<Reference<List<Statement>>>(
                    StatementTapSDK.STATEMENTS)!!.get!!
                println("STATEMENTS: "+statements.size)
                statements.forEach {
                    println("ACCOUNT: ${it.account.holderName} ${it.account.number} - ${it.transactions.size}")
                    it.transactions.forEach { transaction ->
                        println("TRANSACTION: ${transaction.id} - ${it.account.holderName}")
                    }
                }
            }

            else {
                data?.getStringExtra(StatementTapSDK.ERROR)?.let {
                    showMessage(it)
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