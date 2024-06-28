package com.udacity.notifications

import android.content.Intent

sealed class IntentKey<T>(val name: String)
object Keys {
    object FileName : IntentKey<String>("FILE_NAME")
    object Status : IntentKey<String>("STATUS")
}

fun Intent.putExtra(key: IntentKey<String>, value: String): Intent {
    putExtra(key.name, value)
    return this
}

fun Intent.getExtra(key: IntentKey<String>): String? {
    return getStringExtra(key.name)
}