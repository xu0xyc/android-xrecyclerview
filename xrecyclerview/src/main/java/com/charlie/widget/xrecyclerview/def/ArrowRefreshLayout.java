package com.charlie.widget.xrecyclerview.def;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.charlie.widget.xrecyclerview.R;
import com.charlie.widget.xrecyclerview.enumer.ProgressStyle;
import com.charlie.widget.xrecyclerview.interf.IPullRefresh;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Date;

public class ArrowRefreshLayout extends LinearLayout implements IPullRefresh {

    private static final float DRAG_RATE = 2.0f;

    private static final int ROTATE_ANIM_DURATION = 180;
    private static final int SCROLL_ANIM_DURATION = 300;
    private static final int DONE_SHOW_DURATION = 500;

    private LinearLayout mRootLayout;
    private ImageView mArrowImageView;
    private AVLoadingIndicatorView mProgressBar;
    private TextView mStatusTextView;
    private TextView mTimeTextView;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private int mState = STATE_NORMAL;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private ValueAnimator mScrollAnimator;
    private boolean isRefreshSucess;
    private Date mPreRefreshTime;

    private int mTouchSlop;
    private float sumOffset;

    public ArrowRefreshLayout(Context context) {
        this(context, null);
    }

    public ArrowRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        setLayoutParams(lp);
        setPadding(0, 0, 0, 0);

        mRootLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview_refresh_header, null);
        addView(mRootLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        mRootLayout.setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_textview);
        mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.default_pull_refresh_status_hint_color));
        mTimeTextView = (TextView) findViewById(R.id.last_refresh_time);
        mTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.default_pull_refresh_time_hint_color));
        mProgressBar = (AVLoadingIndicatorView) findViewById(R.id.listview_header_progressbar);
        mProgressBar.setIndicator(ProgressStyle.LineSpinFadeLoader.value);
        mProgressBar.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.default_pull_refresh_progress_color));

        mRotateUpAnim = new RotateAnimation(0.0f, 180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mIntrinsicWidth = getMeasuredWidth();
        mIntrinsicHeight = getMeasuredHeight();
    }

    public void setArrowImageView(int resid) {
        mArrowImageView.setImageResource(resid);
    }

    public void setProgressStyle(ProgressStyle style) {
        mProgressBar.setIndicator(style.value);
    }

    public void setProgressColor(int color) {
        mProgressBar.setIndicatorColor(color);
    }

    public void setStatusTextColor(int colorResId) {
        mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), colorResId));
    }

    public void setTimeTextColor(int colorResId) {
        mTimeTextView.setTextColor(ContextCompat.getColor(getContext(), colorResId));
    }

    public void setViewBackgroundColor(int colorResId) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));
    }

    public void setState(int state) {
        if (state == mState) return;

        mArrowImageView.clearAnimation();

        if (state == STATE_REFRESHING) {
            mArrowImageView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            if (mTimeTextView.isShown()) mTimeTextView.setVisibility(View.GONE);
            smoothScrollTo(mIntrinsicHeight);
        } else if (state == STATE_DONE) {
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            if (mTimeTextView.isShown()) mTimeTextView.setVisibility(View.GONE);
            smoothScrollTo(mIntrinsicHeight);
        } else {
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            if (null != mPreRefreshTime) {
                mTimeTextView.setVisibility(View.VISIBLE);
                mTimeTextView.setText(getResources().getString(R.string.listview_header_last_time) + friendlyTime(mPreRefreshTime));
            }
        }

        switch (state) {
            case STATE_NORMAL:
                mArrowImageView.setImageResource(R.drawable.ic_pull_refresh_arrow);
                if (STATE_PREPARE_TO_REFRESH == mState) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                mStatusTextView.setText(R.string.listview_header_hint_normal);
                break;
            case STATE_PREPARE_TO_REFRESH:
                mArrowImageView.setImageResource(R.drawable.ic_pull_refresh_arrow);
                mArrowImageView.startAnimation(mRotateUpAnim);
                mStatusTextView.setText(R.string.listview_header_hint_release);
                break;
            case STATE_REFRESHING:
                mStatusTextView.setText(R.string.refreshing);
                break;
            case STATE_DONE:
                sumOffset = 0;
                if (isRefreshSucess) {
                    mArrowImageView.setImageResource(R.drawable.ic_success_black);
                    mStatusTextView.setText(R.string.refresh_done_success);
                } else {
                    mArrowImageView.setImageResource(R.drawable.ic_failure_black);
                    mStatusTextView.setText(R.string.refresh_done_failure);
                }
                break;
            default:
                break;
        }

        mState = state;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void onNormal() {
        setState(STATE_NORMAL);
    }

    @Override
    public boolean onMove(float offSet) {
        Log.d("xyc", "mState:" + mState + ", sumOffset:" + sumOffset + ", offSet:" + offSet);
        if(mState >= STATE_REFRESHING) return false;

        sumOffset += offSet;

        if (null != mScrollAnimator && mScrollAnimator.isRunning()) {
            mScrollAnimator.end();
        }

        if (sumOffset >= 0) {

            setVisibleHeight((int)(sumOffset/DRAG_RATE));

            if (mState < STATE_REFRESHING) {
                if (getVisibleHeight() > mIntrinsicHeight) {
                    setState(STATE_PREPARE_TO_REFRESH);
                } else {
                    onNormal();
                }
            }
            return true;
        } else {
            sumOffset = 0;
            return false;
        }
    }

    @Override
    public boolean onRelease() {
        Log.d("xyc", "onRelease:");
        sumOffset = 0;
        boolean isRefreshing = false;

        if (mState == STATE_NORMAL) {
            smoothScrollTo(0);
        }

        if (mState == STATE_PREPARE_TO_REFRESH) {
            onRefreshing();
            isRefreshing = true;
        }

        if (mState == STATE_REFRESHING && getVisibleHeight() > mIntrinsicHeight) {
            smoothScrollTo(mIntrinsicHeight);
        }

        return isRefreshing;
    }

    @Override
    public void onRefreshing() {
        setState(STATE_REFRESHING);
    }

    @Override
    public void refreshComplete(boolean success) {
        isRefreshSucess = success;
        if (isRefreshSucess) {
            mPreRefreshTime = new Date();
        }
        setState(STATE_DONE);
        postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, DONE_SHOW_DURATION);
    }

    @Override
    public View getView() {
        return this;
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mRootLayout.getLayoutParams();
        lp.height = height;
        mRootLayout.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mRootLayout.getLayoutParams();
        return lp.height;
    }

    public void reset() {
        smoothScrollTo(0);
        postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, SCROLL_ANIM_DURATION);
    }

    private void smoothScrollTo(int destHeight) {
        if (null != mScrollAnimator && mScrollAnimator.isRunning()) {
            mScrollAnimator.end();
        }

        if (Math.abs(destHeight - getVisibleHeight()) <= mTouchSlop) {
            setVisibleHeight(destHeight);
            mScrollAnimator = null;
        } else {
            mScrollAnimator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setVisibleHeight((int) animation.getAnimatedValue());
                }
            });
            mScrollAnimator.setDuration(SCROLL_ANIM_DURATION);
            mScrollAnimator.start();
        }
    }

    public String friendlyTime(Date time) {
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);

        if (ct == 0) {
            return getContext().getResources().getString(R.string.text_just);
        }

        if (ct > 0 && ct < 60) {
            return ct + getContext().getResources().getString(R.string.text_seconds_ago);
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + getContext().getResources().getString(R.string.text_minute_ago);
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + getContext().getResources().getString(R.string.text_hour_ago);
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            int day = ct / 86400;
            return day + getContext().getResources().getString(R.string.text_day_ago);
        }
        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + getContext().getResources().getString(R.string.text_month_ago);
        }
        return ct / 31104000 + getContext().getResources().getString(R.string.text_year_ago);
    }

}