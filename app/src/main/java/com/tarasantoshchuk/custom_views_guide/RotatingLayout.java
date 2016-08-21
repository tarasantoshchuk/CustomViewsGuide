package com.tarasantoshchuk.custom_views_guide;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class RotatingLayout extends ViewGroup {
    private static final int UNDEFINED_POSITION = -1;
    private int mViewsCount;
    private Paint mCenterPaint = new Paint();

    public RotatingLayout(Context context) {
        this(context, null);
    }

    public RotatingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RotatingLayout);

        mViewsCount = attributes.getInteger(R.styleable.RotatingLayout_views_count, getChildCount());

        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int squareSize;

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            squareSize = getDefaultSize();
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            squareSize = widthSize;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            squareSize = heightSize;
        } else {
            squareSize = Math.min(heightSize, widthSize);
        }

        int calculatedWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : squareSize;
        int calculatedHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : squareSize;

        setMeasuredDimension(calculatedWidth, calculatedHeight);

        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(getMaxChildWidth(), MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(getMaxChildHeight(), MeasureSpec.AT_MOST)
            );
        }
    }

    private int getMaxChildHeight() {
        return 200;
    }

    private int getMaxChildWidth() {
        return 200;
    }

    private int getDefaultSize() {
        return 1000;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCenterPaint.setColor(Color.parseColor("#ff0000"));
        mCenterPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 20, mCenterPaint);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int side = Math.min(r - l, b - t);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int radius = side / 4;


        int previousViewIndex = -1;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int index = params.index == UNDEFINED_POSITION ? previousViewIndex + 1 : params.index;

            int childLeft = (int) (centerX + radius * Math.sin(2 * index * Math.PI / (float) mViewsCount) - child.getMeasuredWidth() / 2);
            int childTop = (int) (centerY - radius * Math.cos(2 * index * Math.PI / (float) mViewsCount) - child.getMeasuredHeight() / 2);

            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            previousViewIndex = index;
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public class LayoutParams extends ViewGroup.LayoutParams {
        public int index;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray attributes = c.obtainStyledAttributes(attrs, R.styleable.RotatingLayout);
            index = attributes.getInteger(R.styleable.RotatingLayout_layout_viewIndex, UNDEFINED_POSITION);
            attributes.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            if (source instanceof LayoutParams) {
                LayoutParams convertedSource = (LayoutParams) source;
                this.index = convertedSource.index;
            }
        }
    }
}
