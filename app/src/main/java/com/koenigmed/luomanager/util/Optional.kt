package com.koenigmed.luomanager.util

class Optional<T> private constructor(private val value: T?) {

    val isPresent: Boolean
        get() = value != null

    fun get(): T? {
        return value
    }

    companion object {

        fun <T> of(value: T?): Optional<T> {
            return if (value == null) {
                Optional.empty()
            } else Optional(value)

        }

        fun <T> empty(): Optional<T> {
            return Optional(null)
        }
    }
}