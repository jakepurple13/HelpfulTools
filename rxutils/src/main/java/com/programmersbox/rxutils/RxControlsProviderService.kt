package com.programmersbox.rxutils

import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import androidx.annotation.RequiresApi
import io.reactivex.processors.ReplayProcessor
import org.reactivestreams.FlowAdapters
import org.reactivestreams.Publisher
import java.util.concurrent.Flow

@RequiresApi(Build.VERSION_CODES.R)
abstract class RxControlsProviderService : ControlsProviderService() {
    private lateinit var updatePublisher: ReplayProcessor<Control>
    abstract fun createPublisherForAllAvailableControls(): Publisher<Control>
    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> = FlowAdapters.toFlowPublisher(createPublisherForAllAvailableControls())
    abstract fun createPublisherForIds(publisher: Publisher<Control>, controlIds: MutableList<String>)
    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        updatePublisher = ReplayProcessor.create()
        createPublisherForIds(updatePublisher, controlIds)
        return FlowAdapters.toFlowPublisher(updatePublisher)
    }

    protected fun publish(control: Control) = if (::updatePublisher.isInitialized) updatePublisher.onNext(control) else Unit
}