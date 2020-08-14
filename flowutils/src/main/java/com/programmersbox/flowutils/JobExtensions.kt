package com.programmersbox.flowutils

import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A delegate to cancel the current job upon setting a new one
 */
class JobReset : ReadWriteProperty<Any?, Job?> {
    private val job: AtomicReference<Job?> = AtomicReference(null)
    override fun getValue(thisRef: Any?, property: KProperty<*>): Job? = job.get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Job?) = job.getAndSet(value)?.cancel().let { Unit }
}
