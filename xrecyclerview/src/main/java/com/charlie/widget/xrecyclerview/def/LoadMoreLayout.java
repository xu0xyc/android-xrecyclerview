package com.charlie.widget.xrecyclerview.def;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.charlie.widget.xrecyclerview.R;
import com.charlie.widget.xrecyclerview.enumer.ProgressStyle;
import com.charlie.widget.xrecyclerview.interf.ILoadMore;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;

public class LoadMoreLayout extends RelativeLayout implements ILoadMore {

    private View mLoadingLayout;
    private View mErrorLayout;
    private View mNoMoreLayout;

    private AVLoadingIndicatorView mProgressView;
    private TextView mLoadingText;
    private TextView mErrorText;
    private TextView mNoMoreText;

    private String mLoadingHint;
    private String mNoMoreHint;
    private String mErrorHint;

    private ProgressStyle mProgressStyle;
    private int mProgressColorResId;
    private int mHintColorResId;

    private int mState = STATE_NORMAL;

    public LoadMoreLayout(Context context) {
        this(context, null);
    }

    public LoadMoreLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void initialize(Context context) {
        inflate(context, R.layout.layout_recyclerview_footer, this);

        mHintColorResId = R.color.default_load_more_status_hint_color;
        mProgressColorResId = R.color.default_load_more_progress_color;
        mProgressStyle = ProgressStyle.BallPulse;
        onNormal();

    }

    public void setLoadingHint(String hint) {
        this.mLoadingHint = hint;
    }

    public void setNoMoreHint(String hint) {
        this.mNoMoreHint = hint;
    }

    public void setErrorHint(String hint) {
        this.mErrorHint = hint;
    }

    public void setHintColor(int colorResId) {
        this.mHintColorResId = colorResId;
    }

    public void setProgressColor(int colorResId) {
        this.mProgressColorResId = colorResId;
    }

    public void setProgressStyle(ProgressStyle style) {
        this.mProgressStyle = style;
    }

    public void setViewBackgroundColor(int colorResId) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));
    }

    @Override
    public void onNormal() {
        setState(STATE_NORMAL);
    }

    @Override
    public void onLoading() {
        setState(STATE_LOADING);
    }

    @Override
    public void onError() {
        setState(STATE_ERROR);
    }

    @Override
    public void onNoMore() {
        setState(STATE_NOMORE);
    }

    @Override
    public View getView() {
        return this;
    }

    public void setState(int status) {
        if (mState == status) {
            return;
        }

        switch (status) {
            case STATE_NORMAL:
                setOnClickListener(null);
                if (mLoadingLayout != null) {
                    mLoadingLayout.setVisibility(GONE);
                }

                if (mNoMoreLayout != null) {
                    mNoMoreLayout.setVisibility(GONE);
                }

                if (mErrorLayout != null) {
                    mErrorLayout.setVisibility(GONE);
                }
                break;
            case STATE_LOADING:
                setOnClickListener(null);
                if (mNoMoreLayout != null) {
                    mNoMoreLayout.setVisibility(GONE);
                }

                if (mErrorLayout != null) {
                    mErrorLayout.setVisibility(GONE);
                }

                if (mLoadingLayout == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_viewstub);
                    mLoadingLayout = viewStub.inflate();

                    mProgressView = (AVLoadingIndicatorView) mLoadingLayout.findViewById(R.id.loading_progressbar);
                    mLoadingText = (TextView) mLoadingLayout.findViewById(R.id.loading_text);
                } else {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                }

                mProgressView.setIndicatorColor(ContextCompat.getColor(getContext(), mProgressColorResId));
                // 处理mProgressView.setIndicator(Indicator)时，会失去原来的宽高的问题
                Indicator indicator = mProgressView.getIndicator();
                if (!indicator.getClass().getSimpleName().equalsIgnoreCase(mProgressStyle.value)) {
                    mProgressView.setIndicator(mProgressStyle.value);
                    if (mProgressView.getWidth() > 0 && mProgressView.getHeight() > 0) {
                        mProgressView.getIndicator().setBounds(0, 0, mProgressView.getWidth(), mProgressView.getHeight());
                    }
                }
                mLoadingText.setText(TextUtils.isEmpty(mLoadingHint) ? getResources().getString(R.string.list_footer_loading) : mLoadingHint);
                mLoadingText.setTextColor(ContextCompat.getColor(getContext(), mHintColorResId));
                break;
            case STATE_ERROR:
                if (mLoadingLayout != null) {
                    mLoadingLayout.setVisibility(GONE);
                }

                if (mNoMoreLayout != null) {
                    mNoMoreLayout.setVisibility(GONE);
                }

                if (mErrorLayout == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.network_error_viewstub);
                    mErrorLayout = viewStub.inflate();
                    mErrorText = (TextView) mErrorLayout.findViewById(R.id.network_error_text);
                } else {
                    mErrorLayout.setVisibility(View.VISIBLE);
                }

                mErrorText.setText(TextUtils.isEmpty(mErrorHint) ? getResources().getString(R.string.list_footer_network_error) : mErrorHint);
                mErrorText.setTextColor(ContextCompat.getColor(getContext(), mHintColorResId));
                break;
            case STATE_NOMORE:
                setOnClickListener(null);
                if (mLoadingLayout != null) {
                    mLoadingLayout.setVisibility(GONE);
                }

                if (mErrorLayout != null) {
                    mErrorLayout.setVisibility(GONE);
                }

                if (mNoMoreLayout == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
                    mNoMoreLayout = viewStub.inflate();

                    mNoMoreText = (TextView) mNoMoreLayout.findViewById(R.id.loading_end_text);
                } else {
                    mNoMoreLayout.setVisibility(View.VISIBLE);
                }

                mNoMoreText.setText(TextUtils.isEmpty(mNoMoreHint) ? getResources().getString(R.string.list_footer_end) : mNoMoreHint);
                mNoMoreText.setTextColor(ContextCompat.getColor(getContext(), mHintColorResId));
                break;
            default:
                break;
        }

        mState = status;
    }

    @Override
    public int getState() {
        return mState;
    }
}