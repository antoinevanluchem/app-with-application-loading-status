package com.udacity.notifications

import android.content.Intent
import android.os.Build
import com.udacity.DownloadStatus

sealed class IntentKey<T>(val name: String)
object Keys {
    object FileName : IntentKey<String>("FILE_NAME")
    object Status : IntentKey<DownloadStatus>("STATUS")
}

//
// Putters
//
fun Intent.putExtra(key: IntentKey<String>, value: String): Intent {
    putExtra(key.name, value)
    return this
}

fun Intent.putExtra(key: IntentKey<DownloadStatus>, value: DownloadStatus): Intent {
    putExtra(key.name, value)
    return this
}

//
// Getters
//
fun Intent.getExtra(key: IntentKey<String>): String? {
    return getStringExtra(key.name)
}

fun Intent.getExtra(key: IntentKey<DownloadStatus>): DownloadStatus? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key.name, DownloadStatus::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key.name) as? DownloadStatus
    }
}