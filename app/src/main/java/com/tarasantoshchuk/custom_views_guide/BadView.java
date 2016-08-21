package com.tarasantoshchuk.custom_views_guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class BadView extends View {
    public BadView(Context context) {
        super(context);
    }

    public BadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(2 * widthSize, 2 * heightSize);
    }
}
