package com.ksnk.radio.di.modules

import com.ksnk.radio.ui.favoriteFragment.FavoriteFragment
import com.ksnk.radio.ui.historyFragment.HistoryFragment
import com.ksnk.radio.ui.listFragment.ListFragment
import com.ksnk.radio.ui.settingFragment.SettingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun listFragment(): ListFragment?

    @ContributesAndroidInjector
    abstract fun playerFragment(): SettingFragment?

    @ContributesAndroidInjector
    abstract fun favoriteFragment(): FavoriteFragment?

    @ContributesAndroidInjector
    abstract fun historyFragment():HistoryFragment?
}