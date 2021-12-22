package com.brankas.testapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.badoualy.stepperindicator.StepperIndicator;
import com.brankas.testapp.Constants;
import com.brankas.testapp.R;
import com.brankas.testapp.TestApplication;
import com.brankas.testapp.adapter.CustomPagerAdapter;
import com.brankas.testapp.fragment.BaseFragment;
import com.brankas.testapp.fragment.ClientDetailsFragment;
import com.brankas.testapp.fragment.SourceAccountFragment;
import com.brankas.testapp.listener.ScreenListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import as.brank.sdk.core.CoreError;
import as.brank.sdk.tap.TapListener;
import as.brank.sdk.tap.statement.StatementTapSDK;
import tap.common.BankCode;
import tap.common.Country;
import tap.statement.StatementTapRequest;

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

public class MainActivity extends FragmentActivity {
    /**
     * Pertains to the field map to be used for checkout
     */
    private HashMap<String, String> map = new HashMap<>();

    /**
     * Pertains to the rotation animation for the progress image
     */
    private RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    /**
     * Constants pertaining to pages or screens of the [viewPager]
     */
    private static final int SOURCE_ACCOUNT_INFO = 0;
    private static final int CLIENT_DETAILS_INFO = 1;

    private boolean isCheckoutClicked = false;

    private final int REQUEST_CODE = 2005;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Provide API KEY
        if(Constants.API_KEY.isEmpty()) {
            showMessage("Please provide API Key inside Constants class!");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }
        else {
            initViewPager();
            addConfirmButtonListener();
            addBackButtonListener();
            addSwitchListener();
            addAutoFillListener();
            TestApplication.getInstance().updateTap(TestApplication.getInstance().isDebug());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showProgress(false);
    }
    /**
     * This function is used to initialize the [viewPager]. It sets the adapter and PageChangeListener
     *
     */
    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager(), new ScreenListener() {
            @Override
            public void onFieldsFilled(boolean isFilled, HashMap<String, String> map, int page) {
                /**
                 * Enables the [confirmButton] only if all of the required fields are filled up and
                 * the current sender fragment is visible
                 */
                if(page == viewPager.getCurrentItem())
                    enableConfirmButton(isFilled);

                for(Map.Entry<String, String> entry: map.entrySet()) {
                    MainActivity.this.map.put(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }

            @Override
            public int describeContents() {
                return 0;
            }
        }));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                findViewById(R.id.back).setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                TextView fillText = findViewById(R.id.fillText);
                switch (position) {
                    case SOURCE_ACCOUNT_INFO:
                        fillText.setText(getString(R.string.enter_source_account_information));
                        break;
                    case CLIENT_DETAILS_INFO:
                        fillText.setText(getString(R.string.enter_pidp_details));
                        break;
                }

                ((Button) findViewById(R.id.confirmButton)).setText(
                        getString(position == CLIENT_DETAILS_INFO ?
                                R.string.checkout : R.string.next));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ((StepperIndicator) findViewById(R.id.stepper)).setViewPager(viewPager);
    }

    private void addConfirmButtonListener() {
        findViewById(R.id.confirmButton).setOnClickListener(v -> {
            int currentItem = ((ViewPager) findViewById(R.id.viewPager)).getCurrentItem();
            if(currentItem == SOURCE_ACCOUNT_INFO)
                showPage(CLIENT_DETAILS_INFO);
            else if(currentItem == CLIENT_DETAILS_INFO) {
                String returnUrl = map.get(ClientDetailsFragment.RETURN_URL);
                String failUrl = map.get(ClientDetailsFragment.FAIL_URL);

                int counter = 2;

                counter = checkWebPattern(returnUrl, ClientDetailsFragment.RETURN_URL, counter);
                counter = checkWebPattern(failUrl, ClientDetailsFragment.FAIL_URL, counter);

                if(counter == 2)
                    checkout();
            }
        });
    }

    private int checkWebPattern(String url, String key, int counter) {
        if(url != null) {
            if(url.isEmpty())
                return counter;
            if(!Patterns.WEB_URL.matcher(url).matches()) {
                showError(key);
                return counter - 1;
            }
        }
        return counter;
    }

    private void showError(String tag) {
        getViewPagerFragment(((ViewPager) findViewById(R.id.viewPager))
                .getCurrentItem()).showError(tag);
    }

    private void addBackButtonListener() {
        findViewById(R.id.back).setOnClickListener(v ->
                showPage(((ViewPager) findViewById(R.id.viewPager)).getCurrentItem() - 1));
    }

    private void enableConfirmButton(boolean isEnabled) {
        findViewById(R.id.confirmButton).setEnabled(isEnabled);
        findViewById(R.id.confirmButton).setBackgroundColor(
                getResources().getColor(isEnabled ? R.color.colorPrimary : R.color.disabledButton));
    }

    private void showPage(int page) {
        ((ViewPager) findViewById(R.id.viewPager)).setCurrentItem(page, true);
    }

    private void checkout() {
        showProgress(true);

        StatementTapRequest.Builder request = new StatementTapRequest.Builder()
            .country(getCountry(map.get(SourceAccountFragment.COUNTRY)))
            .externalId(map.get(ClientDetailsFragment.EXTERNAL_ID))
            .successURL(map.get(ClientDetailsFragment.RETURN_URL))
            .failURL(map.get(ClientDetailsFragment.FAIL_URL))
            .organizationName(map.get(ClientDetailsFragment.DISPLAY_NAME))
            .bankCodes(Arrays.asList(BankCode.BDO_PERSONAL));

        isCheckoutClicked = true;

        StatementTapSDK.INSTANCE.checkout(this, request.build(), new TapListener<String>() {
            @Override
            public void onTapStarted() {
                showProgress(false);
                findViewById(R.id.rootLayout).bringToFront();
                findViewById(R.id.confirmButton).setVisibility(View.GONE);
                findViewById(R.id.rootLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTapEnded() {
                findViewById(R.id.confirmButton).setVisibility(View.VISIBLE);
                findViewById(R.id.rootLayout).setVisibility(View.GONE);
                resetFields();
            }

            @Override
            public void onResult(@Nullable String str, @Nullable CoreError coreError) {
                if(coreError != null) {
                    showProgress(false);
                    showMessage(coreError.getErrorMessage());
                }
                else
                    showMessage("Transaction Successful! Here is the transaction id: "+str);
            }
        }, REQUEST_CODE, false, true);
    }

    private Country getCountry(String country) {
        switch(country) {
            case "Philippines":
                return Country.PH;
            case "Indonesia":
                return Country.ID;
            default:
                return Country.TH;
        }
    }

    private BankCode getBankCode(String bankCode) {
        switch (bankCode) {
            case "BDO":
                return BankCode.BDO_PERSONAL;
            case "BPI":
                return BankCode.BPI_PERSONAL;
            case "MetroBank":
                return BankCode.METROBANK_PERSONAL;
            case "PNB":
                return BankCode.PNB_PERSONAL;
            case "RCBC":
                return BankCode.RCBC_PERSONAL;
            case "Union Bank":
                return BankCode.UNIONBANK_PERSONAL;
            case "BCA":
                return BankCode.BCA_PERSONAL;
            case "BNI":
                return BankCode.BNI_PERSONAL;
            case "BRI":
                return BankCode.BRI_PERSONAL;
            case "Mandiri":
                return BankCode.MANDIRI_PERSONAL;
        }
        return BankCode.DUMMY_BANK_PERSONAL;
    }

    private void addAutoFillListener() {
        findViewById(R.id.imgLogo).setOnClickListener(v ->
                getViewPagerFragment(((ViewPager)findViewById(R.id.viewPager))
                        .getCurrentItem()).autoFill());
    }

    private BaseFragment getViewPagerFragment(int position) {
        return ((CustomPagerAdapter)((ViewPager)findViewById(R.id.viewPager)).getAdapter())
                .getItem(position);
    }

    private void showProgress(boolean isShown) {
        if (isShown) {
            findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
            rotateAnimation.setDuration(900);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            findViewById(R.id.progress).startAnimation(rotateAnimation);
        } else {
            findViewById(R.id.progressLayout).setVisibility(View.GONE);
            findViewById(R.id.progress).clearAnimation();
        }
    }


    private void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(
                android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.getView().setBackgroundResource(R.color.colorPrimary);
        ((TextView)snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(
                ContextCompat.getColor(this, android.R.color.white));
        snackbar.show();
    }

    private void resetFields() {
        if(isCheckoutClicked) {
            map.clear();
            CustomPagerAdapter customPagerAdapter = (CustomPagerAdapter)
                    ((ViewPager)findViewById(R.id.viewPager)).getAdapter();
            customPagerAdapter.getFragments().clear();
            customPagerAdapter.notifyDataSetChanged();

            ((ViewPager)findViewById(R.id.viewPager)).setCurrentItem(SOURCE_ACCOUNT_INFO);
            ((SourceAccountFragment) getViewPagerFragment(SOURCE_ACCOUNT_INFO)).clearFields();
        }

        isCheckoutClicked = false;
    }

    @Override
    public void onBackPressed() {
        if(((ViewPager)findViewById(R.id.viewPager)).getCurrentItem() > 0)
            showPage(((ViewPager)findViewById(R.id.viewPager)).getCurrentItem()- 1);
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                String transactionId = data.getStringExtra(StatementTapSDK.STATEMENT_ID);
                showMessage("Transaction Successful! Here is the transaction id: "+transactionId);
                // Call this to clear the saved credentials within Tap Web Application
                // Call this when you detect that there is a different user
                // TapSDK.clearRememberMe(this@MainActivity)
            }

            else {
                if(data.getStringExtra(StatementTapSDK.ERROR) != null) {
                    showMessage(data.getStringExtra(StatementTapSDK.ERROR)+" - "
                            +data.getStringExtra(StatementTapSDK.ERROR_CODE));
                }
            }
        }
    }

    private void addSwitchListener() {
        ((SwitchCompat) findViewById(R.id.switchEnv)).setOnCheckedChangeListener(
                (buttonView, isChecked) -> TestApplication.getInstance().updateTap(!isChecked));
    }
}