package com.koenigmed.luomanager

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.toothpick.module.AppModule
import com.koenigmed.luomanager.toothpick.module.ServerModule
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, Crashlytics())
        }

        initLogger()
        initToothpick()
        initAppScope()
        initThreetenABP()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            Toothpick.setConfiguration(Configuration.forProduction().disableReflection())
            FactoryRegistryLocator.setRootRegistry(com.koenigmed.luomanager.FactoryRegistry())
            MemberInjectorRegistryLocator.setRootRegistry(com.koenigmed.luomanager.MemberInjectorRegistry())
        }
    }

    private fun initAppScope() {
        val appScope = Toothpick.openScope(DI.APP_SCOPE)
        appScope.installModules(AppModule(this))

        //By default we need init ServerScope for launch app
        val serverScope = Toothpick.openScopes(DI.APP_SCOPE, DI.SERVER_SCOPE)
        serverScope.installModules(ServerModule())
    }

    private fun initThreetenABP() {
        AndroidThreeTen.init(this)
    }

}
