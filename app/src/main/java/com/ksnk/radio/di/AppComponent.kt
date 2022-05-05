package com.ksnk.radio.di

import android.app.Application
import com.ksnk.radio.helper.PreferenceHelper
import com.ksnk.radio.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModules::class,
        FragmentBuildersModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        ServiceBuilderModule::class]
)

interface AppComponent : AndroidInjector<App?> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application?): Builder?
        fun build(): AppComponent?
    }

    override fun inject(app: App?)

    fun preferenceHelper(): PreferenceHelper
}