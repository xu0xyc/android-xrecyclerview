package com.charlie.widget.xrecyclerview.interf;

import android.view.View;

public interface IPullRefresh {

	int STATE_NORMAL = 0;
	int STATE_PREPARE_TO_REFRESH = 1;
	int STATE_REFRESHING = 2;
	int STATE_DONE = 3;

	/**
	 * 获取当前的状态
     */
	int getState();

	/**
	 * 默认
	 */
	void onNormal();

	/**
	 * 下拉移动
	 */
	boolean onMove(float offSet);

	/**
	 * 下拉松开
	 */
	boolean onRelease();

	/**
	 * 正在刷新
	 */
	void onRefreshing();

	/**
	 * 下拉刷新完成
	 */
	void refreshComplete(boolean success);

	/**
	 * 获取View
	 */
	View getView();

}