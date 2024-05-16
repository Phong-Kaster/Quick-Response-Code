package com.example.quickresponsecode.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


    fun<T> LifecycleOwner.collectOnLifecycle(flow: Flow<T>, state: Lifecycle.State, action: suspend (value: T) -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(state) {
                flow.collect(action)
            }
        }
    }

    fun<T> LifecycleOwner.collectLatestOnLifecycle(flow: Flow<T>, state: Lifecycle.State, action: suspend (value: T) -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(state) {
                flow.collectLatest(action)
            }
        }
    }

    fun<T> LifecycleOwner.collectOnResume(flow: Flow<T>, action: suspend (value: T) -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                flow.collect(action)
            }
        }
    }

    fun<T> LifecycleOwner.collectLatestOnResume(flow: Flow<T>, action: suspend (value: T) -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                flow.collectLatest(action)
            }
        }
    }

    /** Collect a flow and cancel if action return true */
    fun<T> LifecycleOwner.collectOnce(flow: Flow<T>, action: suspend (value: T) -> Boolean) {
        var job: Job? = null
        job = lifecycleScope.launch {
            flow.collectLatest {
                val result = action(it)
                if (result) job?.cancel()
            }
        }
    }
