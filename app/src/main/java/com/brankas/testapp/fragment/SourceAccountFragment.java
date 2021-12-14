package com.brankas.testapp.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.brankas.testapp.R;
import com.brankas.testapp.adapter.BankAdapter;
import com.brankas.testapp.customview.BoxedEditText;
import com.brankas.testapp.listener.ScreenListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SourceAccountFragment extends BaseFragment {
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String COUNTRY = "country";
    public static final String BANK_CODE = "bank_code";

    private int currentSelectedCountry = 0;
    private int currentSelectedBank = 0;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_source_account;
    }

    @Override
    public void initDetails() {
        initCountrySpinner();
    }

    @Override
    public void showError(String tag) {
        if(tag != null) {
            switch (tag) {
                case EMAIL:
                    ((BoxedEditText) parentView.findViewById(R.id.email)).setText(
                            getString(R.string.invalid_email));
                    break;
            }
        }
        else
            ((BoxedEditText) parentView.findViewById(R.id.email)).hideError();
    }

    @Override
    public void autoFill() {
        ((BoxedEditText) parentView.findViewById(R.id.firstName)).setText("First");
        ((BoxedEditText) parentView.findViewById(R.id.lastName)).setText("Last");
        ((BoxedEditText) parentView.findViewById(R.id.email)).setText("hello@gmail.com");
        ((BoxedEditText) parentView.findViewById(R.id.mobileNumber)).setText("0912345678");
    }

    @Override
    public HashMap<String, Object> getFieldsMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FIRST_NAME, parentView.findViewById(R.id.firstName));
        hashMap.put(LAST_NAME, parentView.findViewById(R.id.lastName));
        hashMap.put(EMAIL, parentView.findViewById(R.id.email));
        hashMap.put(MOBILE_NUMBER, parentView.findViewById(R.id.mobileNumber));
        hashMap.put(COUNTRY, parentView.findViewById(R.id.countrySpinner));
        hashMap.put(BANK_CODE, parentView.findViewById(R.id.bankSpinner));
        return hashMap;
    }

    @Override
    public List<String> getOptionalFields() {
        return new ArrayList();
    }

    @Override
    public int getPage() {
        return 0;
    }

    private void initCountrySpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.countries,
            android.R.layout.simple_spinner_dropdown_item);
        ((AppCompatSpinner) parentView.findViewById(R.id.countrySpinner)).setAdapter(dataAdapter);
        ((AppCompatSpinner) parentView.findViewById(R.id.countrySpinner)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map.put(COUNTRY, dataAdapter.getItem(position).toString());
                currentSelectedCountry = position;
                initBankSpinner(map.get(COUNTRY));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initBankSpinner(String country) {
        TypedArray logos = getResources().obtainTypedArray(country.equals("Philippines") ?
                R.array.phLogos : R.array.idLogos);
        String[] banks = getResources().getStringArray(country.equals("Philippines") ?
                R.array.phBanks : R.array.idBanks);
        BankAdapter bankAdapter = new BankAdapter(requireContext(), logos, banks);
        ((AppCompatSpinner) parentView.findViewById(R.id.bankSpinner)).setAdapter(bankAdapter);
        ((AppCompatSpinner) parentView.findViewById(R.id.bankSpinner)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map.put(BANK_CODE, bankAdapter.getItem(position));
                currentSelectedBank = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static SourceAccountFragment newInstance(ScreenListener screenListener) {
        SourceAccountFragment sourceAccountFragment = new SourceAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LISTENER, screenListener);
        sourceAccountFragment.setArguments(bundle);
        return sourceAccountFragment;
    }
}