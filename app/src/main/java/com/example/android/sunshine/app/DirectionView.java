package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.io.Serializable;

public class DirectionView extends View {
    private Arrow mArrow = new Arrow();
    private Paint mPaint = new Paint();

    private double mDirection = 0.0;

    public DirectionView(Context context) {
        super(context);
        setFocusable(true);
    }

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    public DirectionView(Context context, AttributeSet attrs,
                         int defaultStyle) {
        super(context, attrs, defaultStyle);
        setFocusable(true);
    }

    public void setDirection(float direction) {
        mArrow.initArrowMetrics();
        mDirection = direction;
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getContext().getSystemService(
                        Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(
                    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(Utility.getWindDirection((float) mDirection));
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 22;
        int desiredHeight = 22;

        int width;
        int height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        mArrow.initArrowMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mArrow.allInitialized()) {
            return;
        }

        canvas.rotate((float) mDirection, getWidth() / 2, getHeight() / 2);

        mPaint.setColor(getResources().getColor(R.color.sunshine_blue));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(1);

        canvas.drawPath(mArrow.getRightArrow(), mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(mArrow.getLeftArrow(), mPaint);

        canvas.restore();
    }

    private class Arrow extends Path implements Serializable {
        private PointF leftPoint;
        private PointF rightPoint;
        private PointF middlePoint;
        private PointF centerPoint;

        public void initArrowMetrics() {
            float width = (float) getWidth() / 2;
            float height = (float) getHeight() / 2;

            centerPoint = new PointF(getWidth() / 2, getHeight() / 2);

            leftPoint = new PointF(centerPoint.x - width / 2, centerPoint.y);
            rightPoint = new PointF(centerPoint.x + width / 2, centerPoint.y);
            middlePoint = new PointF(centerPoint.x, 0);
        }

        public boolean allInitialized() {
            return (centerPoint != null && leftPoint != null &&
                    rightPoint != null && middlePoint != null);
        }

        public Arrow getArrow() {
            if (!allInitialized()) {
                return null;
            }

            rewind();

            moveTo(leftPoint.x, leftPoint.y);
            lineTo(rightPoint.x, rightPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(leftPoint.x, leftPoint.y);

            close();

            return this;
        }

        public Arrow getRightArrow() {
            if (!allInitialized()) {
                return null;
            }

            rewind();

            moveTo(middlePoint.x, rightPoint.y);
            lineTo(rightPoint.x, rightPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(middlePoint.x, rightPoint.y);

            close();

            return this;
        }

        public Arrow getLeftArrow() {
            if (!allInitialized()) {
                return null;
            }

            rewind();

            moveTo(leftPoint.x, leftPoint.y);
            lineTo(middlePoint.x, leftPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(leftPoint.x, leftPoint.y);

            close();

            return this;
        }
    }
}

