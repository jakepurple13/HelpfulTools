package com.programmersbox.flowutils

import android.os.Environment
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
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

abstract class CoroutineTask<Progress, Result> : CoroutineScope {
    protected open val job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    /**
     * Publishes the current progress
     * calls [onProgressUpdate] on the UI thread
     */
    protected suspend fun publishProgress(vararg values: Progress) = withContext(Dispatchers.Main) { onProgressUpdate(values) }
    open fun onProgressUpdate(values: Array<out Progress>) {}
    abstract suspend fun doInBackground(): Result
    open fun onPreExecute() {}
    open fun onPostExecute(result: Result) {}
    fun cancel() = job.cancel()
    fun execute() = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) { doInBackground() } // runs in background thread without blocking the Main Thread
        onPostExecute(result)
    }

}

/**
 * DownloadUrl
 *
 * A way to download a file from a url while getting the download percentage
 *
 * @param downloadUrl The url to download
 * @param outputFolder the folder to place the downloaded file in
 * @param outputName the name of the file
 */
@Suppress("BlockingMethodInNonBlockingContext")
abstract class DownloadUrl(
    private val downloadUrl: String,
    private val outputFolder: String = Environment.getExternalStorageDirectory().toString() + "/Download/",
    private val outputName: String
) : CoroutineTask<Int, Boolean>() {

    abstract override fun onPreExecute()
    abstract override fun onPostExecute(result: Boolean)
    abstract override fun onProgressUpdate(values: Array<out Int>)

    /**
     * Modify the connection by adding headers or anything else
     */
    open fun HttpURLConnection.headers() {}

    override suspend fun doInBackground(): Boolean { // to run code in Background Thread
        // do async work
        var flag = false

        try {
            val url = URL(downloadUrl)
            val c = url.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.headers()
            c.connect()
            val file = File(outputFolder)
            file.mkdirs()
            val outputFile = File(file, outputName)

            if (outputFile.exists()) outputFile.delete()

            val fos = FileOutputStream(outputFile)
            val inputStream = c.inputStream
            val totalSize = c.contentLength.toFloat() //size of file

            val buffer = ByteArray(1024)
            var len1: Int
            var downloaded = 0f
            while (inputStream.read(buffer).also { len1 = it } != -1) {
                fos.write(buffer, 0, len1)
                downloaded += len1
                publishProgress((downloaded * 100 / totalSize).toInt())
            }
            fos.close()
            inputStream.close()
            flag = true
        } catch (e: MalformedURLException) {
            flag = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return flag
    }
}

/**
 * A job version of the CompositeDisposable
 */
class JobDisposable {

    private var resources: HashSet<Job>? = null

    @Volatile
    var disposed = false
        private set

    /**
     * Creates an empty CompositeDisposable.
     */
    constructor()

    /**
     * Creates a CompositeDisposables with the given array of initial elements.
     * @param disposables the array of Disposables to start with
     * @throws NullPointerException if `disposables` or any of its array items is null
     */
    constructor(vararg disposables: Job) {
        Objects.requireNonNull(disposables, "disposables is null")
        resources = HashSet<Job>(disposables.size + 1)
        resources?.addAll(disposables)
    }

    /**
     * Creates a CompositeDisposables with the given Iterable sequence of initial elements.
     * @param disposables the Iterable sequence of Disposables to start with
     * @throws NullPointerException if `disposables` or any of its items is null
     */
    constructor(disposables: Iterable<Job>) {
        Objects.requireNonNull(disposables, "disposables is null")
        resources = HashSet<Job>()
        resources?.addAll(disposables)
    }

    fun dispose() {
        if (disposed) return
        var set: HashSet<Job>?
        synchronized(this) {
            if (disposed) return
            disposed = true
            set = resources
            resources = null
        }
        dispose(set)
    }

    /**
     * Adds a disposable to this container or disposes it if the
     * container has been disposed.
     * @param disposable the disposable to add, not null
     * @return true if successful, false if this container has been disposed
     * @throws NullPointerException if `disposable` is null
     */
    fun add(disposable: Job): Boolean {
        Objects.requireNonNull(disposable, "disposable is null")
        if (!disposed) {
            synchronized(this) {
                if (!disposed) {
                    val set: HashSet<Job> = resources ?: HashSet()
                    resources = set
                    set.add(disposable)
                    return true
                }
            }
        }
        disposable.cancel()
        return false
    }

    /**
     * Atomically adds the given array of Disposables to the container or
     * disposes them all if the container has been disposed.
     * @param disposables the array of Disposables
     * @return true if the operation was successful, false if the container has been disposed
     * @throws NullPointerException if `disposables` or any of its array items is null
     */
    fun addAll(vararg disposables: Job): Boolean {
        Objects.requireNonNull(disposables, "disposables is null")
        if (!disposed) {
            synchronized(this) {
                if (!disposed) {
                    val set: HashSet<Job> = resources ?: HashSet<Job>(disposables.size + 1)
                    resources = set
                    set.addAll(disposables)
                    return true
                }
            }
        }
        disposables.forEach { it.cancel() }
        return false
    }

    /**
     * Removes and disposes the given Job if it is part of this
     * container.
     * @param disposable the Job to remove and dispose, not null
     * @return true if the operation was successful
     */
    fun remove(disposable: Job): Boolean {
        if (delete(disposable)) {
            disposable.cancel()
            return true
        }
        return false
    }

    /**
     * Removes (but does not dispose) the given disposable if it is part of this
     * container.
     * @param disposable the Job to remove, not null
     * @return true if the operation was successful
     */
    fun delete(disposable: Job): Boolean {
        Objects.requireNonNull(disposable, "disposables is null")
        if (disposed) return false
        synchronized(this) {
            if (disposed) return false
            val set: HashSet<Job>? = resources
            if (set == null || !set.remove(disposable)) return false
        }
        return true
    }

    /**
     * Atomically clears the container, then disposes all the previously contained Disposables.
     */
    fun clear() {
        if (disposed) return
        var set: HashSet<Job>?
        synchronized(this) {
            if (disposed) return
            set = resources
            resources = null
        }
        dispose(set)
    }

    /**
     * Returns the number of currently held Disposables.
     * @return the number of currently held Disposables
     */
    val size
        get(): Int {
            if (disposed) return 0
            synchronized(this) {
                return if (disposed) 0 else resources?.size ?: 0
            }
        }

    /**
     * Dispose the contents of the OpenHashSet by suppressing non-fatal
     * Throwables till the end.
     * @param set the OpenHashSet to dispose elements of
     */
    private fun dispose(set: HashSet<Job>?) {
        if (set == null) return
        var errors: MutableList<Throwable?>? = null
        set.forEach {
            try {
                it.cancel()
            } catch (ex: Throwable) {
                if (errors == null) errors = ArrayList()
                errors!!.add(ex)
            }
        }
        if (errors != null) throw Exception(errors.toString())
    }

    operator fun plusAssign(job: Job) {
        add(job)
    }

    operator fun plusAssign(job: Array<Job>) {
        addAll(*job)
    }

    operator fun minusAssign(job: Job) {
        remove(job)
    }

    override fun toString(): String = "JobDisposable(size=$size, resources=$resources)"
}

fun Job.addTo(jobDisposable: JobDisposable) = jobDisposable.add(this)
fun Array<Job>.addTo(jobDisposable: JobDisposable) = jobDisposable.addAll(*this)