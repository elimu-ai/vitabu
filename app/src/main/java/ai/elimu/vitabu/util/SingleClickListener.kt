package ai.elimu.vitabu.util;

import android.os.SystemClock;
import android.view.View;


/**
 * Prevent double click
 */
public abstract class SingleClickListener implements View.OnClickListener {

    private final static long DOUBLE_CLICK_INTERVAL = 2000;

    private static long mLastClickTime = 0;

    @Override
    final public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_INTERVAL){
            return ;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);
}