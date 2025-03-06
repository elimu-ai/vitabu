package ai.elimu.vitabu.util

import android.os.SystemClock
import android.view.View

/**
 * Prevent double click
 */
abstract class SingleClickListener : View.OnClickListener {
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_INTERVAL) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        onSingleClick(v)
    }

    abstract fun onSingleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_INTERVAL: Long = 2000

        private var mLastClickTime: Long = 0
    }
}