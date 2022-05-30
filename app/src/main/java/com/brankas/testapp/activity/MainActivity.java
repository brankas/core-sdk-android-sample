package com.brankas.testapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.Constants;
import com.brankas.testapp.R;
import com.brankas.testapp.adapter.BalanceBanksAdapter;
import com.brankas.testapp.model.BalanceBankItemViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import as.brank.sdk.core.CoreListener;
import as.brank.sdk.tap.balance.BalanceTapSDK;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import tap.model.BankCode;
import tap.model.Country;
import tap.model.DismissalDialog;
import tap.model.Reference;
import tap.model.balance.Account;
import tap.model.balance.Bank;
import tap.request.balance.BalanceTapRequest;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

public class MainActivity extends FragmentActivity {

    private NestedScrollView scrollView;
    private SwitchCompat useRememberMe;
    private TextInputEditText actionBarText;
    private SwitchCompat showActionBar;
    private SwitchCompat enableAutoConsent;
    private TextInputEditText apiKey;
    private TextInputEditText orgName;
    private TextInputEditText externalId;
    private TextInputEditText successURL;
    private TextInputEditText failURL;
    private AppCompatSpinner countrySpinner;
    private LinearLayout checkBoxLayout;
    private RecyclerView lstBanks;
    private RecyclerView lstCorpBanks;
    private AppCompatButton checkout;

    private Country country = Country.UNKNOWN;
    private ArrayList<BalanceBankItemViewModel> bankItems = new ArrayList();
    private Boolean scrollToBottom = false;

    private Disposable subscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        useRememberMe = findViewById(R.id.useRememberMe);
        actionBarText = findViewById(R.id.action_bar_text);
        showActionBar = findViewById(R.id.showActionBar);
        enableAutoConsent = findViewById(R.id.enableAutoConsent);
        apiKey = findViewById(R.id.apiKey);
        orgName = findViewById(R.id.orgName);
        externalId = findViewById(R.id.externalId);
        successURL = findViewById(R.id.successURL);
        failURL = findViewById(R.id.failURL);
        countrySpinner = findViewById(R.id.countrySpinner);
        checkBoxLayout = findViewById(R.id.checkBoxLayout);
        lstBanks = findViewById(R.id.lstBanks);
        lstCorpBanks = findViewById(R.id.lstCorpBanks);
        checkout = findViewById(R.id.checkout);

        checkout.setEnabled(false);
        updateAPIKey();
        initBankList();
        initCountrySpinner();
        addListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != subscriber) subscriber.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (4000 != requestCode) return;

        if (resultCode != RESULT_OK) {
            String error = (null != data && null != data.getStringExtra(BalanceTapSDK.ERROR))
                    ? data.getStringExtra(BalanceTapSDK.ERROR) : "";
            String errorCode = (null != data && null != data.getStringExtra(BalanceTapSDK.ERROR_CODE))
                    ? data.getStringExtra(BalanceTapSDK.ERROR_CODE) : "";
            Toast.makeText(MainActivity.this, error + " (" + errorCode + ")", Toast.LENGTH_LONG).show();
            return;
        }

        if (null == data) return;

        Reference<List<Account>> accounts = data.getParcelableExtra(BalanceTapSDK.ACCOUNTS);

        if (null == accounts && null == accounts.getGet()) return;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        StringBuilder stringBuilder = new StringBuilder();

        for (Account item : accounts.getGet()) {
            String holderName = null != item.getHolderName() ? item.getHolderName() : "";
            String number = null != item.getNumber() ? item.getNumber() : "";
            String currency = (null != item.getBalance() && null != item.getBalance().getCurrency())
                    ? item.getBalance().getCurrency().name() : "";
            Double numInCents = 0.0;
            try {
                numInCents = Double.parseDouble(item.getBalance().getNumInCents()) / 100.0;
            } catch (Exception e) {}

            stringBuilder.append("Account: " + holderName + " - " + number + ": " +
                            currency + numInCents);
            stringBuilder.append("\n");
        }

        System.out.println("STRING BUILDER: "+stringBuilder.toString());

        dialogBuilder.setMessage(stringBuilder.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

        dialogBuilder.create().show();
    }

    private void initBankList() {
        lstBanks.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        lstBanks.setAdapter(new BalanceBanksAdapter(MainActivity.this, bankItems, false));

        lstCorpBanks.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        lstCorpBanks.setAdapter(new BalanceBanksAdapter(MainActivity.this, bankItems, true));
    }

    private void initCountrySpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(this, R.array.countries,
                R.layout.item_spinner);
        countrySpinner.setAdapter(dataAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Country selected = Country.TH;
                if (0 == position) selected = Country.PH;
                else if (1 == position) selected = Country.ID;

                if (selected == country) return;

                country = selected;
                bankItems.clear();
                lstBanks.getAdapter().notifyDataSetChanged();
                lstCorpBanks.getAdapter().notifyDataSetChanged();

                BalanceTapSDK.INSTANCE.initialize(MainActivity.this, apiKey.getText().toString(), null, false);
                BalanceTapSDK.INSTANCE.getEnabledBanks(country, (CoreListener<List<Bank>>) (banks, error) -> {
                    if (null == banks) {
                        String message = (null != error && null != error.getErrorMessage()) ? error.getErrorMessage() : "";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    banks.sort(Comparator.comparing(bank -> bank.getTitle().toLowerCase()));
                    for (Bank bank : banks) {
                     bankItems.add(new BalanceBankItemViewModel(bank, true));
                    }

                    lstBanks.getAdapter().notifyDataSetChanged();
                    lstCorpBanks.getAdapter().notifyDataSetChanged();
                    if (scrollToBottom) {
                        scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);
                    } else {
                        scrollToBottom = true;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        countrySpinner.setSelection(0);
    }

    private void addListeners() {
        findViewById(R.id.autoFill).setOnClickListener(view -> {
            updateAPIKey();
            orgName.setText("Organization");
            externalId.setText("External ID");
            successURL.setText("https://google.com");
            failURL.setText("https://hello.com");
            if(showActionBar.isChecked());
                actionBarText.setText("Balance Tap");
            checkout.setEnabled(true);
        });

        if (null != subscriber) subscriber.dispose();

        ArrayList<Observable<CharSequence>> list = new ArrayList();
        list.add(RxTextView.textChanges(orgName));
        list.add(RxTextView.textChanges(externalId));
        list.add(RxTextView.textChanges(successURL));
        list.add(RxTextView.textChanges(failURL));

        subscriber = Observable.combineLatest(list, args -> {
            for (int i = 0; i < args.length; i ++) {
                if (TextUtils.isEmpty(args[i].toString().trim())) {
                    return false;
                }
            }
            return true;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isEnable -> {
                    enableCheckout();
                });

        showActionBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            actionBarText.setEnabled(isChecked);
        });

        checkout.setOnClickListener(view -> {
            BalanceTapRequest.Builder builder = new BalanceTapRequest.Builder()
                    .country(country)
                    .externalId(externalId.getText().toString())
                    .successURL(successURL.getText().toString())
                    .failURL(failURL.getText().toString())
                    .organizationName(orgName.getText().toString())
                    .dismissalDialog(
                            new DismissalDialog("Do you want to close the application?",
                                    "Yes", "No")
                    );

            builder.bankCodes(getBankCodes());

            BalanceTapSDK.INSTANCE.initialize(MainActivity.this, apiKey.getText().toString(), null, false);
            BalanceTapSDK.INSTANCE.checkout(MainActivity.this, builder.build(), (CoreListener<String>) (data, error) -> {
                if (null != error) {
                    Toast.makeText(MainActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }, 4000, enableAutoConsent.isChecked(), useRememberMe.isChecked(), showActionBar.isChecked() ? actionBarText.getText().toString() : null);
        });
    }

    private List<BankCode> getBankCodes() {
        ArrayList<BankCode> bankCodes = new ArrayList();
        for (BalanceBankItemViewModel item : bankItems) {
            if (item.getSelected())
                bankCodes.add(item.getBank().getBankCode());
        }
        return bankCodes;
    }

    private void updateAPIKey() {
        apiKey.setText(Constants.API_KEY);
    }

    private void enableCheckout() {
        checkout.setEnabled(formValidation());
    }

    private Boolean formValidation() {
        if (null == apiKey.getText() || apiKey.getText().toString().trim().isEmpty()
                || null == successURL.getText() || successURL.getText().toString().trim().isEmpty()
                || null == failURL.getText() || failURL.getText().toString().trim().isEmpty()
                || null == externalId.getText() || externalId.getText().toString().trim().isEmpty()) {
            return false;
        }

        String successURL = this.successURL.getText().toString();
        if (!successURL.startsWith("http://") && !successURL.startsWith("https://") && !successURL.startsWith("www."))
            return false;

        String failURL = this.failURL.getText().toString();
        if (!failURL.startsWith("http://") && !failURL.startsWith("https://") && !failURL.startsWith("www."))
            return false;

        return true;
    }

}