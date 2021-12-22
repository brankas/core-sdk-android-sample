package com.brankas.testapp.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.brankas.testapp.customview.BoxedEditText;
import com.brankas.testapp.listener.ScreenListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseFragment extends Fragment {

    public abstract void showError(String tag);
    public abstract int getLayoutId();
    public abstract void initDetails();
    public abstract void autoFill();
    public abstract HashMap<String, Object> getFieldsMap();
    public abstract List<String> getOptionalFields();
    public abstract int getPage();

    protected ScreenListener screenListener;
    protected int fieldCount = 0;
    protected HashMap<String, String> map = new HashMap<>();
    protected View parentView;

    public static String LISTENER = "LISTENER";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        screenListener.onFieldsFilled(map.size() == fieldCount
                || fieldCount == 0, map, getPage());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        screenListener = getArguments().getParcelable(LISTENER);
        return parentView = inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDetails();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        for(Map.Entry<String, Object> entry : getFieldsMap().entrySet()) {
           if(entry.getValue() instanceof BoxedEditText)
               outState.putString(entry.getKey(), ((BoxedEditText) entry.getValue()).getText());
           else if(entry.getValue() instanceof AppCompatSpinner)
               outState.putInt(entry.getKey(), ((AppCompatSpinner)
                       entry.getValue()).getSelectedItemPosition());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fieldCount = 0;
        map.clear();
        for(Map.Entry<String, Object> entry : getFieldsMap().entrySet()) {
            boolean isOptional = getOptionalFields().contains(entry.getKey());
            if(!isOptional)
                ++fieldCount;

            if(entry.getValue() instanceof BoxedEditText) {
                ((BoxedEditText) entry.getValue()).addTextWatcher(getTextWatcher(
                        entry.getKey(), isOptional));
                if(savedInstanceState != null)
                    updateText(savedInstanceState, entry.getKey(),
                            (BoxedEditText) entry.getValue());
            }

            else if(entry.getValue() instanceof AppCompatSpinner) {
                if(savedInstanceState != null)
                    ((AppCompatSpinner) entry.getValue()).setSelection(
                            savedInstanceState.getInt(entry.getKey()));
            }
        }
    }

    protected TextWatcher getTextWatcher(String tag, boolean optional) {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (screenListener != null) {
                    if(fieldCount == 0) {
                        map.put(tag, s.toString());
                        screenListener.onFieldsFilled(true, map, getPage());
                    }
                    else {
                        if (optional && !s.toString().isEmpty())
                            map.put(tag, s.toString());
                        else {
                            if (!s.toString().isEmpty()) {
                                map.put(tag, s.toString());
                            } else {
                                if (map.containsKey(tag))
                                    map.remove(tag);
                            }
                        }
                        screenListener.onFieldsFilled(map.size() == fieldCount, map, getPage());
                    }
                }
            }
        };
    }

    public void clearFields() {
        for(Map.Entry<String, Object> entry : getFieldsMap().entrySet()) {
            if (entry.getValue() instanceof BoxedEditText)
                ((BoxedEditText) entry.getValue()).setText("");
        }
    }

    private void updateText(Bundle bundle, String key, BoxedEditText boxedEditText) {
        if(bundle.getString(key) != null) {
            boxedEditText.setText(bundle.getString(key));
        }
    }

    private class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}