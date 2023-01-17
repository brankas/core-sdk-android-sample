package com.brankas.testapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.Constants;
import com.brankas.testapp.R;
import com.brankas.testapp.adapter.StatementBanksAdapter;
import com.brankas.testapp.adapter.StatementTransactionsAdapter;
import com.brankas.testapp.model.StatementBankItemViewModel;
import com.brankas.testapp.model.TransactionItemViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.collect.Iterables;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import as.brank.sdk.core.CoreError;
import as.brank.sdk.core.CoreListener;
import as.brank.sdk.tap.statement.StatementTapSDK;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import kotlin.Pair;
import tap.model.BankCode;
import tap.model.Country;
import tap.model.DismissalDialog;
import tap.model.Reference;
import tap.model.balance.Account;
import tap.model.statement.Bank;
import tap.model.statement.Statement;
import tap.model.statement.StatementResponse;
import tap.model.statement.Transaction;
import tap.request.statement.StatementRetrievalRequest;
import tap.request.statement.StatementTapRequest;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

public class MainActivity extends AppCompatActivity {

    private NestedScrollView scrollView;
    private SwitchCompat useRememberMe;
    private TextInputEditText actionBarText;
    private SwitchCompat showActionBar;
    private SwitchCompat enableAutoConsent;
    private SwitchCompat retrieveStatements;
    private TextInputEditText apiKey;
    private TextInputEditText orgName;
    private TextInputEditText externalId;
    private TextInputEditText successURL;
    private TextInputEditText failURL;
    private AppCompatSpinner countrySpinner;
    private LinearLayout checkBoxLayout;
    private RecyclerView lstBanks;
    private RecyclerView lstCorpBanks;
    private LinearLayout statementRetrievalLayout;
    private DatePicker datePickerStart;
    private DatePicker datePickerEnd;
    private AppCompatButton checkout;
    private SwitchCompat enableLogging;
    private SwitchCompat retrieveBalance;

    private Country country = Country.UNKNOWN;

    private ArrayList<StatementBankItemViewModel> bankItems = new ArrayList();
    private Boolean scrollToBottom = false;
    private StatementRetrievalRequest.Builder statementRetrievalBuilder = new StatementRetrievalRequest.Builder();

    private Disposable subscriber = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        useRememberMe = findViewById(R.id.useRememberMe);
        actionBarText = findViewById(R.id.action_bar_text);
        showActionBar = findViewById(R.id.showActionBar);
        enableAutoConsent = findViewById(R.id.enableAutoConsent);
        retrieveStatements = findViewById(R.id.retrieveStatements);
        apiKey = findViewById(R.id.apiKey);
        orgName = findViewById(R.id.orgName);
        externalId = findViewById(R.id.externalId);
        successURL = findViewById(R.id.successURL);
        failURL = findViewById(R.id.failURL);
        countrySpinner = findViewById(R.id.countrySpinner);
        checkBoxLayout = findViewById(R.id.checkBoxLayout);
        lstBanks = findViewById(R.id.lstBanks);
        lstCorpBanks = findViewById(R.id.lstCorpBanks);
        statementRetrievalLayout = findViewById(R.id.statementRetrievalLayout);
        datePickerStart = findViewById(R.id.datePickerStart);
        datePickerEnd = findViewById(R.id.datePickerEnd);
        checkout = findViewById(R.id.checkout);
        enableLogging = findViewById(R.id.enableLogging);
        retrieveBalance = findViewById(R.id.retrieveBalance);

        checkout.setEnabled(false);
        updateAPIKey();
        initBankList();
        initCountrySpinner();
        initDates();
        addListeners();

        StatementTapSDK.INSTANCE.initDownload(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != subscriber)
            subscriber.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (2000 != requestCode) return;

        if (resultCode != RESULT_OK) {
            String error = (null != data && null != data.getStringExtra(StatementTapSDK.ERROR))
                    ? data.getStringExtra(StatementTapSDK.ERROR) : "";
            String errorCode = (null != data && null != data.getStringExtra(StatementTapSDK.ERROR_CODE))
                    ? data.getStringExtra(StatementTapSDK.ERROR_CODE) : "";
            Toast.makeText(this, error + " (" + errorCode + ")", Toast.LENGTH_LONG).show();
        }

        if (null == data) return;

        Reference<StatementResponse> statementResponse = data.getParcelableExtra(StatementTapSDK.STATEMENTS);
        ArrayList<TransactionItemViewModel> transactionList = new ArrayList();
        String statementId = "";

        if(statementResponse != null) {
            List<Statement> statements = statementResponse.getGet().getStatementList();
            if (statements != null) {
                for (Statement item : statements) {
                    statementId = item.getId();
                    for (Transaction transaction : item.getTransactions()) {
                        transactionList.add(new TransactionItemViewModel(transaction, item.getAccount(), false));
                    }
                }
            } else {
                statementId = (null != data.getStringExtra(StatementTapSDK.STATEMENT_ID)) ? data.getStringExtra(StatementTapSDK.STATEMENT_ID) : "";
            }
        }

        transactionList.sort(Comparator.comparing(TransactionItemViewModel::getDateString));

        if(!transactionList.isEmpty())
            Iterables.getLast(transactionList).setLast(true);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View contentView = getLayoutInflater().inflate(R.layout.dialog_statement, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.list);
        AppCompatButton closeButton = contentView.findViewById(R.id.closeButton);
        AppCompatButton downloadButton = contentView.findViewById(R.id.downloadButton);
        AppCompatTextView statementIdText = contentView.findViewById(R.id.statementId);

        statementIdText.setText("Statement ID: " + statementId);

        if(transactionList.isEmpty())
            statementIdText.setText(statementIdText.getText().toString() + "\n\n\nStatement List is Empty");

        StatementTransactionsAdapter adapter = new StatementTransactionsAdapter(this, transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dialogBuilder.setView(contentView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        List<Account> accounts = statementResponse != null ?
                statementResponse.getGet().getAccountList() : null;

        closeButton.setOnClickListener(view -> {
            dialog.dismiss();
            if(accounts != null)
                showAccounts(accounts);
        });

        String finalStatementId = statementId;
        downloadButton.setOnClickListener(view -> {
            dialog.dismiss();
            if(accounts != null)
                showAccounts(accounts);
            StatementTapSDK.INSTANCE.downloadStatement(this, finalStatementId, (CoreListener<Pair<String, byte[]>>) (pair, error) -> {
                if (null != error) {
                    if (null == Looper.myLooper())
                        Looper.prepare();

                    Toast.makeText(this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }, true);
        });
    }

    private void initDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        datePickerStart.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerEnd.setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    private void initBankList() {
        lstBanks.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        lstBanks.setAdapter(new StatementBanksAdapter(this, bankItems, false));

        lstCorpBanks.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        lstCorpBanks.setAdapter(new StatementBanksAdapter(this, bankItems, true));
    }

    private void initCountrySpinner() {
        ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(this, R.array.countries, R.layout.item_spinner);
        countrySpinner.setAdapter(dataAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country selected = Country.TH;
                if (0 == position) selected = Country.ID;
                else if (1 == position) selected = Country.PH;

                if (selected == country) return;

                country = selected;
                retrieveBanks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        countrySpinner.setSelection(1);
    }

    private void retrieveBanks() {
        bankItems.clear();
        lstBanks.getAdapter().notifyDataSetChanged();
        lstCorpBanks.getAdapter().notifyDataSetChanged();

        StatementTapSDK.INSTANCE.initialize(MainActivity.this, apiKey.getText().toString(), null, false, enableLogging.isChecked());
        StatementTapSDK.INSTANCE.getEnabledBanks(country, retrieveBalance.isChecked(), (CoreListener<List<Bank>>) (banks, error) -> {
            if (null == banks) {
                String message = (null != error && null != error.getErrorMessage()) ? error.getErrorMessage() : "";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                return;
            }

            banks.sort(Comparator.comparing(bank -> bank.getTitle().toLowerCase()));
            for (Bank bank : banks) {
                bankItems.add(new StatementBankItemViewModel(bank, true));
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

    private void addListeners() {
        findViewById(R.id.autoFill).setOnClickListener(view -> {
            updateAPIKey();
            orgName.setText("Organization");
            externalId.setText("External ID");
            successURL.setText("https://google.com");
            failURL.setText("https://hello.com");
            if(showActionBar.isChecked())
                actionBarText.setText("Statement Tap");
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

        retrieveStatements.setOnCheckedChangeListener((buttonView, isChecked) -> {
            statementRetrievalLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        retrieveBalance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            retrieveBanks();
        });

        datePickerStart.setOnDateChangedListener((datePicker, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            statementRetrievalBuilder.startDate(cal);
        });

        datePickerEnd.setOnDateChangedListener((datePicker, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            statementRetrievalBuilder.endDate(cal);
        });

        showActionBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            actionBarText.setEnabled(isChecked);
        });

        checkout.setOnClickListener(view -> {
            StatementTapRequest.Builder builder = new StatementTapRequest.Builder()
                    .country(country)
                    .externalId(externalId.getText().toString())
                    .successURL(successURL.getText().toString())
                    .failURL(failURL.getText().toString())
                    .organizationName(orgName.getText().toString())
                    .dismissalDialog(
                            new DismissalDialog("Do you want to close the application?",
                                                "Yes", "No")
                    );

            if(retrieveStatements.isChecked())
                builder.statementRetrievalRequest(statementRetrievalBuilder.build());

            builder.bankCodes(getBankCodes());
            builder.setIncludeBalance(retrieveBalance.isChecked());

            StatementTapRequest request = builder.build();

            StatementTapSDK.INSTANCE.initialize(this, apiKey.getText().toString(), null, false, enableLogging.isChecked());
            StatementTapSDK.INSTANCE.checkout(this, request, (CoreListener<String>) (data, error) -> {
                if (null != error) {
                    Toast.makeText(MainActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }, 2000, enableAutoConsent.isChecked(), useRememberMe.isChecked(), showActionBar.isChecked() ? actionBarText.getText().toString() : null);
        });
    }

    private List<BankCode> getBankCodes() {
        ArrayList<BankCode> bankCodes = new ArrayList();
        for (StatementBankItemViewModel item : bankItems) {
            if (item.isSelected())
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

    private void showAccounts(List<Account> accounts) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        StringBuilder stringBuilder = new StringBuilder();

        for(Account account: accounts) {
            stringBuilder.append("Account: "+account.getHolderName()+ " - " +account.getNumber()+": " +
                    account.getBalance().getCurrency().name()+""+(Long.parseLong(
                            account.getBalance().getNumInCents()) / 100));
            stringBuilder.append("\n");
        }

        dialogBuilder.setMessage(stringBuilder.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                });

        dialogBuilder.create().show();
    }
}