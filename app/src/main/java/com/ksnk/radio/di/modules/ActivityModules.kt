package com.ksnk.radio.di.modules

import com.ksnk.radio.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModules {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity?
}