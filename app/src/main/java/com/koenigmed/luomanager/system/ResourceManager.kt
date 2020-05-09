package com.koenigmed.luomanager.system

import android.content.Context
import android.support.annotation.IntegerRes
import javax.inject.Inject

class ResourceManager @Inject constructor(private val context: Context) : IResourceManager {
    override fun getStringArray(id: Int) = context.resources.getStringArray(id)

    override fun getString(id: Int) = context.getString(id)

    override fun getInteger(@IntegerRes resourceId: Int) = context.resources.getInteger(resourceId)

    override fun getContext() = context
}