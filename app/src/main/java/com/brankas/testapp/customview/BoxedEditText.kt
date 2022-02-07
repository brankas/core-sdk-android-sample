package com.brankas.testapp.customview

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import com.brankas.testapp.R

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

/**
 * Custom Layout for [EditText] with a Boxed Background
 *
 */
class BoxedEditText: LinearLayout {
    private var textWatcher: TextWatcher? = null
    private lateinit var editText: EditText
    private lateinit var errorText: AppCompatTextView
    private lateinit var prefix: AppCompatTextView
    private lateinit var border: RelativeLayout

    constructor(context: Context?) : super(context) {
        initViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initViews(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initViews(attrs)
    }

    private fun initViews(attrs: AttributeSet? = null) {
        inflate(context, R.layout.layout_boxed_edittext, this)

        editText = findViewById(R.id.editText)
        errorText = findViewById(R.id.errorText)
        prefix = findViewById(R.id.prefix)
        border = findViewById(R.id.border)

        /**
         * Obtains the custom attributes from XML
         */
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.BoxedEditText)

        /**
         * Adds hint from XML
         */
        val hint = typedArray.getString(R.styleable.BoxedEditText_hint)
        hint?.let {
            setHint(it)
        }

        /**
         * Converts and adds the [InputType] from XML
         */
        val inputType = typedArray.getInt(R.styleable.BoxedEditText_inputType, 0)
        if (inputType in 1..5) {
            var input = InputType.TYPE_CLASS_TEXT
            when (inputType) {
                2 -> input = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                3 -> input = InputType.TYPE_CLASS_PHONE
                4 -> input = InputType.TYPE_CLASS_NUMBER
                5 -> input = InputType.TYPE_TEXT_VARIATION_URI
            }
            setInputType(input)
        }

        /**
         * Checks if prefix has been added in the XML
         */
        val prefix = typedArray.getString(R.styleable.BoxedEditText_prefix)
        prefix?.let {
            showPrefix(it)
        }
        typedArray.recycle()
    }

    /**
     * Sets the input type of the [EditText]
     *
     * @param inputType
     */
    fun setInputType(inputType: Int) {
        editText.inputType = inputType
    }

    /**
     * Adds a custom touch listener to the EditText
     *
     * @param onTouchListener
     */
    fun setTouchListener(onTouchListener: OnTouchListener?) {
        editText.setOnTouchListener(onTouchListener)
    }

    /**
     * Retrieves the EditText
     *
     * @return [EditText]
     */
    fun getEditText(): EditText {
        return editText
    }

    /**
     * Updates the text of EditText
     *
     * @param text
     */
    fun setText(text: String) {
        editText.setText(text)
    }

    /**
     * Shows the error message via a label below the EditText
     *
     * @param errorMessage Message to be shown
     */
    fun showError(errorMessage: String?) {
        errorMessage?.let {
            errorText.visibility = View.VISIBLE
            errorText.text = it
        }
        border.setBackgroundColor(context.resources.getColor(R.color.errorTextColor))
    }

    /**
     * Hides the error
     *
     */
    fun hideError() {
        errorText.visibility = View.GONE
    }

    /**
     * Updates the maximum number of characters that the EditText can accommodate
     *
     * @param length
     */
    fun updateMaxLength(length: Int) {
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
    }

    /**
     * Changes the hint in the EditText
     *
     * @param hint
     */
    fun setHint(hint: String) {
        editText.hint = hint
    }

    /**
     * Returns the text currently seen on the EditText
     *
     * @return
     */
    fun getText(): String {
        return editText.text.toString()
    }

    /**
     * Adds a custom [TextWatcher] that will listen to inputs to the EditText
     *
     * @param textWatcher
     */
    fun addTextWatcher(textWatcher: TextWatcher) {
        editText.addTextChangedListener(textWatcher)
    }

    /**
     * Removes the [TextWatcher]
     *
     */
    fun removeTextWatcher() {
        textWatcher?.let {
            editText.removeTextChangedListener(it)
        }
    }

    /**
     * Shows the uneditable prefix beside the EditText
     *
     * @param prefixStr Prefix to be shown
     */
    fun showPrefix(prefixStr: String) {
        prefix.visibility = View.VISIBLE
        prefix.text = prefixStr
    }
}