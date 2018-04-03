package pl.edu.zut.mad.appzut.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.support.design.widget.CollapsingToolbarLayout
import android.view.View
import pl.edu.zut.mad.appzut.R

/**
 * Uses [CollapsingToolbarLayout] to get color animator
 * and apply it to other view by method [addAnimatorForView].
 * This uses tricky solution and it shouldn't be copied!
 */
class CollapsingToolbarColorAnimator(private val collapsingToolbarLayout: CollapsingToolbarLayout) {

    companion object {
        private const val MAX_ALPHA = 255f
    }

    private val colorExpanded = collapsingToolbarLayout.getColorById(R.color.colorAccent)
    private val colorCollapsed = collapsingToolbarLayout.getColorById(R.color.colorPrimary)
    private val argbEvaluator = ArgbEvaluator()
    private val collapsingToolbarColorAnimator: ValueAnimator by lazy {
        with(collapsingToolbarLayout) {
            val scrimAnimator = getScrimAnimator()
            if (scrimAnimator == null) {
                setScrimsShown(true, true)
                setScrimsShown(false, true)
                return@lazy getScrimAnimator()!!
            }
            return@lazy scrimAnimator!!
        }
    }

    /**
     * Note that first call of [CollapsingToolbarLayout.setScrimsShown]
     * is only for internal initialization of scrim animator (prevent from null).
     * Second call is for restoring state of [CollapsingToolbarLayout].
     */
    fun addAnimatorForView(view: View) {
        collapsingToolbarColorAnimator.let {
            it.addUpdateListener { setViewBackgroundByAlpha(view, it.animatedValue as Int) }
        }
    }

    private fun setViewBackgroundByAlpha(view: View, alpha: Int) {
        val fraction = alpha / MAX_ALPHA
        val color = argbEvaluator.evaluate(fraction, colorExpanded, colorCollapsed) as Int
        view.setBackgroundColor(color)
    }

    fun removeAllAnimators() =
        collapsingToolbarColorAnimator.removeAllUpdateListeners()
}
