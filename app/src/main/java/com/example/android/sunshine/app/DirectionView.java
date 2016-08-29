package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class DirectionView extends View {
    private Arrow mArrow = new Arrow();
    private Paint mPaint = new Paint();

    private double mDirection = 30.0;

    public DirectionView(Context context) {
        super(context);
    }

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionView(Context context, AttributeSet attrs,
                         int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    public void setDirection(float direction) {
        mDirection = direction;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mArrow.initArrowMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate((float) mDirection, getWidth() / 2, getHeight() / 2);

        mPaint.setColor(getResources().getColor(R.color.sunshine_blue));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(1);

        canvas.drawPath(mArrow.getRightArrow(), mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(mArrow.getLeftArrow(), mPaint);
    }

    private class Arrow extends Path {
        private PointF leftPoint;
        private PointF rightPoint;
        private PointF middlePoint;
        private PointF centerPoint;

        public void initArrowMetrics() {
            float width = getWidth() / 2;
            float height = getHeight() / 2;

            centerPoint = new PointF(getWidth() / 2, getHeight() / 2);

            leftPoint = new PointF(centerPoint.x - width / 2, centerPoint.y);
            rightPoint = new PointF(centerPoint.x + width / 2, centerPoint.y);
            middlePoint = new PointF(centerPoint.x, 0);
        }

        public Arrow getArrow() {
            moveTo(leftPoint.x, leftPoint.y);
            lineTo(rightPoint.x, rightPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(leftPoint.x, leftPoint.y);

            close();

            return this;
        }
        public Arrow getRightArrow() {
            moveTo(middlePoint.x, rightPoint.y);
            lineTo(rightPoint.x, rightPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(middlePoint.x, rightPoint.y);

            close();

            return this;
        }

        public Arrow getLeftArrow() {
            moveTo(leftPoint.x, leftPoint.y);
            lineTo(middlePoint.x, leftPoint.y);
            lineTo(middlePoint.x, middlePoint.y);
            lineTo(leftPoint.x, leftPoint.y);

            close();

            return this;
        }
    }
}

