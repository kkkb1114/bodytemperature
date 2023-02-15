package kkkb1114.sampleproject.bodytemperature.thermometer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import kkkb1114.sampleproject.bodytemperature.R;

/**
 * Created by Kofi Gyan on 11/27/2015.
 */


public class Thermometer extends View {

    //온도계 원 페인트
    private Paint mInnerCirclePaint;
    private Paint mOuterCirclePaint;
    private Paint mFirstOuterCirclePaint;

    //온도계 아크 페인트
    private Paint mFirstOuterArcPaint;


    //온도계 라인 페인트
    private Paint mInnerLinePaint;
    private Paint mOuterLinePaint;
    private Paint mFirstOuterLinePaint;


    //온도계 반지름
    private int mOuterRadius;
    private int mInnerRadius;
    private int mFirstOuterRadius;


    //온도계 색상
    private int mThermometerColor = Color.rgb(200, 115, 205);

    //원과 선 변수
    private float mLastCellWidth;
    private int mStageHeight;
    private float mCellWidth;
    private float mStartCenterY; //center of first cell
    private float mEndCenterY; //center of last cell
    private float mStageCenterX;
    private float mXOffset;
    private float mYOffset;

    // I   1st Cell     I  2nd Cell       I  3rd Cell  I
    private static final int NUMBER_OF_CELLS = 3; //모든 ie.stageHeight에서 3개의 셀을 3개의 동일한 셀로 나눕니다.

    //애니메이션 변수
    private float mIncrementalTempValue;
    private boolean mIsAnimating;
    private Animator mAnimator;


    public Thermometer(Context context) {
        this(context, null);
    }

    public Thermometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Thermometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {

            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Thermometer, defStyle, 0);

            mThermometerColor = a.getColor(R.styleable.Thermometer_thermometerBg, mThermometerColor);

            a.recycle();
        }

        init();
    }


    private void init() {

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(mThermometerColor);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);
        mInnerCirclePaint.setStrokeWidth(17f);


        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setColor(Color.WHITE);
        mOuterCirclePaint.setStyle(Paint.Style.FILL);
        mOuterCirclePaint.setStrokeWidth(32f);


        mFirstOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirstOuterCirclePaint.setColor(mThermometerColor);
        mFirstOuterCirclePaint.setStyle(Paint.Style.FILL);
        mFirstOuterCirclePaint.setStrokeWidth(60f);


        mFirstOuterArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirstOuterArcPaint.setColor(mThermometerColor);
        mFirstOuterArcPaint.setStyle(Paint.Style.STROKE);
        mFirstOuterArcPaint.setStrokeWidth(30f);


        mInnerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerLinePaint.setColor(mThermometerColor);
        mInnerLinePaint.setStyle(Paint.Style.FILL);
        mInnerLinePaint.setStrokeWidth(17f);

        mOuterLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterLinePaint.setColor(Color.WHITE);
        mOuterLinePaint.setStyle(Paint.Style.FILL);


        mFirstOuterLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirstOuterLinePaint.setColor(mThermometerColor);
        mFirstOuterLinePaint.setStyle(Paint.Style.FILL);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mStageCenterX = getWidth() / 2;
        Log.e("mStageCenterX", String.valueOf(mStageCenterX));

        mStageHeight = getHeight();

        Log.e("mStageHeight", String.valueOf(mStageHeight));

        mCellWidth = mStageHeight / NUMBER_OF_CELLS;
        Log.e("mCellWidth", String.valueOf(mCellWidth));

        //첫 번째 셀의 중심
        mStartCenterY = mCellWidth / 2;
        //float data = (float) ((35.6 / 45) * 100);
        //mStartCenterY = (mStageHeight / 100) * data;
        Log.e("mStartCenterY", String.valueOf(mStartCenterY));

        //세 번째 셀로 이동
        mLastCellWidth = (NUMBER_OF_CELLS * mCellWidth);
        Log.e("mLastCellWidth", String.valueOf(mLastCellWidth));

        //마지막(세 번째) 셀 중심
        mEndCenterY = mLastCellWidth - (mCellWidth / 2);
        Log.e("mEndCenterY", String.valueOf(mEndCenterY));


        // mOuterRadius는 mCellWidth의 1/4입니다.
        mOuterRadius = (int) (0.25 * mCellWidth);

        mInnerRadius = (int) (0.656 * mOuterRadius);

        mFirstOuterRadius = (int) (1.344 * mOuterRadius);

        mFirstOuterLinePaint.setStrokeWidth(mFirstOuterRadius);

        mOuterLinePaint.setStrokeWidth(mFirstOuterRadius / 2);

        mFirstOuterArcPaint.setStrokeWidth(mFirstOuterRadius / 4);

        mXOffset = mFirstOuterRadius / 4;
        mXOffset = mXOffset / 2;

        //get the d/f btn firstOuterLine and innerAnimatedline
        mYOffset = (mStartCenterY + (float) 0.875 * mOuterRadius) - (mStartCenterY + mInnerRadius);
        mYOffset = mYOffset / 2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFirstOuterCircle(canvas);

        drawOuterCircle(canvas);

        drawInnerCircle(canvas);

        drawFirstOuterLine(canvas);

        drawOuterLine(canvas);

        animateInnerLine(canvas);

        drawFirstOuterCornerArc(canvas);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //take care of paddingTop and paddingBottom
        int paddingY = getPaddingBottom() + getPaddingTop();

        //get height and width
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        height += paddingY;

        setMeasuredDimension(width, height);
    }


    private void drawInnerCircle(Canvas canvas) {
        drawCircle(canvas, mInnerRadius, mInnerCirclePaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        drawCircle(canvas, mOuterRadius, mOuterCirclePaint);
    }


    private void drawFirstOuterCircle(Canvas canvas) {
        drawCircle(canvas, mFirstOuterRadius, mFirstOuterCirclePaint);
    }


    private void drawCircle(Canvas canvas, float radius, Paint paint) {
        canvas.drawCircle(mStageCenterX, mEndCenterY, radius, paint);
    }

    private void drawOuterLine(Canvas canvas) {

        float startY = mEndCenterY - (float) (0.875 * mOuterRadius);
        float stopY = mStartCenterY + (float) (0.875 * mOuterRadius);

        drawLine(canvas, startY, stopY, mOuterLinePaint);
    }


    private void drawFirstOuterLine(Canvas canvas) {

        float startY = mEndCenterY - (float) (0.875 * mFirstOuterRadius);
        float stopY = mStartCenterY + (float) (0.875 * mOuterRadius);

        drawLine(canvas, startY, stopY, mFirstOuterLinePaint);
    }


    private void drawLine(Canvas canvas, float startY, float stopY, Paint paint) {
        canvas.drawLine(mStageCenterX, startY, mStageCenterX, stopY, paint);
    }


    //simulate temperature measurement for now
    private void animateInnerLine(Canvas canvas) {

        if (mAnimator == null)
            measureTemperature();


        if (!mIsAnimating) {

            mIncrementalTempValue = mEndCenterY + (float) (0.875 * mInnerRadius);

            mIsAnimating = true;

        } else {

            mIncrementalTempValue = mEndCenterY + (float) (0.875 * mInnerRadius) - mIncrementalTempValue;

        }

        if (mIncrementalTempValue > mStartCenterY + mInnerRadius) {
            float startY = mEndCenterY + (float) (0.875 * mInnerRadius);
            drawLine(canvas, startY, mIncrementalTempValue, mInnerCirclePaint);

        } else {

            float startY = mEndCenterY + (float) (0.875 * mInnerRadius);
            float stopY = mStartCenterY + mInnerRadius;
            drawLine(canvas, startY, stopY, mInnerCirclePaint);
            mIsAnimating = false;
            stopMeasurement();

        }

    }


    private void drawFirstOuterCornerArc(Canvas canvas) {

        float y = mStartCenterY - (float) (0.875 * mFirstOuterRadius);

        RectF rectF = new RectF(mStageCenterX - mFirstOuterRadius / 2 + mXOffset, y + mFirstOuterRadius, mStageCenterX + mFirstOuterRadius / 2 - mXOffset, y + (2 * mFirstOuterRadius) + mYOffset);

        canvas.drawArc(rectF, -180, 180, false, mFirstOuterArcPaint);

    }


    public void setThermometerColor(int thermometerColor) {
        this.mThermometerColor = thermometerColor;

        mInnerCirclePaint.setColor(mThermometerColor);

        mFirstOuterCirclePaint.setColor(mThermometerColor);

        mFirstOuterArcPaint.setColor(mThermometerColor);

        mInnerLinePaint.setColor(mThermometerColor);

        mFirstOuterLinePaint.setColor(mThermometerColor);

        invalidate();
    }


    //simulate temperature measurement for now
    private void measureTemperature() {
        mAnimator = new Animator();
        mAnimator.start();
    }


    private class Animator implements Runnable {
        private Scroller mScroller;
        private final static int ANIM_START_DELAY = 1000;
        private final static int ANIM_DURATION = 4000;
        private boolean mRestartAnimation = false;

        public Animator() {
            mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        }

        public void run() {
            if (mAnimator != this)
                return;

            if (mRestartAnimation) {
                int startY = (int) (mStartCenterY - (float) (0.875 * mInnerRadius));
                int dy = (int) (mEndCenterY + mInnerRadius);
                mScroller.startScroll(0, startY, 0, dy, ANIM_DURATION);
                mRestartAnimation = false;
            }

            boolean isScrolling = mScroller.computeScrollOffset();
            mIncrementalTempValue = mScroller.getCurrY();

            if (isScrolling) {
                invalidate();
                post(this);
            } else {
                stop();
            }


        }

        public void start() {
            mRestartAnimation = true;
            postDelayed(this, ANIM_START_DELAY);
        }

        public void stop() {
            removeCallbacks(this);
            mAnimator = null;
        }

    }


    private void stopMeasurement() {
        if (mAnimator != null)
            mAnimator.stop();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        measureTemperature();

    }

    @Override
    protected void onDetachedFromWindow() {
        stopMeasurement();

        super.onDetachedFromWindow();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        switch (visibility) {
            case View.VISIBLE:

                measureTemperature();

                break;

            default:

                stopMeasurement();

                break;
        }
    }


}