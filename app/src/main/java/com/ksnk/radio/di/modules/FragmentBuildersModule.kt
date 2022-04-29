package com.ksnk.radio.di.modules

import com.ksnk.radio.TestFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun testFragment(): TestFragment?
}