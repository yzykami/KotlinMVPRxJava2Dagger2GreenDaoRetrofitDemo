package com.dch.test.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.dch.test.R;
import com.dch.test.widget.SwipeLayout;
import com.dch.test.widget.WaterRefreshView;

public class TestViewActivity extends AppCompatActivity {

    private int transY;
    private AnimatedVectorDrawable drawable;
    private AnimatedVectorDrawable drawableTick;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        final WaterRefreshView waterRefreshView = (WaterRefreshView) findViewById(R.id.waterrefreshview);
        waterRefreshView.setOnRefreshListener(new WaterRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawableTick.start();
                        waterRefreshView.refreshSuccess();
                    }
                },200);
            }
        });
        final SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.swipe_edit_layout);
        swipeLayout.setOnMenuClickListener(new SwipeLayout.OnMenuClickListener() {
            @Override
            public void onMenuClick(View v) {
                transWaterRefreshView(waterRefreshView);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(swipeLayout, "alpha", 1f, 0f);
                alpha.setDuration(500);
                alpha.setInterpolator(new DecelerateInterpolator());
                alpha.start();
                alpha.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        swipeLayout.setVisibility(View.GONE);
                    }
                });
            }
        });
//        final SwipeLayout swipeLayout2 = (SwipeLayout) findViewById(R.id.swipe_edit_layout2);
//        swipeLayout2.setOnMenuClickListener(new SwipeLayout.OnMenuClickListener() {
//            @Override
//            public void onMenuClick(View v) {
////                transWaterRefreshView(waterRefreshView);
//                ObjectAnimator alpha = ObjectAnimator.ofFloat(swipeLayout2, "translationX", 0,swipeLayout2.getWidth());
//                alpha.setDuration(500);
//                alpha.setInterpolator(new DecelerateInterpolator());
//                alpha.start();
//                alpha.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        swipeLayout2.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
        swipeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                transY = swipeLayout.getHeight();
                swipeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        ImageView iv_vector = (ImageView) findViewById(R.id.iv_test_view_vector_jrtt);
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) iv_vector.getDrawable();
        drawable.start();
        ImageView iv_tick = (ImageView) findViewById(R.id.iv_test_view_vector_tick);
        drawableTick = (AnimatedVectorDrawable) iv_tick.getDrawable();
        drawableTick.start();
    }

    private void transWaterRefreshView(WaterRefreshView waterRefreshView) {
        final ObjectAnimator translateY = ObjectAnimator.ofFloat(waterRefreshView, "translationY", waterRefreshView.getTop(), -transY);
        translateY.setDuration(400);
        translateY.setInterpolator(new DecelerateInterpolator());
        translateY.setStartDelay(400);
        translateY.start();
    }
}
