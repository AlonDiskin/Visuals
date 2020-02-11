package com.diskin.alon.visuals.common.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface LiveEvent<T : Any> {

    val event: T?

    fun observe(owner: LifecycleOwner, observer: Observer<in T>)
}