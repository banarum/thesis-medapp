package com.koenigmed.luomanager.system

import android.content.Context
import android.support.annotation.ArrayRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes

interface IResourceManager {

    fun getStringArray(@ArrayRes id: Int): Array<String>
    fun getString(@StringRes id: Int): String
    fun getInteger(@IntegerRes resourceId: Int): Int
    fun getContext(): Context
}