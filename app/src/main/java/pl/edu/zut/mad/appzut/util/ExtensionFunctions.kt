package pl.edu.zut.mad.appzut.util

import android.animation.ValueAnimator
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.view.View

@ColorInt
fun View.getColorById(@ColorRes colorId: Int) = ContextCompat.getColor(this.context, colorId)

fun CollapsingToolbarLayout.getScrimAnimator() =
    javaClass.getDeclaredField("mScrimAnimator").let {
        it.isAccessible = true
        it.get(this) as ValueAnimator?
    }
