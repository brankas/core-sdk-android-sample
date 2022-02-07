package com.brankas.testapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.customview.BoxedEditText

abstract class BaseFragment : Fragment() {

    abstract fun showError(tag: String?)
    abstract fun getLayoutId(): Int
    abstract fun initDetails()
    abstract fun autoFill()
    abstract fun getFieldsMap(): HashMap<String, Any>
    abstract fun getOptionalFields(): List<String>
    abstract fun getPage(): Int

    lateinit var screenListener: ScreenListener

    protected var fieldCount = 0
    protected val map = hashMapOf<String, String>()

    protected lateinit var parentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        screenListener.onFieldsFilled(map.size == fieldCount
                || fieldCount == 0, map, getPage())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        screenListener = arguments?.getParcelable<ScreenListener>(LISTENER) as ScreenListener
        parentLayout =  inflater.inflate(getLayoutId(), container, false)
        return parentLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDetails()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getFieldsMap().entries.forEach { entry ->
            (entry.value as? BoxedEditText)?.let {
                outState.putString(entry.key, it.getText())
            }
            (entry.value as? AppCompatSpinner)?.let {
                outState.putInt(entry.key, it.selectedItemPosition)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fieldCount = 0
        map.clear()
        getFieldsMap().entries.forEach { entry ->
            val isOptional = getOptionalFields().contains(entry.key)
            if(!isOptional)
                ++fieldCount

            (entry.value as? BoxedEditText)?.let { boxedEditText ->
                boxedEditText.addTextWatcher(getTextWatcher(entry.key, isOptional))
                savedInstanceState?.let {
                    updateText(it, entry.key, boxedEditText)
                }
            }
            (entry.value as? AppCompatSpinner)?.let { spinner ->
                savedInstanceState?.let {
                    spinner.setSelection(it.getInt(entry.key))
                }
            }
        }
    }

    protected fun getTextWatcher(tag: String, optional: Boolean = false): TextWatcher {
        return object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                if (this@BaseFragment::screenListener.isInitialized) {
                    if(fieldCount == 0) {
                        map[tag] = s.toString()
                        screenListener.onFieldsFilled(true, map, getPage())
                    }
                    else {
                        if (optional && s.toString().isNotEmpty()) {
                            map[tag] = s.toString()
                        }
                        else {
                            if (s.toString().isNotEmpty()) {
                                map[tag] = s.toString()
                            } else {
                                if (map.containsKey(tag))
                                    map.remove(tag)
                            }
                        }
                        screenListener.onFieldsFilled(map.size == fieldCount, map, getPage())
                    }
                }
            }
        }
    }

    fun clearFields() {
        getFieldsMap().entries.forEach {
            if (it.value.javaClass == BoxedEditText::class) {
                (it.value as BoxedEditText).setText("")
            }
        }
    }

    private fun updateText(bundle: Bundle, key: String, boxedEditText: BoxedEditText) {
        bundle.getString(key)?.let {
            boxedEditText.setText(it)
        }
    }

    open class TextWatcherAdapter : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        const val LISTENER = "LISTENER"
        inline fun <reified V : BaseFragment> newInstance(screenListener: ScreenListener): V {
            val fragment = V::class.java.newInstance()
            val bundle = Bundle()
            bundle.putParcelable(LISTENER, screenListener)
            fragment.arguments = bundle
            return fragment
        }
    }
}