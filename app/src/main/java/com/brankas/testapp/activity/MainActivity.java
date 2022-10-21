package com.brankas.testapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

import com.brankas.testapp.Constants;
import com.brankas.testapp.R;
import com.brankas.testapp.adapter.BankSpinnerItemAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import as.brank.sdk.core.CoreError;
import as.brank.sdk.core.CoreListener;
import as.brank.sdk.tap.direct.DirectTapSDK;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import tap.model.Amount;
import tap.model.BankCode;
import tap.model.Country;
import tap.model.Currency;
import tap.model.DismissalDialog;
import tap.model.Reference;
import tap.model.direct.Account;
import tap.model.direct.Bank;
import tap.model.direct.Client;
import tap.model.direct.Customer;
import tap.model.direct.FundTransferFee;
import tap.model.direct.FundTransferLimit;
import tap.model.direct.Transaction;
import tap.request.direct.DirectTapRequest;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

public class MainActivity extends FragmentActivity {

    //Checkout
    private SwitchCompat useRememberMe;
    private SwitchCompat showActionBar;
    private TextInputEditText actionBarText;
    private SwitchCompat enableExpiryDate;
    private SwitchCompat enableLogoURL;
    private TextInputEditText apiKey;
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText emailAddress;
    private TextInputEditText mobileNumber;
    private TextInputEditText destinationAccountId;
    private TextInputEditText amount;
    private TextInputEditText memo;
    private TextInputEditText referenceID;
    private TextInputEditText orgName;
    private TextInputEditText successURL;
    private TextInputEditText failURL;
    private TextInputLayout logoURLLayout;
    private TextInputEditText logoURL;
    private AppCompatSpinner countrySpinner;
    private LinearLayout destinationBankLayout;
    private AppCompatSpinner destinationBankSpinner;
    private LinearLayout sourceBankLayout;
    private AppCompatSpinner sourceBankSpinner;
    private LinearLayout expiryDateLayout;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AppCompatButton checkout;

    //Search
    private AppCompatSpinner menuSpinner;
    private AppCompatButton search;
    private AppCompatButton retrieve;
    private TextInputEditText apiKeySearch;
    private TextInputEditText query;
    private AppCompatSpinner searchBySpinner;

    private Country country = Country.PH;
    private ArrayList<Bank> banks = new ArrayList();
    private Bank selectedDestBank;
    private Bank selectedSourceBank;
    private int searchBy = 0;

    private Disposable subscriber;
    private Disposable searchSubscriber;
    private BankSpinnerItemAdapter destBankSpinnerAdapter;

    private Bank[] idBanks = {
            new Bank(BankCode.UNRECOGNIZED, Country.ID, "None", "",
                    new FundTransferLimit(
                            Currency.UNKNOWN_CURRENCY,
                            fastcheckout.FundTransferLimit.getDefaultInstance()
                    ),
                    new FundTransferFee(
                        Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()
                    ), false, true
            ),
            new Bank(BankCode.BCA_PERSONAL, Country.ID, "BCA", "",
                    new FundTransferLimit(
                            Currency.UNKNOWN_CURRENCY,
                            fastcheckout.FundTransferLimit.getDefaultInstance()
                    ),
                    new FundTransferFee(
                        Currency.UNKNOWN_CURRENCY,
                        fastcheckout.FundTransferFee.getDefaultInstance()
                    ), false, true
            ),
            new Bank(BankCode.BNI_PERSONAL, Country.ID, "BNI", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                    Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
            new Bank(BankCode.BRI_PERSONAL, Country.ID, "BRI", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                    Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
            new Bank(BankCode.MANDIRI_PERSONAL, Country.ID, "Mandiri", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                    Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true)
    };

    private Bank[] phBanks = {
        new Bank(BankCode.UNRECOGNIZED, Country.PH, "None", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.BDO_PERSONAL, Country.PH, "BDO", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.BPI_PERSONAL, Country.PH, "BPI", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.EASTWEST_PERSONAL, Country.PH, "EastWest Bank", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.LANDBANK_PERSONAL, Country.PH, "LandBank", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.METROBANK_PERSONAL, Country.PH, "MetroBank", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.PNB_PERSONAL, Country.PH, "PNB", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.RCBC_PERSONAL, Country.PH, "RCBC", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true),
        new Bank(BankCode.UNIONBANK_PERSONAL, Country.PH, "Union Bank", "", new FundTransferLimit(Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferLimit.getDefaultInstance()), new FundTransferFee(
                Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()), false, true)
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checkout
        useRememberMe = findViewById(R.id.useRememberMe);
        showActionBar = findViewById(R.id.showActionBar);
        actionBarText = findViewById(R.id.action_bar_text);
        enableExpiryDate = findViewById(R.id.enableExpiryDate);
        enableLogoURL = findViewById(R.id.enableLogoURL);
        apiKey = findViewById(R.id.apiKey);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        mobileNumber = findViewById(R.id.mobileNumber);
        destinationAccountId = findViewById(R.id.destinationAccountId);
        amount = findViewById(R.id.amount);
        memo = findViewById(R.id.memo);
        referenceID = findViewById(R.id.referenceID);
        orgName = findViewById(R.id.orgName);
        successURL = findViewById(R.id.successURL);
        failURL = findViewById(R.id.failURL);
        logoURLLayout = findViewById(R.id.logoURLLayout);
        logoURL = findViewById(R.id.logoURL);
        countrySpinner = findViewById(R.id.countrySpinner);
        destinationBankLayout = findViewById(R.id.destinationBankLayout);
        destinationBankSpinner = findViewById(R.id.destinationBankSpinner);
        sourceBankLayout = findViewById(R.id.sourceBankLayout);
        sourceBankSpinner = findViewById(R.id.sourceBankSpinner);
        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        checkout = findViewById(R.id.checkout);

        menuSpinner = findViewById(R.id.menuSpinner);

        //Search
        search = findViewById(R.id.search);
        retrieve = findViewById(R.id.retrieve);
        apiKeySearch = findViewById(R.id.apiKeySearch);
        query = findViewById(R.id.query);
        searchBySpinner = findViewById(R.id.searchBySpinner);

        search.setEnabled(false);
        checkout.setEnabled(false);

        initSearchBySpinner();

        updateAPIKey();
        initCountrySpinner();
        initMenuSpinner();
        addListeners();
        addSearchListeners();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3000) {
            if(resultCode == RESULT_OK) {
                Transaction transaction = data != null ? (Transaction) ((Reference)data.getParcelableExtra(DirectTapSDK.TRANSACTION)).getGet() : null;
                if (null == transaction) return;
                showTransaction(transaction);
            } else {
                String error = null != data ? data.getStringExtra(DirectTapSDK.ERROR) : "";
                String errorCode = null != data ? data.getStringExtra(DirectTapSDK.ERROR_CODE) : "";
                Toast.makeText(this, error + " (" + errorCode + ")", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAPIKey() {
        apiKey.setText(Constants.API_KEY);
        destinationAccountId.setText(Constants.DESTINATION_ACCOUNT_ID);
    }

    private void initSearchBySpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(this, R.array.searchByTransaction,
                R.layout.item_spinner);
        searchBySpinner.setAdapter(dataAdapter);

        searchBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                query.setHint(dataAdapter.getItem(position).toString());
                searchBy = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        searchBySpinner.setSelection(0);
    }

    private void initMenuSpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(this, R.array.menu_items,
                R.layout.item_spinner);
        menuSpinner.setAdapter(dataAdapter);

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                findViewById(R.id.checkoutLayout).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                findViewById(R.id.searchLayout).setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        menuSpinner.setSelection(0);
    }

    private void initCountrySpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(this, R.array.countries_direct, R.layout.item_spinner);
        countrySpinner.setAdapter(dataAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (0 == position) country = Country.ID;
                else country = Country.PH;
                selectedDestBank = null;
                initBankSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initBankSpinner() {
        Bank[] bankList = Country.ID == country ? idBanks : phBanks;
        destBankSpinnerAdapter = new BankSpinnerItemAdapter(this, Arrays.asList(bankList));
        destinationBankSpinner.setAdapter(destBankSpinnerAdapter);
        destinationBankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (null == destBankSpinnerAdapter) return;
                BankSpinnerItemAdapter adapter = destBankSpinnerAdapter;
                selectedDestBank = (Bank) adapter.getItem(position);
                BankCode bankCode = ((Bank) adapter.getItem(position)).getBankCode();

                if (BankCode.UNRECOGNIZED == bankCode) {
                    selectedDestBank = null;
                    banks.clear();
                    selectedSourceBank = null;
                    initSourceBankSpinner();
                    sourceBankLayout.setVisibility(View.GONE);
                    return;
                } else {
                    sourceBankLayout.setVisibility(View.VISIBLE);
                }

                DirectTapSDK.INSTANCE.initialize(MainActivity.this, apiKey.getText().toString(), null, false);
                DirectTapSDK.INSTANCE.getSourceBanks(country, bankCode, (CoreListener<List<Bank>>) (data, error) -> {
                    if (null == data) {
                        String message = (null != error && null != error.getErrorMessage()) ? error.getErrorMessage() : "";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    banks.clear();
                    for(Bank bank: data) {
                        if(bank.isEnabled())
                            banks.add(bank);
                    }

                    selectedSourceBank = null;
                    initSourceBankSpinner();
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSourceBankSpinner() {
        for (Bank bank: banks) {
            for (Bank item: idBanks) {
                if (item.getBankCode() == bank.getBankCode()) item.setLogoUrl(bank.getLogoUrl());
            }
            for (Bank item: phBanks) {
                if (item.getBankCode() == bank.getBankCode()) item.setLogoUrl(bank.getLogoUrl());
            }
        }

        if (null != destBankSpinnerAdapter) destBankSpinnerAdapter.notifyDataSetChanged();

        ArrayList<Bank> bankList = new ArrayList();
        bankList.add(
                new Bank(BankCode.UNRECOGNIZED, country, "None", "",
                     new FundTransferLimit(
                                 Currency.UNKNOWN_CURRENCY,
                                 fastcheckout.FundTransferLimit.getDefaultInstance()
                     ),
                     new FundTransferFee(
                                 Currency.UNKNOWN_CURRENCY, fastcheckout.FundTransferFee.getDefaultInstance()
                     ), false, true
                )
        );
        bankList.addAll(banks);

        BankSpinnerItemAdapter dataAdapter = new BankSpinnerItemAdapter(this, bankList);
        sourceBankSpinner.setAdapter(dataAdapter);
        sourceBankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSourceBank = 0 < position ? banks.get(position - 1) : null;
                enableCheckout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void addSearchListeners() {
        apiKeySearch.setText(Constants.API_KEY);

        if(searchSubscriber != null)
            searchSubscriber.dispose();

        ArrayList<Observable<CharSequence>> list = new ArrayList();
        list.add(RxTextView.textChanges(apiKeySearch));
        list.add(RxTextView.textChanges(query));

        searchSubscriber = Observable.combineLatest(list, args -> {
            for (int i = 0; i < args.length; i ++) {
                if (TextUtils.isEmpty(args[i].toString().trim())) {
                    return false;
                }
            }

            return true;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isEnable -> {
                    enableSearch();
                });


        retrieve.setOnClickListener(view -> {
            DirectTapSDK.INSTANCE.initialize(this, apiKeySearch.getText().toString(), null, false);
            DirectTapSDK.INSTANCE.getLastTransaction((CoreListener<Transaction>) (transaction, coreError) -> {
                showTransaction(transaction, coreError);
            });
        });

        search.setOnClickListener(view -> {
            DirectTapSDK.INSTANCE.initialize(this, apiKeySearch.getText().toString(), null, false);
            if(searchBy == 0)
                DirectTapSDK.INSTANCE.getTransactionById(query.getText().toString(),
                    (CoreListener<Transaction>) (transaction, coreError) -> {
                    showTransaction(transaction, coreError);
                });
            else
                DirectTapSDK.INSTANCE.getTransactionByReferenceId(query.getText().toString(),
                        (CoreListener<Transaction>) (transaction, coreError) -> {
                    showTransaction(transaction, coreError);
                });
        });
    }

    private void addListeners() {
        findViewById(R.id.autoFill).setOnClickListener(view -> {
            updateAPIKey();

            orgName.setText("Organization");
            successURL.setText("https://google.com");
            failURL.setText("https://hello.com");
            firstName.setText("First");
            lastName.setText("Last");
            emailAddress.setText("hello@gmail.com");
            mobileNumber.setText("09123456789");
            amount.setText("10000");
            memo.setText("Memo");
            if(showActionBar.isChecked())
                actionBarText.setText("Direct Test");
            referenceID.setText(getDateString(Calendar.getInstance()));
        });

        if (null != subscriber) subscriber.dispose();

        ArrayList<Observable<CharSequence>> list = new ArrayList();
        list.add(RxTextView.textChanges(apiKey));
        list.add(RxTextView.textChanges(firstName));
        list.add(RxTextView.textChanges(lastName));
        list.add(RxTextView.textChanges(emailAddress));
        list.add(RxTextView.textChanges(mobileNumber));
        list.add(RxTextView.textChanges(destinationAccountId));
        list.add(RxTextView.textChanges(amount));
        list.add(RxTextView.textChanges(memo));
        list.add(RxTextView.textChanges(referenceID));
        list.add(RxTextView.textChanges(orgName));
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

        enableExpiryDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            expiryDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        enableLogoURL.setOnCheckedChangeListener((buttonView, isChecked) -> {
            logoURLLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        showActionBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            actionBarText.setEnabled(isChecked);
        });

        checkout.setOnClickListener(view -> {

            DirectTapRequest.Builder request = new DirectTapRequest.Builder()
                    .sourceAccount(createAccount())
                    .destinationAccountId(destinationAccountId.getText().toString())
                    .amount(createAmount())
                    .memo(memo.getText().toString())
                    .customer(
                            new Customer(firstName.getText().toString(),
                                             lastName.getText().toString(), emailAddress.getText().toString(),
                                             mobileNumber.getText().toString(), null)
                    )
                    .referenceId(referenceID.getText().toString())
                    .client(
                            new Client(orgName.getText().toString(),
                                    enableLogoURL.isChecked() ? logoURL.getText().toString() : null,
                                    successURL.getText().toString(), failURL.getText().toString(), false
                            )
                     )
                    .dismissalDialog(
                            new DismissalDialog("Do you want to close the application?", "Yes", "No")
                    );

            if(enableExpiryDate.isChecked()) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                cal.set(Calendar.MONTH, datePicker.getMonth());
                cal.set(Calendar.YEAR, datePicker.getYear());
                cal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                cal.set(Calendar.MINUTE, timePicker.getMinute());
                request.setExpiryDate(cal);
            }

            DirectTapSDK.INSTANCE.initialize(MainActivity.this, apiKey.getText().toString(), null, false);
            DirectTapSDK.INSTANCE.checkout(MainActivity.this, request.build(), (CoreListener<String>) (data, error) -> {
                if (null != error) {
                    Toast.makeText(MainActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }, 3000, useRememberMe.isChecked(), showActionBar.isChecked() ? actionBarText.getText().toString() : null);
        });
    }

    private Account createAccount() {
        if (null != selectedSourceBank) {
            return new Account(selectedSourceBank.getBankCode(), country);
        } else {
            return new Account(null, country);
        }
    }

    private Amount createAmount() {
        return new Amount(getCurrency(), String.valueOf((long)(Double.parseDouble(amount.getText().toString()) * 100)));
    }

    private Currency getCurrency() {
        switch (country) {
            case PH: return Currency.PHP;
            case ID: return Currency.IDR;
            default: return Currency.UNKNOWN_CURRENCY;
        }
    }

    private void enableCheckout() {
        checkout.setEnabled(formValidation());
    }

    private void enableSearch() {
        search.setEnabled(!apiKeySearch.getText().toString().trim().isEmpty() &&
                !query.getText().toString().trim().isEmpty());
    }

    private Boolean formValidation() {
        if (null == apiKey.getText().toString() || apiKey.getText().toString().trim().isEmpty()
                || null == firstName.getText().toString() || firstName.getText().toString().trim().isEmpty()
                || null == lastName.getText().toString() || lastName.getText().toString().trim().isEmpty()
                || null == emailAddress.getText().toString() || emailAddress.getText().toString().trim().isEmpty()
                || null == mobileNumber.getText().toString() || mobileNumber.getText().toString().trim().isEmpty()
                || null == destinationAccountId.getText().toString() || destinationAccountId.getText().toString().trim().isEmpty()
                || null == amount.getText().toString() || amount.getText().toString().trim().isEmpty()
                || null == memo.getText().toString() || memo.getText().toString().trim().isEmpty()
                || null == referenceID.getText().toString() || referenceID.getText().toString().trim().isEmpty()
                || null == successURL.getText().toString() || successURL.getText().toString().trim().isEmpty()
                || null == failURL.getText().toString() || failURL.getText().toString().trim().isEmpty()
                || null == orgName.getText().toString() || orgName.getText().toString().trim().isEmpty()) {
                return false;
        }

        String emailAddress = this.emailAddress.getText().toString();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches())
            return false;

        String mobileNumber = this.mobileNumber.getText().toString();
        if (!mobileNumber.startsWith("09") || 11 != mobileNumber.length())
            return false;

        String successURL = this.successURL.getText().toString();
        if (!successURL.startsWith("http://") && !successURL.startsWith("https://") && !successURL.startsWith("www."))
            return false;

        String failURL = this.failURL.getText().toString();
        if (!failURL.startsWith("http://") && !failURL.startsWith("https://") && !failURL.startsWith("www."))
            return false;

        Double amount = 0.0;
        try {
            amount = Double.parseDouble(this.amount.getText().toString());
        } catch (Exception e) {
        }

        amount *= 100.0;

        if (0.0 == amount)
            return false;

        if(selectedSourceBank == null)
            return true;

        if (null == selectedDestBank) return false;
        if (null == selectedSourceBank) return false;

        Double min = 0.0;
        if (selectedDestBank.getBankCode() == selectedSourceBank.getBankCode()) {
            try {
                min = Double.parseDouble(selectedSourceBank.getFundTransferLimit().getIntrabankMinLimit().getNumInCents());
            } catch (Exception e) { }
        } else {
            try {
                min = Double.parseDouble(selectedSourceBank.getFundTransferLimit().getInterbankMinLimit().getNumInCents());
            } catch (Exception e) { }
        }

        Double max = 0.0;
        if (selectedDestBank.getBankCode() == selectedSourceBank.getBankCode()) {
            try {
                max = Double.parseDouble(selectedSourceBank.getFundTransferLimit().getIntrabankMaxLimit().getNumInCents());
            } catch (Exception e) { }
        } else {
            try {
                max = Double.parseDouble(selectedSourceBank.getFundTransferLimit().getInterbankMaxLimit().getNumInCents());
            } catch (Exception e) { }
        }

        if (amount < min || amount > max)
            return false;

        return true;
    }

    private String getDateString(Calendar calendar) {
        if (null == calendar) return "";

        SimpleDateFormat format = new SimpleDateFormat("MMMM d yyyy hh:mm:ss", Locale.getDefault());
        return format.format(calendar.getTimeInMillis());
    }

    private void showTransaction(Transaction data, CoreError error) {
        if(data != null)
            showTransaction(data);
        else
            Toast.makeText(this, error.getErrorMessage() + "("
                    + error.getErrorCode().getCode(), Toast.LENGTH_LONG).show();
    }

    private void showTransaction(Transaction transaction) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("TRANSACTION (");
        stringBuilder.append(null != transaction.getId() ? transaction.getId() : "");
        stringBuilder.append("):");
        stringBuilder.append("\n");
        stringBuilder.append("Reference ID: ");
        stringBuilder.append(null != transaction.getReferenceId() ? transaction.getReferenceId() : "");
        stringBuilder.append("\n");
        stringBuilder.append("Status: ");
        stringBuilder.append(null != transaction.getStatus() ? transaction.getStatus().name() : "");
        stringBuilder.append("\n");
        stringBuilder.append("Status Code: ");
        stringBuilder.append((null != transaction.getStatusMessage() ? transaction.getStatusMessage() : "") + " (" + (null != transaction.getStatusCode() ? transaction.getStatusCode() : "") + ")");
        stringBuilder.append("\n");
        stringBuilder.append("Bank: ");
        stringBuilder.append((null != transaction.getBankCode() ? transaction.getBankCode().name() : "") + " " + (null != transaction.getCountry() ? transaction.getCountry().name() : ""));
        stringBuilder.append("\n");
        if (null != transaction.getAmount() && null != transaction.getAmount().getNumInCents() && !transaction.getAmount().getNumInCents().isEmpty()) {
            stringBuilder.append("Amount: ");
            stringBuilder.append(transaction.getAmount().getCurrency().name()+ " " + ((float)(
                    Integer.parseInt(transaction.getAmount().getNumInCents()) / 100)));
            stringBuilder.append("\n");
        }
        if (null != transaction.getBankFee() && null != transaction.getBankFee().getNumInCents() && !transaction.getBankFee().getNumInCents().isEmpty()) {
            stringBuilder.append("Bank Fee: ");
            stringBuilder.append(transaction.getBankFee().getCurrency().name() + " " + ((float)(
                    Integer.parseInt(transaction.getBankFee().getNumInCents()) / 100)));
            stringBuilder.append("\n");
        }
        stringBuilder.append("Date: ");
        stringBuilder.append(getDateString(transaction.getFinishedDate()));
        stringBuilder.append("\n");

        dialogBuilder.setMessage(stringBuilder.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

}