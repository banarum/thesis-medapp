package com.koenigmed.luomanager.toothpick.module

import com.google.gson.Gson
import com.koenigmed.luomanager.data.repository.auth.AuthRepository
import com.koenigmed.luomanager.data.repository.auth.IAuthRepository
import com.koenigmed.luomanager.data.repository.profile.IUserRepository
import com.koenigmed.luomanager.data.repository.profile.UserRepository
import com.koenigmed.luomanager.data.repository.program.IProgramRepository
import com.koenigmed.luomanager.data.repository.program.ProgramRepository
import com.koenigmed.luomanager.data.server.MyoApi
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.domain.interactor.profile.ProfileInteractor
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.domain.interactor.program.ReceiptInteractor
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.toothpick.provider.ApiProvider
import com.koenigmed.luomanager.toothpick.provider.GsonProvider
import com.koenigmed.luomanager.toothpick.provider.OkHttpClientProvider
import okhttp3.OkHttpClient
import org.xml.sax.ErrorHandler
import toothpick.config.Module

class ServerModule : Module() {
    init {
        //Network
        bind(Gson::class.java).toProvider(GsonProvider::class.java).providesSingletonInScope()
        bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java).providesSingletonInScope()
        bind(MyoApi::class.java).toProvider(ApiProvider::class.java).providesSingletonInScope()

        bind(UserRepository::class.java).singletonInScope()

        //Error handler with logout logic
        bind(ErrorHandler::class.java).singletonInScope()

        //Auth
        bind(IAuthRepository::class.java).to(AuthRepository::class.java).singletonInScope()
        bind(AuthInteractor::class.java).singletonInScope()

        //Program
        bind(IProgramRepository::class.java).to(ProgramRepository::class.java).singletonInScope()
        bind(ProgramInteractor::class.java).singletonInScope()
        bind(ReceiptInteractor::class.java).singletonInScope()
        bind(BtInteractor::class.java).singletonInScope()

        //Device
        bind(DeviceInteractor::class.java).singletonInScope()

        //User
        bind(IUserRepository::class.java).to(UserRepository::class.java).singletonInScope()
        bind(ProfileInteractor::class.java).singletonInScope()

    }
}