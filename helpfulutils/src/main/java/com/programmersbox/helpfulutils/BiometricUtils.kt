package com.programmersbox.helpfulutils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

@DslMarker
annotation class BiometricMarker

@DslMarker
annotation class BiometricPromptMark

class BiometricBuilder(private var fragmentActivity: FragmentActivity) {

    class BiometricPromptBuilder {

        companion object {
            fun biometricPromptBuilder(block: BiometricPromptBuilder.() -> Unit) = BiometricPromptBuilder().apply(block).build()
        }

        @BiometricPromptMark
        var title = ""

        @BiometricPromptMark
        var subtitle = ""

        @BiometricPromptMark
        var description = ""

        /**
         * What the cancel button says
         */
        @BiometricPromptMark
        var negativeButton: String? = null

        /**
         * Optional: A hint to the system to require user confirmation after a biometric has been authenticated.
         * For example, implicit modalities like Face and Iris authentication are passive, meaning they don't require an explicit user action to complete.
         * When set to 'false', the user action (e.g. pressing a button) will not be required. BiometricPrompt will require confirmation by default.
         * A typical use case for not requiring confirmation would be for low-risk transactions, such as re-authenticating a recently authenticated application.
         * A typical use case for requiring confirmation would be for authorizing a purchase. Note that this is a hint to the system.
         * The system may choose to ignore the flag.
         * For example, if the user disables implicit authentication in Settings, or if it does not apply to a modality (e.g. Fingerprint).
         * When ignored, the system will default to requiring confirmation. This method only applies to Q and above.
         */
        @BiometricPromptMark
        var confirmationRequired = false

        /**
         * If true, allows user to confirm using a pin, pattern, or password
         * You cannot have this and [negativeButton] set
         */
        @BiometricPromptMark
        var deviceCredentialAllowed = false

        private fun build() = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setConfirmationRequired(confirmationRequired)
            .apply {
                require(!(negativeButton != null && deviceCredentialAllowed)) { "Can't have both negative button behavior and device credential enabled" }
                negativeButton?.let { setNegativeButtonText(it) }
                setDeviceCredentialAllowed(deviceCredentialAllowed)
            }
            .build()
    }

    enum class BiometricErrorType(var num: Int, var reason: String) {
        NO_HARDWARE(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, "No biometric features available on this device."),
        HW_UNAVAILABLE(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, "Biometric features are currently unavailable."),
        NONE_ENROLLED(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED, "The user hasn't associated any biometric credentials with their account.")
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.P)
        @RequiresPermission(allOf = [Manifest.permission.USE_BIOMETRIC])
        fun biometricBuilder(fragmentActivity: FragmentActivity, block: BiometricBuilder.() -> Unit) =
            BiometricBuilder(fragmentActivity).apply(block).build()
    }

    private var biometricPromptInfo: BiometricPrompt.PromptInfo? = null
    private var onAuthFailed: () -> Unit = {}
    private var onAuthError: (errorCode: Int, errorMessage: String) -> Unit = { _, _ -> }
    private var onAuthSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit = { _ -> }
    private var onError: (error: BiometricErrorType) -> Unit = { System.err.println("$it: ${it.reason}") }

    @BiometricMarker
    fun authFailed(block: () -> Unit) = run { onAuthFailed = block }

    @BiometricMarker
    fun authError(block: (errorCode: Int, errorMessage: String) -> Unit) = run { onAuthError = block }

    @BiometricMarker
    fun authSuccess(block: (result: BiometricPrompt.AuthenticationResult) -> Unit) = run { onAuthSuccess = block }

    @BiometricMarker
    fun error(block: (error: BiometricErrorType) -> Unit) = run { onError = block }

    @BiometricMarker
    fun promptInfo(block: BiometricPromptBuilder.() -> Unit) = run { biometricPromptInfo = BiometricPromptBuilder.biometricPromptBuilder(block) }

    private fun build() {
        when (BiometricManager.from(fragmentActivity).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPromptInfo?.let {
                val biometricPrompt = BiometricPrompt(fragmentActivity, Executors.newSingleThreadExecutor(),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            fragmentActivity.runOnUiThread { onAuthError(errorCode, errString.toString()) }
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            fragmentActivity.runOnUiThread(onAuthFailed)
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            fragmentActivity.runOnUiThread { onAuthSuccess(result) }
                        }
                    })
                biometricPrompt.authenticate(it)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> onError(BiometricErrorType.NO_HARDWARE)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> onError(BiometricErrorType.HW_UNAVAILABLE)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> onError(BiometricErrorType.NONE_ENROLLED)
        }
    }
}