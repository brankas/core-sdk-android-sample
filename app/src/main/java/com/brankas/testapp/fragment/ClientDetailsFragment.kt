package com.brankas.testapp.fragment

import com.brankas.testapp.R
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.customview.BoxedEditText

class ClientDetailsFragment: BaseFragment() {
    private lateinit var logoUrl: BoxedEditText
    private lateinit var returnUrl: BoxedEditText
    private lateinit var failUrl: BoxedEditText
    private lateinit var displayName: BoxedEditText

    override fun getLayoutId(): Int {
        return R.layout.fragment_client_details
    }

    override fun initDetails() {
        logoUrl = parentLayout.findViewById(R.id.logoUrl)
        returnUrl = parentLayout.findViewById(R.id.returnUrl)
        failUrl = parentLayout.findViewById(R.id.failUrl)
        displayName = parentLayout.findViewById(R.id.displayName)
    }

    override fun showError(tag: String?) {
        tag?.let {
            when(it) {
                LOGO_URL -> logoUrl.showError(getString(R.string.invalid_url_format))
                RETURN_URL -> returnUrl.showError(getString(R.string.invalid_url_format))
                FAIL_URL -> failUrl.showError(getString(R.string.invalid_url_format))
            }
        }
    }

    override fun autoFill() {
        displayName.setText("Display Name")
        returnUrl.setText("google.com.ph")
        failUrl.setText("www.hello.com")
    }

    override fun getFieldsMap(): HashMap<String, Any> {
        val hashMap = hashMapOf<String, Any>()
        hashMap[DISPLAY_NAME] = displayName
        hashMap[LOGO_URL] = logoUrl
        hashMap[RETURN_URL] = returnUrl
        hashMap[FAIL_URL] = failUrl
        return hashMap
    }

    override fun getOptionalFields(): List<String> {
        return listOf(DISPLAY_NAME, LOGO_URL, RETURN_URL, FAIL_URL)
    }

    override fun getPage(): Int {
        return 2
    }

    companion object {
        const val DISPLAY_NAME = "display_name"
        const val LOGO_URL = "logo_url"
        const val RETURN_URL = "return_url"
        const val FAIL_URL = "fail_url"

        fun newInstance(screenListener: ScreenListener): ClientDetailsFragment {
            return newInstance<ClientDetailsFragment>(screenListener)
        }
    }
}