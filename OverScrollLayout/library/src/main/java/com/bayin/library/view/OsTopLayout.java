package com.bayin.library.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bayin.library.R;

import java.util.Observer;

/****************************************
 * 功能说明:  
 *
 * Author: Created by bayin on 2017/7/21.
 ****************************************/

public class OsTopLayout extends ScrollView {
    private static final float MOVE_FACTOR = 0.5f;
    private ViewGroup contentview;
    private View mFooterView;
    private boolean isAtTop = false, isAtBottom = false, isMove = false;
    private float mStartY;
    private int footerHeight;
    private TextView mTvMsg;
    private String normal_msg = "上拉查看更多";
    private String alter_msg = "释放查看更多";
    private boolean isRotated = false;

    private boolean canLoadMore = false;//是否需要切换页面
    private OverScrollObservable mOverScrollObservable;
    private ImageView mImageArrowUp;
    private ObjectAnimator mRotate, mRotateUp;


    public OsTopLayout(Context context) {
        this(context, null);
    }

    public OsTopLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        loadAnim();
    }

    public int getChildCounts() {
        return contentview.getChildCount();
    }

    public ViewGroup getContentView() {
        return contentview;
    }

    /**
     * @param context
     * @param id
     */
    public OsTopLayout(Context context, int id) {
        this(context, null);
        contentview = (ViewGroup) LayoutInflater.from(context).inflate(id, null, false);
        addView(contentview);
        contentview.addView(mFooterView, contentview.getChildCount());
    }

    public void subcribeObsever(Observer observer) {
        mOverScrollObservable.addObserver(observer);
    }

    public void setContentview(View contentview) {
        this.contentview = (ViewGroup) contentview;
    }

    private void initView() {
        mFooterView = inflate(getContext(), R.layout.bottom_footer_layout, null);
        mImageArrowUp = (ImageView) mFooterView.findViewById(R.id.image_arrow_up);
        mTvMsg = (TextView) mFooterView.findViewById(R.id.text_msg);
        mOverScrollObservable = new OverScrollObservable();
    }

    private void loadAnim() {
        mRotate = ObjectAnimator.ofFloat(mImageArrowUp, "rotation", 0, -180);
        mRotate.setDuration(300);
        mRotate.setInterpolator(new DecelerateInterpolator());

        mRotateUp = ObjectAnimator.ofFloat(mImageArrowUp, "rotation", -180, 0);
        mRotateUp.setDuration(300);
        mRotateUp.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentview = (ViewGroup) getChildAt(0);
            contentview.addView(mFooterView, contentview.getChildCount());
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentview == null || mFooterView == null || getChildCount() < 1) return;
        footerHeight = mFooterView.getMeasuredHeight();
        int width = contentview.getMeasuredWidth();
        int height = contentview.getMeasuredHeight();
        contentview.layout(0, 0, width, height);
        setPadding(0, 0, 0, -footerHeight);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isAtTop = isAtTop();
                isAtBottom = isAtBottom();
                mStartY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isAtTop && !isAtBottom) {
                    mStartY = (int) ev.getRawY();
                    isAtTop = isAtTop();
                    isAtBottom = isAtBottom();
                    break;
                }
                float nowY = ev.getRawY();
                float offsetY = (nowY - mStartY) * MOVE_FACTOR;

                Log.e("xxx", "父布局滑动：" + getScrollY());
                Log.i("xxx", "偏移量: " + offsetY);
                //在顶部 && 向下move，触发overScroll
                //在底部 && 向上move，触发overScroll
                //高度小于屏幕高度，isAttop && isAtBottom
                boolean shouldMove = (isAtTop && offsetY > 0 || isAtBottom && offsetY < 0 || isAtTop && isAtBottom);

                if (shouldMove) {
                    contentview.setTranslationY(offsetY);
                    isMove = true;
                    if (offsetY + footerHeight < 0) {
                        canLoadMore = true;
                        mTvMsg.setText(alter_msg);
                        if (!isRotated) {
                            //执行动画
                            mRotate.start();
                            isRotated = true;
                        }
                    } else {
                        canLoadMore = false;
                        mTvMsg.setText(normal_msg);
                        if (isRotated) {
                            //执行动画
                            mRotateUp.start();
                            isRotated = false;
                        }
                    }
                    return true;
                }

                if (isAtTop && offsetY < 0 && contentview.getTranslationY() > 0) {
                    contentview.setTranslationY(contentview.getTranslationY() + offsetY);
                    isMove = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //判断是否加载下一页
                if (canLoadMore) {
                    mOverScrollObservable.notifyLoadMore();
                    canLoadMore = false;
                    mTvMsg.setText(normal_msg);
                }
                if (isMove) resetLayout();
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 将布局复原
     */
    private void resetLayout() {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(contentview, "translationY", contentview.getTranslationY(), 0);
        translationY.setDuration(400);
        translationY.start();
        if (isRotated)
            mRotateUp.start();
        isMove = false;
        isAtTop = false;
        isAtBottom = false;
    }


    private boolean isAtTop() {
        return getScrollY() == 0
                || contentview.getHeight() < getHeight() + getScrollY();
    }

    private boolean isAtBottom() {
        return contentview.getHeight() <= getHeight() + getScrollY() - getPaddingBottom();
    }
}
