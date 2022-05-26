package com.ksnk.radio.ui.settingFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.ksnk.radio.R
import com.ksnk.radio.enums.DisplayListType
import com.ksnk.radio.helper.PreferenceHelper
import com.ksnk.radio.ui.main.MainActivity
import com.ksnk.radio.ui.main.MainViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class SettingFragment : Fragment() {
    private lateinit var displayTypeRadioGroup: RadioGroup

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var preferencesHelper: PreferenceHelper
    private lateinit var updateLottieAnimView: LottieAnimationView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLottieAnimView = view.findViewById(R.id.lottieAnimUpdateDb)
        displayTypeRadioGroup = view.findViewById(R.id.displayTypeRadioGroup)
        val displayListType = preferencesHelper.getDisplayListType()
        if (displayListType == DisplayListType.List) {
            displayTypeRadioGroup.check(R.id.listRadioButton)
        } else {
            displayTypeRadioGroup.check(R.id.gridRadioButton)
        }

        displayTypeRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.listRadioButton -> {
                    preferencesHelper.setDisplayListType(DisplayListType.List)
                }
                R.id.gridRadioButton -> {
                    preferencesHelper.setDisplayListType(DisplayListType.Grid)
                }
            }
        }


        updateLottieAnimView.setOnClickListener {
            updateLottieAnimView.playAnimation()
            (activity as MainActivity?)?.updateDb()
        }
    }

    companion object

    fun newInstance(): SettingFragment {
        return SettingFragment()
    }
}