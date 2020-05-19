package com.koenigmed.luomanager.data.decice

import io.reactivex.Observable

interface BtApi<T, K>: BtEventReceiver {
    fun executeCommand(command: T, chained: Boolean=false): Observable<ProgressResponse<K>>
}

data class ProgressResponse<T>(val result: T?, val progress: Float) {
    fun isCompleted() = progress>=1
}

