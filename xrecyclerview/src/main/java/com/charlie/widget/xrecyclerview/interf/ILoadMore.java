package com.charlie.widget.xrecyclerview.interf;

import android.view.View;

public interface ILoadMore {

    int STATE_NORMAL = 0;
    int STATE_LOADING = 1;
    int STATE_ERROR = 2;
    int STATE_NOMORE = 3;

    /**
     *  获取当前的状态
     */
    int getState();

    /**
     * 状态回调，回复初始设置
     */
    void onNormal();

    /**
     * 状态回调，加载中
     */
    void onLoading();

    /**
     * 状态回调，加载失败
     */
    void onError();

    /**
     * 状态回调，已全部加载完成
     */
    void onNoMore();

    /**
     * 获取View
     */
    View getView();

}
