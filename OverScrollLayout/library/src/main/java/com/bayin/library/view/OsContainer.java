package com.bayin.library.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bayin.library.view.utils.ScreenUtil;

import java.util.Observable;
import java.util.Observer;

/****************************************
 * 功能说明:  
 *
 * Author: Created by bayin on 2017/7/21.
 ****************************************/

public class OsContainer extends FrameLayout implements Observer {

    private OsTopLayout mTopView;
    private OsBottomLayout mBottomView;
    private ValueAnimator mValueAnimator;
    private ObjectAnimator mAlphaAnim;
    private int mWindowHeight;

    public OsContainer(@NonNull Context context) {
        this(context, null);
    }

    public OsContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OsContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mWindowHeight = ScreenUtil.getWindowHeight(context);
    }

    public void setChildView(OsTopLayout topView, OsBottomLayout bottomView) {
        this.mTopView = topView;
        this.mBottomView = bottomView;
        addView(mBottomView, 0);
        addView(mTopView, 1);
        mBottomView.setVisibility(INVISIBLE);
        mTopView.setVisibility(VISIBLE);
        mTopView.subcribeObsever(this);
        mBottomView.subcribeObsever(this);

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof OverScrollObservable) {
            if (arg.equals(OverScrollObservable.MSG_LOADMORE)) {
                Toast.makeText(getContext(), "load more...", Toast.LENGTH_SHORT).show();
                startLoadMoreAnimation();
            } else if (arg.equals(OverScrollObservable.MSG_RESET)) {
                Toast.makeText(getContext(), "reset...", Toast.LENGTH_SHORT).show();
                startResetAnimation();
            }
        }
    }

    private int ANIM_UNIT = 400;

    private void startLoadMoreAnimation() {
        ObjectAnimator dismissTopAnim = ObjectAnimator.ofFloat(mTopView, "alpha", 1f, 0f).setDuration(ANIM_UNIT);
        ObjectAnimator slideInBottomAnim = ObjectAnimator.ofFloat(mBottomView.getContentView(), "translationY", mWindowHeight, 0).setDuration(ANIM_UNIT);
        slideInBottomAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBottomView.setVisibility(VISIBLE);
                mBottomView.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTopView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(dismissTopAnim).before(slideInBottomAnim);
        set.start();
    }

    private void startResetAnimation() {
        ObjectAnimator dismissBottomAnim = ObjectAnimator.ofFloat(mBottomView, "alpha", 1f, 0f).setDuration(ANIM_UNIT);
        ObjectAnimator slideInTopAnim = ObjectAnimator.ofFloat(mTopView.getContentview(), "translationY", -mWindowHeight, 0).setDuration(ANIM_UNIT);
        slideInTopAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTopView.setVisibility(VISIBLE);
                mTopView.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(dismissBottomAnim).before(slideInTopAnim);
        set.start();
    }
}
