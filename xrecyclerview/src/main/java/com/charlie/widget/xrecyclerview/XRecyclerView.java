package com.charlie.widget.xrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import com.charlie.widget.xrecyclerview.def.ArrowRefreshLayout;
import com.charlie.widget.xrecyclerview.def.LoadMoreLayout;
import com.charlie.widget.xrecyclerview.design.AppBarStateChangeListener;
import com.charlie.widget.xrecyclerview.enumer.DataEmptyStyle;
import com.charlie.widget.xrecyclerview.enumer.LoadMoreStyle;
import com.charlie.widget.xrecyclerview.enumer.PullRefreshStyle;
import com.charlie.widget.xrecyclerview.interf.ILoadMore;
import com.charlie.widget.xrecyclerview.interf.IPullRefresh;
import com.charlie.widget.xrecyclerview.interf.OnItemClickListener;
import com.charlie.widget.xrecyclerview.interf.OnItemLongClickListener;
import com.charlie.widget.xrecyclerview.interf.OnLoadMoreListener;
import com.charlie.widget.xrecyclerview.interf.OnPullRefreshListener;

/**
 * 扩展的RecyclerView,支持：
 * 1、下拉刷新
 * 2、添加HeaderView
 * 3、自定义数据为空时View
 * 4、添加FooterView
 * 5、上拉加载
 * 6、Item点击和长按事件回调
 * Created by Charlie on 2017/9/12.
 */
public class XRecyclerView extends RecyclerView {
    private static final String TAG = XRecyclerView.class.getSimpleName();

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private PullRefreshStyle mPullRefreshStyle = PullRefreshStyle.NULL;
    private IPullRefresh mIPullRefresh;
    private OnPullRefreshListener mOnPullRefreshListener;
    private LoadMoreStyle mLoadMoreStyle = LoadMoreStyle.NULL;
    private ILoadMore mILoadMore;
    private OnLoadMoreListener mOnLoadMoreListener;
    private DataEmptyStyle mDataEmptyStyle = DataEmptyStyle.ALL;
    private View mDataEmptyView;

    private View mHeaderView;
    private View mFooterView;

    private XRecyclerViewAdapter mXAdapter;
    private int mFirstVisiableItem;
    private int mLastVisiableItem;
    private int mLastCompletelyVisiableItem;
    private boolean isNoMore;
    private boolean isLoadMoreError;

    private int mTouchSlop;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mIsHorDragging;
    private float mLastX;
    private float mLastY;
    private float mMoveDy;
    private float scrollDy;
    private boolean isPulling;

    private boolean isAttachedWindow;
    private ItemDecoration mItemDecoration;

    boolean isHidePullRefresh;// 是否隐藏下拉刷新
    boolean isHideLoadMore;// 是否隐藏上拉加载
    boolean isShowDataEmpty;// 是否显示数据为空

    private AdapterDataObserver mDataObserver = new InnerAdapterDataObserver();
    private AppBarStateChangeListener.State mAppBarState = AppBarStateChangeListener.State.EXPANDED;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        initialize();
    }

    private void initialize() {
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public void setOnPullRefreshListener(OnPullRefreshListener listener) {
        mOnPullRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setPullRefreshStyle(PullRefreshStyle style, IPullRefresh iPullRefresh) {
        mPullRefreshStyle = style;
        if (PullRefreshStyle.NULL == mPullRefreshStyle) {
            mIPullRefresh = null;
        } else if (PullRefreshStyle.DEFAULT == mPullRefreshStyle) {
            mIPullRefresh = new ArrowRefreshLayout(getContext());
        } else {
            mIPullRefresh = iPullRefresh;
        }
    }

    public void setLoadMoreStyle(LoadMoreStyle style, ILoadMore iLoadMore) {
        mLoadMoreStyle = style;
        if (LoadMoreStyle.NULL == mLoadMoreStyle) {
            mILoadMore = null;
        } else if (LoadMoreStyle.DEFAULT == mLoadMoreStyle) {
            mILoadMore = new LoadMoreLayout(getContext());
        } else {
            mILoadMore = iLoadMore;
        }
    }

    public void setDataEmptyStyle(DataEmptyStyle style) {
        mDataEmptyStyle = style;
    }

    public void setDataEmptyView(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mDataEmptyView = inflater.inflate(layoutId, this, false);
    }

    public void setDataEmptyView(View view) {
        mDataEmptyView = view;
    }

    public void setHeaderView(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mHeaderView = inflater.inflate(layoutId, this, false);
    }

    public void setHeaderView(View view) {
        mHeaderView = view;
    }

    public void setFooterView(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mFooterView = inflater.inflate(layoutId, this, false);
    }

    public void setFooterView(View view) {
        mFooterView = view;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public PullRefreshStyle getPullRefreshStyle() {
        return mPullRefreshStyle;
    }

    public IPullRefresh getIPullRefresh() {
        return mIPullRefresh;
    }

    public OnPullRefreshListener getOnPullRefreshListener() {
        return mOnPullRefreshListener;
    }

    public LoadMoreStyle getLoadMoreStyle() {
        return mLoadMoreStyle;
    }

    public ILoadMore getILoadMore() {
        return mILoadMore;
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return mOnLoadMoreListener;
    }

    public DataEmptyStyle getDataEmptyStyle() {
        return mDataEmptyStyle;
    }

    public View getDataEmptyView() {
        if (null == mDataEmptyView) {
            mDataEmptyView = new View(mContext);
            mDataEmptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        return mDataEmptyView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setIsNoMore(boolean noMore) {
        isNoMore = noMore;
        if (isAttachedWindow) {
            checkLoadMore();
        }
    }

    public void setIsLoadMoreError(boolean loadMoreError) {
        isLoadMoreError = loadMoreError;
        if (isAttachedWindow) {
            checkLoadMore();
        }
    }

    public void startRefreshing() {
        if (isAttachedWindow && mXAdapter.isHavePullRefresh() && mIPullRefresh.getState() < IPullRefresh.STATE_REFRESHING) {
            mIPullRefresh.onRefreshing();
            isNoMore = false;
            isLoadMoreError = false;
            if (null != mOnPullRefreshListener) {
                mOnPullRefreshListener.onPullRefresh();
            }
        }
    }

    public void stopRefreshing(boolean success) {
        if (isAttachedWindow && mXAdapter.isHavePullRefresh() && mIPullRefresh.getState() == IPullRefresh.STATE_REFRESHING) {
            mIPullRefresh.refreshComplete(success);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (null != mXAdapter) {
            mXAdapter.getInnerAdapter().unregisterAdapterDataObserver(mDataObserver);
        }

        adapter.registerAdapterDataObserver(mDataObserver);
        mXAdapter = new XRecyclerViewAdapter(adapter, this);
        super.setAdapter(mXAdapter);
    }

    private class InnerAdapterDataObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
//            Log.d(TAG, "onChanged");
            mXAdapter.notifyDataSetChanged();
            checkLoadMore();
            checkDataEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
//            Log.d(TAG, "onItemRangeChanged-->positionStart:" + positionStart + ", itemCount:" + itemCount);
            mXAdapter.notifyItemRangeChanged(positionStart + mXAdapter.getDefaultOccupiedHeadCount(), itemCount);
            checkLoadMore();
            checkDataEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
//            Log.d(TAG, "onItemRangeInserted-->positionStart:" + positionStart + ", itemCount:" + itemCount);
            mXAdapter.notifyItemRangeInserted(positionStart + mXAdapter.getDefaultOccupiedHeadCount(), itemCount);
            checkLoadMore();
            checkDataEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
//            Log.d(TAG, "onItemRangeRemoved-->positionStart:" + positionStart + ", itemCount:" + itemCount);
            mXAdapter.notifyItemRangeRemoved(positionStart + mXAdapter.getDefaultOccupiedHeadCount(), itemCount);
            checkLoadMore();
            checkDataEmpty();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
//            Log.d(TAG, "onItemRangeMoved-->fromPosition:" + fromPosition + ", toPosition:" + toPosition);
            int occupiedHeadCount = mXAdapter.getDefaultOccupiedHeadCount();
            mXAdapter.notifyItemRangeChanged(fromPosition + occupiedHeadCount, toPosition + occupiedHeadCount + itemCount);
            checkLoadMore();
            checkDataEmpty();
        }

    }

    private void checkDataEmpty() {
        if (isNotEmpty()) {
            if (isShowDataEmpty) {
                isShowDataEmpty = false;
                mXAdapter.notifyDataSetChanged();
            }
        } else {
            if (!isShowDataEmpty) {
                isShowDataEmpty = true;
                mXAdapter.notifyDataSetChanged();
            }
        }
    }

    private void checkLoadMore() {
        if (mXAdapter.isHaveLoadMore()) {
            if (isNoMore) {
                if (ILoadMore.STATE_NOMORE != mILoadMore.getState()) mILoadMore.onNoMore();
            } else if (isLoadMoreError) {
                if (ILoadMore.STATE_ERROR != mILoadMore.getState()) {
                    mILoadMore.onError();
                    if (null != mOnLoadMoreListener) {
                        mILoadMore.getView().setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ILoadMore.STATE_LOADING != mILoadMore.getState()) {
                                    mILoadMore.onLoading();
                                    mOnLoadMoreListener.onLoadMore();
                                }
                            }
                        });
                    }
                }
            } else {
                if (!isHideLoadMore) {
                    isHideLoadMore = true;
                    mXAdapter.notifyItemRemoved(mXAdapter.getItemCount() - 1);
                    if (ILoadMore.STATE_NORMAL != mILoadMore.getState()) mILoadMore.onNormal();
                }
            }
        }
    }

    private boolean isNotEmpty() {
        int itemCount = mXAdapter.getInnerAdapter().getItemCount();
        if (itemCount > 0) return true;

        if (DataEmptyStyle.ALL == mDataEmptyStyle) {

            return mXAdapter.isHaveHeader() || mXAdapter.isHaveFooter();
        } else if (DataEmptyStyle.BODY == mDataEmptyStyle) {

            return false;
        } else {

            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouchX = mLastX = ev.getX();
                mInitialTouchY = mLastY = ev.getY();

                mIsHorDragging = false;
                isPulling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsHorDragging) {
                    return false;
                }

                float moveX = ev.getX();
                float moveY = ev.getY();
                float distanceX = Math.abs(moveX - mInitialTouchX);
                float distanceY = Math.abs(moveY - mInitialTouchY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsHorDragging = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsHorDragging = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouchX = mLastX = ev.getX();
                mInitialTouchY = mLastY = ev.getY();

                isPulling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveDy = ev.getY() - mLastY;
                mLastY = ev.getY();
                if (isHaveAndTopPullRefresh() && !isLoadingMore() && (mAppBarState == AppBarStateChangeListener.State.EXPANDED)) {
                    if (mIPullRefresh.onMove(mMoveDy)) {
                        isPulling = true;
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isPulling) {
                    if (mIPullRefresh.onRelease() && null != mOnPullRefreshListener) {
                        isNoMore = false;
                        isLoadMoreError = false;
                        mOnPullRefreshListener.onPullRefresh();
                    }
                }
                isPulling = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        scrollDy = dy;

        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mFirstVisiableItem = linearLayoutManager.findFirstVisibleItemPosition();
            mLastVisiableItem = linearLayoutManager.findLastVisibleItemPosition();
            mLastCompletelyVisiableItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            mFirstVisiableItem = gridLayoutManager.findFirstVisibleItemPosition();
            mLastVisiableItem = gridLayoutManager.findLastVisibleItemPosition();
            mLastCompletelyVisiableItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] firstVisibleItemPositions = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
            mFirstVisiableItem = getMinPostion(firstVisibleItemPositions);
            int[] lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            mLastVisiableItem = getMaxPostion(lastVisibleItemPositions);
            int[] lastCompletelyVisibleItemPositions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
            mLastCompletelyVisiableItem = getMaxPostion(lastCompletelyVisibleItemPositions);

        } else {
            throw new RuntimeException("Not support the type of LayoutManager!");
        }

        if (scrollDy > 0) {
            if (isHideLoadMore && !isRefreshing()) {
                isHideLoadMore = false;
                mXAdapter.notifyItemInserted(mXAdapter.getItemCount());
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mXAdapter.isHaveLoadMore() && !isNoMore && !isLoadMoreError
                    && !isRefreshing() && (scrollDy > 0)) {

                int totalItemCount = getLayoutManager().getItemCount();
                if (mLastVisiableItem >= totalItemCount - 1) {
                    if (ILoadMore.STATE_LOADING != mILoadMore.getState()) {
                        mILoadMore.onLoading();
                        if (null != mOnLoadMoreListener) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                }
            }

            scrollDy = 0;
        }
    }

    public boolean isHaveAndTopPullRefresh() {
        return mXAdapter.isHavePullRefresh() && null != mIPullRefresh.getView().getParent();
    }

    public boolean isRefreshing() {
        return mXAdapter.isHavePullRefresh() && mIPullRefresh.getState() >= IPullRefresh.STATE_REFRESHING;
    }

    public boolean isLoadingMore() {
        return mXAdapter.isHaveLoadMore() && ILoadMore.STATE_LOADING == mILoadMore.getState();
    }

    public int getMinPostion(int[] positions) {
        int minPositon = positions[0];
        for (int p : positions) {
            if (p < minPositon) {
                minPositon = p;
            }
        }

        return minPositon;
    }

    public int getMaxPostion(int[] positions) {
        int maxPositon = positions[0];
        for (int p : positions) {
            if (p > maxPositon) {
                maxPositon = p;
            }
        }

        return maxPositon;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkLoadMore();
        checkDataEmpty();

        //解决XRecyclerView与CollapsingToolbarLayout滑动冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        mAppBarState = state;
                    }
                });
            }
        }

        isAttachedWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedWindow = false;
    }

    @Override
    public Adapter getAdapter() {
        return mXAdapter.getInnerAdapter();
    }


    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);

    }

    @Override
    public void addItemDecoration(final ItemDecoration decor) {
        if(null == decor) return;

        mItemDecoration = new ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                int itemViewType = parent.getChildViewHolder(view).getItemViewType();
                if (XRecyclerViewAdapter.ITEM_TYPE_PULL_REFRESH != itemViewType && XRecyclerViewAdapter.ITEM_TYPE_HEADER != itemViewType && XRecyclerViewAdapter.ITEM_TYPE_EMPTY != itemViewType && XRecyclerViewAdapter.ITEM_TYPE_FOOTER != itemViewType && XRecyclerViewAdapter.ITEM_TYPE_LOAD_MORE != itemViewType) {
                    decor.getItemOffsets(outRect, view, parent, state);
                }
            }
        };

        super.addItemDecoration(mItemDecoration);
    }
}
