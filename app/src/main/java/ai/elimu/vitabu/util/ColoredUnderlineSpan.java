package ai.elimu.vitabu.util;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ColoredUnderlineSpan extends ReplacementSpan {

    private float thickness;
    private Paint linePaint;

    public ColoredUnderlineSpan(int color, float thickness) {
        this.thickness = thickness;
        linePaint = new Paint();
        linePaint.setColor(color);
    }


    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int lineBottom = (int) (top + paint.getFontMetrics().bottom - paint.getFontMetrics().top);

        canvas.drawText(text, start, end, x, y, paint);
        canvas.drawRect(x, lineBottom - thickness, (x + paint.measureText(text, start, end)), lineBottom, linePaint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }
}
