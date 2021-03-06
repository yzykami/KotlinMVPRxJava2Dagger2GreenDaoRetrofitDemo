package com.dch.test.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dch.test.R;

/**
 * 作者：MrCoder on 2017/7/10 14:30
 * 描述：
 * 邮箱：daichuanhao@caijinquan.com
 */
public class WaterView extends View {


    private int defaultWaterColor = Color.RED;
    private int defaultCircleRadius = 40;
    private int mLittleCircleRadiusForGather = 0;
    private int mBigCircleRadius = 40;
    private int mBigCircleRadiusForGather = 0;
    private int mLittleCircleRadius = 40;
    private int mMoveY = 0;
    private int mState = STATE_DEFAULT;//水滴的状态
    private static final int STATE_DEFAULT = 0X001; //默认
    private static final int STATE_DRAG = 0X002; //拖拽
    private static final int STATE_GATHER = 0X003;//汇合
    private int mWaterCenterX = 0, mWaterCenterY = 0, mDefaultY = 0;
    private int mCircleCenterX = 0, mCircleCenterY = 0;
    //    private static final int DEFAULT_MIN_CHANGE_STATE_HEIGHT = 20;
    private static final int DEFAULT_MAX_CHANGE_STATE_HEIGHT = 200;
    private Path mBezierPath;
    private Paint mWaterPaint;
    private ValueAnimator mGatherAnimator;
    private Bitmap mWaterProgressBar;
    private Rect mBitmapRect;
    private Rect mSrcRect;
    private Paint mBitmapPaint;

    public WaterView(Context context) {
        this(context, null);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mWaterPaint = new Paint();
        mWaterPaint.setStyle(Paint.Style.FILL);
        mWaterPaint.setAntiAlias(true);
        mWaterPaint.setColor(defaultWaterColor);

        mBezierPath = new Path();
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mWaterCenterX = right / 2;
                mWaterCenterY = bottom * 4 / 5;
                mCircleCenterX = mWaterCenterX;
                mCircleCenterY = mWaterCenterY;
                mDefaultY = mWaterCenterY;
                mSrcRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                invalidate();
            }
        });

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);

        mWaterProgressBar = getBitmapFromVectorDrawable(context,R.drawable.water_progressbar);
        mBitmapRect = new Rect();
        mSrcRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mState != STATE_GATHER) {
            //画小水滴
            canvas.drawCircle(mWaterCenterX, mWaterCenterY, mLittleCircleRadius, mWaterPaint);
            if (mWaterProgressBar != null && mState != STATE_DRAG){
                mBitmapRect.left = mCircleCenterX - mLittleCircleRadius + 5;
                mBitmapRect.top = mWaterCenterY - mLittleCircleRadius + 5;
                mBitmapRect.right = mCircleCenterX + mLittleCircleRadius - 5;
                mBitmapRect.bottom = mWaterCenterY + mLittleCircleRadius - 5;
                canvas.drawBitmap(mWaterProgressBar,mSrcRect, mBitmapRect,mBitmapPaint);
            }
        } else {
            //画大水滴
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, mBigCircleRadius, mWaterPaint);
            //小水滴向大水滴移动
            //画移动水滴
            canvas.drawCircle(mWaterCenterX, mWaterCenterY, mLittleCircleRadius, mWaterPaint);
            //画贝塞尔曲线
            mBezierPath.reset();
            mBezierPath.moveTo(mCircleCenterX - mBigCircleRadius, mCircleCenterY);
            mBezierPath.quadTo(mWaterCenterX - 5, mCircleCenterY + mMoveY / 2, mWaterCenterX - mLittleCircleRadius, mWaterCenterY);
            mBezierPath.lineTo(mWaterCenterX + mLittleCircleRadius, mWaterCenterY);
            mBezierPath.quadTo(mWaterCenterX + 5, mCircleCenterY + mMoveY / 2, mWaterCenterX + mBigCircleRadius, mCircleCenterY);
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mWaterPaint);
        }
        if (mState == STATE_DRAG) {
            //画移动水滴
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, mBigCircleRadius, mWaterPaint);
            //画贝塞尔曲线
            mBezierPath.reset();
            mBezierPath.moveTo(mWaterCenterX - mLittleCircleRadius, mWaterCenterY);
            mBezierPath.quadTo(mWaterCenterX - 5, mWaterCenterY - mMoveY / 2, mWaterCenterX - mBigCircleRadius, mCircleCenterY);
            mBezierPath.lineTo(mWaterCenterX + mBigCircleRadius, mCircleCenterY);
            mBezierPath.quadTo(mWaterCenterX + 5, mWaterCenterY - mMoveY / 2, mWaterCenterX + mLittleCircleRadius, mWaterCenterY);
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mWaterPaint);
            if (mWaterProgressBar != null){
                mBitmapRect.left = mCircleCenterX - mBigCircleRadius + 5;
                mBitmapRect.top = mCircleCenterY - mBigCircleRadius + 5;
                mBitmapRect.right = mCircleCenterX + mBigCircleRadius - 5;
                mBitmapRect.bottom = mCircleCenterY + mBigCircleRadius - 5;
                canvas.drawBitmap(mWaterProgressBar,mSrcRect, mBitmapRect,mBitmapPaint);
            }
        }
    }


    public void setMoveState(int moveY) {
        if (moveY < 0) {
            throw new RuntimeException("WaterView移动y值必须大于0");
        }
        this.mMoveY = moveY / 2;
//        if (mMoveY > 120) {
//            mMoveY = 120;
//        }
        mState = STATE_DRAG;
        mLittleCircleRadius = (defaultCircleRadius) * ((DEFAULT_MAX_CHANGE_STATE_HEIGHT - mMoveY)) * 9 / 10 / DEFAULT_MAX_CHANGE_STATE_HEIGHT;
        mLittleCircleRadiusForGather = mLittleCircleRadius;
        mBigCircleRadius = (defaultCircleRadius) * ((DEFAULT_MAX_CHANGE_STATE_HEIGHT - mMoveY * 2 / 5)) / DEFAULT_MAX_CHANGE_STATE_HEIGHT;
        mBigCircleRadiusForGather = mBigCircleRadius;

        mCircleCenterY = mWaterCenterY - moveY;

        invalidate();
    }

    public void setMoveStateForGather(int moveY) {
        if (moveY < 0) {
            throw new RuntimeException("WaterView移动y值必须大于0");
        }
//        this.mMoveY = moveY / 2;
//        if (mMoveY > 120) {
//            mMoveY = 120;
//        }
        if (moveY > 0) {
//            mLittleCircleRadius = mLittleCircleRadiusForGather+(defaultCircleRadius-mLittleCircleRadiusForGather)*moveY/mMoveY/2;
//            mBigCircleRadius = mBigCircleRadiusForGather +moveY/mMoveY*(defaultCircleRadius-mBigCircleRadiusForGather)/2;
//            mWaterCenterY = mDefaultY - moveY;
//            invalidate();
        }

    }

    public void setGatherState() {
        //移动距离超出最大值, 可以下拉刷新了,即隐藏水滴
        if (mState != STATE_GATHER) {
            mState = STATE_GATHER;
            mGatherAnimator = ValueAnimator.ofInt(0, mMoveY * 2);
            mGatherAnimator.setDuration(3000);
            mGatherAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mGatherAnimator.setInterpolator(new LinearInterpolator());
            mGatherAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    setMoveStateForGather(animatedValue);
                }
            });
            mGatherAnimator.start();

            mGatherAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    resetState();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void resetState() {
        mState = STATE_DEFAULT;
        mMoveY = 0;
        mLittleCircleRadius = defaultCircleRadius;
        mBigCircleRadius = defaultCircleRadius;
        mWaterCenterX = getWidth() / 2;
        mWaterCenterY = getHeight() * 4 / 5;
        mCircleCenterX = mWaterCenterX;
        mCircleCenterY = mWaterCenterY;
        mBigCircleRadiusForGather = 0;
        mLittleCircleRadiusForGather = 0;
        invalidate();
    }

}
