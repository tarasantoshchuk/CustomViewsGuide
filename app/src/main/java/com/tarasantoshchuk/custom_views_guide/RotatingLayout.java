package com.tarasantoshchuk.custom_views_guide;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class RotatingLayout extends ViewGroup {
    private static final int UNDEFINED_POSITION = -1;
    private int mPositionsCount;
    private Paint mCenterPaint = new Paint();
    private Paint mFakeViewPaint = new Paint();

    private GestureDetector mGestureDetector;

    private float mRotationAngle = 0;

    private HashMap<Integer, View> mChildren = new HashMap<>();
    private float[] mScaleAnimationCheckpoints = new float[]{1f, 2f, 0.5f, 1f};

    public RotatingLayout(Context context) {
        this(context, null);
    }

    public RotatingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RotatingLayout);

        mPositionsCount = attributes.getInteger(R.styleable.RotatingLayout_positions_count, getChildCount());

        attributes.recycle();

        initPaints();
        initGestureDetector(context);
    }

    private void initPaints() {
        mCenterPaint.setColor(Color.parseColor("#ff0000"));
        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterPaint.setStrokeWidth(5);

        mFakeViewPaint.setColor(Color.parseColor("#464646"));
        mFakeViewPaint.setStyle(Paint.Style.FILL);
        mFakeViewPaint.setStrokeWidth(5);
    }

    private void initGestureDetector(Context context) {
        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        double endX = e2.getX();
                        double endY = e2.getY();

                        double startX = endX - distanceX;
                        double startY = endY - distanceY;

                        double startDx = startX - getCenterX();
                        double startDy = startY - getCenterY();

                        double endDx = endX - getCenterX();
                        double endDy = endY - getCenterY();

                        double startAngle = Math.atan(startDx / startDy);
                        double endAngle = Math.atan(endDx / endDy);

                        double deltaAngle = endAngle - startAngle;

                        if (startDy * endDy < 0) {
                            deltaAngle += Math.PI;
                        }

                        mRotationAngle += deltaAngle;

                        invalidate();

                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        startLongPressAnimation();
                    }
                }
        );

        mGestureDetector.setIsLongpressEnabled(true);

    }

    private void startLongPressAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();

        for (int i = 0; i < mPositionsCount; i++) {
            View child = mChildren.get(i);

            if (child != null) {
                ObjectAnimator animatorX = new ObjectAnimator();
                animatorX.setTarget(child);
                animatorX.setProperty(View.SCALE_X);
                animatorX.setFloatValues(mScaleAnimationCheckpoints);
                ObjectAnimator animatorY = new ObjectAnimator();
                animatorY.setTarget(child);
                animatorY.setProperty(View.SCALE_Y);
                animatorY.setFloatValues(mScaleAnimationCheckpoints);

                animators.add(animatorX);
                animators.add(animatorY);
            }
        }

        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
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
        return 100;
    }

    private int getMaxChildWidth() {
        return 100;
    }

    private int getDefaultSize() {
        return 1000;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mGestureDetector.onTouchEvent(event) || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        updateViewsTranslations();

        int centerX = getCenterX();
        int centerY = getCenterY();

        canvas.drawCircle(centerX, centerY, 20, mCenterPaint);
        for (int i = 0; i < mPositionsCount; i++) {
            canvas.drawLine(centerX, centerY, getViewCenterX(i), getViewCenterY(i), mCenterPaint);

            if (!mChildren.containsKey(i)) {
                canvas.drawRect(
                        getViewCenterX(i) - getMaxChildWidth() / 2,
                        getViewCenterY(i) - getMaxChildWidth() / 2,
                        getViewCenterX(i) + getMaxChildWidth() / 2,
                        getViewCenterY(i) + getMaxChildWidth() / 2,
                        mFakeViewPaint
                );
            }
        }
    }

    private void updateViewsTranslations() {
        for (int i = 0; i < mPositionsCount; i++) {
            View child = mChildren.get(i);

            if (child != null) {
                child.setTranslationX(getViewCenterX(i));
                child.setTranslationY(getViewCenterY(i));
            }
        }
    }

    private int getCenterY() {
        return getHeight() / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int previousViewIndex = -1;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int index = params.index == UNDEFINED_POSITION ? previousViewIndex + 1 : params.index;

            child.layout(
                    - child.getMeasuredWidth() / 2,
                    - child.getMeasuredHeight() / 2,
                    child.getMeasuredWidth() / 2,
                    child.getMeasuredHeight() / 2);
            previousViewIndex = index;

            mChildren.put(index, child);
        }
    }

    private int getRadius() {
        return Math.min(getWidth(), getHeight()) / 4;
    }

    private float getViewCenterY(int index) {
        return (float) (getCenterY() - getRadius() * Math.cos(2 * index * Math.PI / (float) mPositionsCount + mRotationAngle));
    }

    private int getCenterX() {
        return getWidth() / 2;
    }

    private float getViewCenterX(int index) {
        return (float) (getCenterX() + getRadius() * Math.sin(2 * index * Math.PI / (float) mPositionsCount + mRotationAngle));
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
            index = attributes.getInteger(R.styleable.RotatingLayout_layout_view_index, UNDEFINED_POSITION);
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

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState superState = new SavedState(super.onSaveInstanceState());
        superState.rotationAngle = mRotationAngle;
        return superState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        SavedState savedState = (SavedState) state;
        mRotationAngle = savedState.rotationAngle;
    }

    private static class SavedState extends BaseSavedState {
        public float rotationAngle;

        public SavedState(Parcel source) {
            super(source);

            rotationAngle = source.readFloat();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeFloat(rotationAngle);
        }
    }


}
