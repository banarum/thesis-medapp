package com.koenigmed.luomanager.data.decice

import io.reactivex.Observable

interface BtApi<T, K>: BtEventReceiver {
    fun executeCommand(command: T): Observable<ProgressResponse<K>>
    fun getRssi(): Observable<Int>
}

data class ProgressResponse<T>(val result: T?, val progress: Float) {
    fun isCompleted() = progress>=1
}

