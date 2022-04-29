package com.ksnk.radio.di.modules

import androidx.lifecycle.ViewModel
import com.ksnk.radio.di.keys.ViewModelKey
import com.ksnk.radio.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}