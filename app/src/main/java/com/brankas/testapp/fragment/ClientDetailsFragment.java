package com.brankas.testapp.fragment;

import android.os.Bundle;

import com.brankas.testapp.R;
import com.brankas.testapp.customview.BoxedEditText;
import com.brankas.testapp.listener.ScreenListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClientDetailsFragment extends BaseFragment {

   public static final String DISPLAY_NAME = "display_name";
   public static final String LOGO_URL = "logo_url";
   public static final String RETURN_URL = "return_url";
   public static final String FAIL_URL = "fail_url";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_client_details;
    }

    @Override
    public void initDetails() { }

    @Override
    public void showError(String tag) {
        if(tag != null) {
            switch (tag) {
                case LOGO_URL:
                    ((BoxedEditText)parentView.findViewById(R.id.logoUrl))
                            .showError(getString(R.string.invalid_url_format));
                    break;
                case RETURN_URL:
                    ((BoxedEditText)parentView.findViewById(R.id.returnUrl))
                            .showError(getString(R.string.invalid_url_format));
                    break;
                case FAIL_URL:
                    ((BoxedEditText)parentView.findViewById(R.id.failUrl))
                            .showError(getString(R.string.invalid_url_format));
                    break;
            }
        }
    }

    @Override
    public void autoFill() {
        ((BoxedEditText)parentView.findViewById(R.id.displayName)).setText("Display Name");
        ((BoxedEditText)parentView.findViewById(R.id.returnUrl)).setText("https://google.com.ph");
        ((BoxedEditText)parentView.findViewById(R.id.failUrl)).setText("https://hello.com");
    }

    @Override
    public HashMap<String, Object> getFieldsMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DISPLAY_NAME, parentView.findViewById(R.id.displayName));
        hashMap.put(LOGO_URL, parentView.findViewById(R.id.logoUrl));
        hashMap.put(RETURN_URL, parentView.findViewById(R.id.returnUrl));
        hashMap.put(FAIL_URL, parentView.findViewById(R.id.failUrl));
        return hashMap;
    }

    @Override
    public List<String> getOptionalFields() {
        return new ArrayList(Arrays.asList(DISPLAY_NAME, LOGO_URL, RETURN_URL, FAIL_URL));
    }

    @Override
    public int getPage() {
        return 2;
    }

    public static ClientDetailsFragment newInstance(ScreenListener screenListener) {
        ClientDetailsFragment clientDetailsFragment = new ClientDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LISTENER, screenListener);
        clientDetailsFragment.setArguments(bundle);
        return clientDetailsFragment;
    }
}