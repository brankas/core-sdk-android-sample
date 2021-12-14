package com.brankas.testapp.fragment;

import android.os.Bundle;

import com.brankas.testapp.R;
import com.brankas.testapp.TestApplication;
import com.brankas.testapp.customview.BoxedEditText;
import com.brankas.testapp.listener.ScreenListener;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransferDetailsFragment extends BaseFragment {

    public static int MAX_DESTINATION_ACCOUNT_ID = 36;
    public static String DESTINATION_ACCOUNT_ID = "destination_account_id";
    public static String MEMO = "memo";
    public static String AMOUNT = "amount";
    public static String REFERENCE_ID = "reference_id";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer_details;
    }

    @Override
    public void initDetails() {
        initDestinationAccountIdMaxLength();
    }

    @Override
    public void showError(String tag) {
        if(tag != null)
            ((BoxedEditText) parentView.findViewById(R.id.destinationAccountId)).showError(
                    getString(R.string.invalid_destination_account));
        else
            ((BoxedEditText) parentView.findViewById(R.id.destinationAccountId)).hideError();
    }

    @Override
    public void autoFill() {
        ((BoxedEditText) parentView.findViewById(R.id.destinationAccountId)).setText(
                TestApplication.getInstance().getDestinationAccountId());
        ((BoxedEditText) parentView.findViewById(R.id.memo)).setText("Bank Transfer");
        ((BoxedEditText) parentView.findViewById(R.id.amount)).setText("100");
        ((BoxedEditText) parentView.findViewById(R.id.referenceId)).setText("Sample Reference 1975");
    }

    @Override
    public HashMap<String, Object> getFieldsMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DESTINATION_ACCOUNT_ID, parentView.findViewById(R.id.destinationAccountId));
        hashMap.put(MEMO, parentView.findViewById(R.id.memo));
        hashMap.put(AMOUNT, parentView.findViewById(R.id.amount));
        hashMap.put(REFERENCE_ID, parentView.findViewById(R.id.referenceId));
        return hashMap;
    }

    @Override
    public List<String> getOptionalFields() {
        return new ArrayList<>();
    }

    @Override
    public int getPage() {
        return 1;
    }

    private void initDestinationAccountIdMaxLength() {
        ((BoxedEditText) parentView.findViewById(R.id.destinationAccountId))
                .updateMaxLength(MAX_DESTINATION_ACCOUNT_ID);
    }

    public void addAmountPrefix(String currency) {
        ((BoxedEditText) parentView.findViewById(R.id.amount)).showPrefix(currency);
    }

    public static TransferDetailsFragment newInstance(ScreenListener screenListener) {
        TransferDetailsFragment transferDetailsFragment = new TransferDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LISTENER, screenListener);
        transferDetailsFragment.setArguments(bundle);
        return transferDetailsFragment;
    }
}