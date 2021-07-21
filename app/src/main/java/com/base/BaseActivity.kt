

package com.base


import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

import com.utils.MessageUtils
import org.jetbrains.annotations.NotNull


public open class BaseActivity : AppCompatActivity() {


    protected open fun showToast(@StringRes resId: Int) {
        MessageUtils.showToast(this, resId)
    }

    open fun convertKmsToMiles(kms: Float): Float {
        return (0.621371 * kms).toFloat()
    }

    protected open fun showToast(@NotNull message: String) {
        MessageUtils.showToast(this, message)
    }

    protected open fun showAlert(@NotNull message: String) {
        MessageUtils.showAlert(this, message)
    }


}