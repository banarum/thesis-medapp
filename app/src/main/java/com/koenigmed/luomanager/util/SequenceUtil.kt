package com.koenigmed.luomanager.util

import java.util.concurrent.atomic.AtomicInteger

object SequenceUtil {
    private val seed = AtomicInteger()

    val nextInt: Int
        get() = seed.incrementAndGet()
}
