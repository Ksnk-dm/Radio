package com.ksnk.radio.di.modules

import com.ksnk.radio.ui.listFragment.ListFragment
import com.ksnk.radio.ui.playerFragment.PlayerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun firstFragment(): ListFragment?

    @ContributesAndroidInjector
    abstract fun secondFragment(): PlayerFragment?
}