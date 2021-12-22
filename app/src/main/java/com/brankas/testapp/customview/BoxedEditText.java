package com.brankas.testapp.customview;


/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.brankas.testapp.R;

/**
 * Custom Layout for [EditText] with a Boxed Background
 *
 */
public class BoxedEditText extends LinearLayout {
    private TextWatcher textWatcher = null;

    public BoxedEditText(Context context) {
        super(context);
        initViews(null);
    }

    public BoxedEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public BoxedEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    public BoxedEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_boxed_edittext, this);

        /**
         * Obtains the custom attributes from XML
         */
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BoxedEditText);

        /**
         * Adds hint from XML
         */
        String hint = typedArray.getString(R.styleable.BoxedEditText_hint);

        if(hint != null)
            setHint(hint);

        /**
         * Converts and adds the [InputType] from XML
         */
        int inputType = typedArray.getInt(R.styleable.BoxedEditText_inputType, 0);
        if (inputType >= 1 && inputType <=5) {
            int input = InputType.TYPE_CLASS_TEXT;
            switch (inputType) {
                case 2:
                    input = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                    break;
                case 3:
                    input = InputType.TYPE_CLASS_PHONE;
                    break;
                case 4:
                    input = InputType.TYPE_CLASS_NUMBER;
                    break;
                case 5:
                    input = InputType.TYPE_TEXT_VARIATION_URI;
                    break;
            }
            setInputType(input);
        }

        /**
         * Checks if prefix has been added in the XML
         */
        String prefix = typedArray.getString(R.styleable.BoxedEditText_prefix);

        if(prefix != null)
            showPrefix(prefix);

        typedArray.recycle();
    }

    /**
     * Sets the input type of the [EditText]
     *
     * @param inputType
     */
    public void setInputType(int inputType) {
        ((EditText) findViewById(R.id.editText)).setInputType(inputType);
    }

    /**
     * Adds a custom touch listener to the EditText
     *
     * @param onTouchListener
     */
    public void setTouchListener(OnTouchListener onTouchListener) {
        findViewById(R.id.editText).setOnTouchListener(onTouchListener);
    }

    /**
     * Retrieves the EditText
     *
     * @return [EditText]
     */
    public EditText getEditText() {
        return findViewById(R.id.editText);
    }

    /**
     * Updates the text of EditText
     *
     * @param text
     */
    public void setText(String text) {
        ((EditText) findViewById(R.id.editText)).setText(text);
    }

    /**
     * Shows the error message via a label below the EditText
     *
     * @param errorMessage Message to be shown
     */
    public void showError(String errorMessage) {
        if(errorMessage != null) {
            findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.errorText)).setText(errorMessage);
        }
       findViewById(R.id.border).setBackgroundColor(getContext().getResources().getColor(
               R.color.errorTextColor));
    }

    /**
     * Hides the error
     *
     */
    public void hideError() {
        findViewById(R.id.errorText).setVisibility(View.GONE);
    }

    /**
     * Updates the maximum number of characters that the EditText can accommodate
     *
     * @param length
     */
    public void updateMaxLength(int length) {
        ((EditText) findViewById(R.id.editText)).setFilters(new InputFilter[] {new
                InputFilter.LengthFilter(length)});
    }

    /**
     * Changes the hint in the EditText
     *
     * @param hint
     */
    public void setHint(String hint) {
        ((EditText) findViewById(R.id.editText)).setHint(hint);
    }

    /**
     * Returns the text currently seen on the EditText
     *
     * @return
     */
    public String getText() {
        return ((EditText) findViewById(R.id.editText)).getText().toString();
    }

    /**
     * Adds a custom [TextWatcher] that will listen to inputs to the EditText
     *
     * @param textWatcher
     */
    public void addTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
        ((EditText) findViewById(R.id.editText)).addTextChangedListener(textWatcher);
    }

    /**
     * Removes the [TextWatcher]
     *
     */
    public void removeTextWatcher() {
        if(textWatcher != null)
            ((EditText) findViewById(R.id.editText)).removeTextChangedListener(textWatcher);
    }

    /**
     * Shows the uneditable prefix beside the EditText
     *
     * @param prefixStr Prefix to be shown
     */
    public void showPrefix(String prefixStr) {
        findViewById(R.id.prefix).setVisibility(View.VISIBLE);
        ((AppCompatTextView) findViewById(R.id.prefix)).setText(prefixStr);
    }
}