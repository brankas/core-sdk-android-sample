package com.brankas.testapp.adapter

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.brankas.testapp.`interface`.ScreenListener
import com.brankas.testapp.fragment.BaseFragment
import com.brankas.testapp.fragment.ClientDetailsFragment
import com.brankas.testapp.fragment.SourceAccountFragment

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

/**
 * Custom [ViewPager] Adapter that shows all the forms to be filled up
 *
 * @property screenListener Refers to the interface that will serve as the communication between the [BaseFragment] and the MainActivity
 * @constructor
 *
 *
 * @param fragmentManager
 */
class CustomPagerAdapter(fragmentManager: FragmentManager, private val screenListener: ScreenListener) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val numPages = 2

    val fragments = hashMapOf<Int, BaseFragment?>()

    override fun getCount(): Int = numPages

    override fun getItem(position: Int): BaseFragment {
        /**
         * Instantiates the fragments if necessary
         */
        if(!fragments.containsKey(position)) {
            fragments[position] = when (position) {
                0 -> SourceAccountFragment.newInstance(screenListener)
                1 -> ClientDetailsFragment.newInstance(screenListener)
                else -> null
            }
        }
        return fragments[position]!!
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        if(fragments.containsKey(position))
            fragments.remove(position)
    }
}