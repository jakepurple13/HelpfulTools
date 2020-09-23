package com.programmersbox.rxutils

import androidx.fragment.app.FragmentActivity
import com.programmersbox.helpfulutils.PermissionInfo
import com.programmersbox.helpfulutils.requestPermissions
import io.reactivex.Single

fun FragmentActivity.rxRequestPermissions(vararg permissions: String) =
    Single.create<PermissionInfo> { emitter -> requestPermissions(*permissions, onResult = emitter::onSuccess) }