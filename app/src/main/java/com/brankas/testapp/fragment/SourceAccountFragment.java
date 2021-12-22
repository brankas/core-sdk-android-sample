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
    public static final String COUNTRY = "country";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_source_account;
    }

    @Override
    public void initDetails() {
        initCountrySpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        screenListener.onFieldsFilled(true, map,0);
    }

    @Override
    public void showError(String tag) {

    }

    @Override
    public void autoFill() {

    }

    @Override
    public HashMap<String, Object> getFieldsMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(COUNTRY, parentView.findViewById(R.id.countrySpinner));
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
        map.put(COUNTRY, dataAdapter.getItem(0).toString());
        ((AppCompatSpinner) parentView.findViewById(R.id.countrySpinner)).setAdapter(dataAdapter);
        ((AppCompatSpinner) parentView.findViewById(R.id.countrySpinner)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map.put(COUNTRY, dataAdapter.getItem(position).toString());
                screenListener.onFieldsFilled(true, map, 0);
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