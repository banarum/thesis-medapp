package com.koenigmed.luomanager.toothpick.module

import android.content.Context
import android.content.res.AssetManager
import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.data.repository.prefs.PrefsRepository
import com.koenigmed.luomanager.data.room.AppDatabase
import com.koenigmed.luomanager.toothpick.provider.db.AppDatabaseProvider
import com.koenigmed.luomanager.toothpick.provider.db.ProgramDaoProvider
import com.koenigmed.luomanager.toothpick.provider.db.UserDaoProvider
import com.koenigmed.luomanager.data.room.dao.ProgramDao
import com.koenigmed.luomanager.data.room.dao.UserDao
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.system.AppSchedulers
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.ResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.toothpick.PrimitiveWrapper
import com.koenigmed.luomanager.toothpick.qualifier.DefaultPageSize
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module

class AppModule(context: Context) : Module() {
    init {
        //Global
        bind(Context::class.java).toInstance(context)
        bind(PrimitiveWrapper::class.java).withName(DefaultPageSize::class.java).toInstance(PrimitiveWrapper(20))
        bind(SchedulersProvider::class.java).toInstance(AppSchedulers())
        bind(IResourceManager::class.java).to(ResourceManager::class.java).singletonInScope()
        bind(AssetManager::class.java).toInstance(context.assets)

        //Navigation
        val cicerone = Cicerone.create(FlowRouter())
        bind(Router::class.java).toInstance(cicerone.router)
        bind(FlowRouter::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        //Prefs
        bind(IPrefsRepository::class.java).to(PrefsRepository::class.java).singletonInScope()
        bind(AppDatabase::class.java).toProvider(AppDatabaseProvider::class.java)
        bind(ProgramDao::class.java).toProvider(ProgramDaoProvider::class.java)
        bind(UserDao::class.java).toProvider(UserDaoProvider::class.java)

    }
}