package com.koenigmed.luomanager.toothpick.module

import com.koenigmed.luomanager.data.mapper.user.UserMapper
import com.koenigmed.luomanager.presentation.global.GlobalMenuController
import toothpick.config.Module

class MainActivityModule : Module() {
    init {
        bind(GlobalMenuController::class.java).toInstance(GlobalMenuController())

        //Mappers
        bind(UserMapper::class.java).singletonInScope()
    }
}