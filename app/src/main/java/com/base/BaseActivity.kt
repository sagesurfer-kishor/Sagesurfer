/*
 * Copyright (c) 2021. Suthar Rohit
 * Developed by Suthar Rohit for NicheTech Computer Solutions Pvt. Ltd. & Matrubharti Technologies Pvt. Ltd. use only.
 * <a href="https://www.RohitSuthar.com/">Suthar Rohit</a>
 *
 * @author Suthar Rohit
 */

package com.base


import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

import com.utils.MessageUtils
import org.jetbrains.annotations.NotNull


/**
 * SirohiWale(com.moving4good) <br />
 * Developed by <b><a href="https://www.RohitSuthar.com/">Suthar Rohit</a></b>  <br />
 * on 12-Feb-2021.
 *
 * @author Suthar Rohit
 */
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