package com.ksnk.radio

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

class TestFragment : Fragment() {
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

companion object
    fun newInstance(): TestFragment {
        return TestFragment()
    }
}