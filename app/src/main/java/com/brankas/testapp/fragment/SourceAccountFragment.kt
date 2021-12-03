package com.brankas.testapp.fragment
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.brankas.testapp.R
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.adapter.BankAdapter
import com.brankas.testapp.customview.BoxedEditText
import kotlinx.android.synthetic.main.fragment_source_account.*
import kotlinx.android.synthetic.main.spinner_item.view.*


class SourceAccountFragment: BaseFragment() {
    private var currentSelectedCountry = 0

    override fun getLayoutId(): Int {
        return R.layout.fragment_source_account
    }

    override fun initDetails() {
        initCountrySpinner()
        screenListener.onFieldsFilled(true, map,0)
    }

    override fun showError(tag: String?) {

    }

    override fun autoFill() {

    }

    override fun getFieldsMap(): HashMap<String, Any> {
        val hashMap = hashMapOf<String, Any>()
        hashMap[COUNTRY] = countrySpinner
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
                screenListener.onFieldsFilled(true, map,0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    companion object {
        const val COUNTRY = "country"

        fun newInstance(screenListener: ScreenListener): SourceAccountFragment {
            return newInstance<SourceAccountFragment>(screenListener)
        }
    }
}