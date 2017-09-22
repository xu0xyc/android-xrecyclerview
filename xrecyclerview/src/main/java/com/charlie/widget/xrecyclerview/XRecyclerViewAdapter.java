package com.charlie.widget.xrecyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.charlie.widget.xrecyclerview.enumer.LoadMoreStyle;
import com.charlie.widget.xrecyclerview.enumer.PullRefreshStyle;

import java.util.List;

/**
 * XRecyclerView内置的Adapter
 * Created by Charlie on 2017/9/12.
 */
/* Package */ class XRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = XRecyclerViewAdapter.class.getSimpleName();

    public static final int ITEM_TYPE_PULL_REFRESH = 9991;
    public static final int ITEM_TYPE_HEADER = 9992;
    public static final int ITEM_TYPE_FOOTER = 9993;
    public static final int ITEM_TYPE_LOAD_MORE = 9994;
    public static final int ITEM_TYPE_EMPTY = 9995;

    private RecyclerView.Adapter mInnerAdapter;
    private XRecyclerView mXRecyclerView;

    public XRecyclerViewAdapter(RecyclerView.Adapter adapter, XRecyclerView recyclerView) {
        mInnerAdapter = adapter;
        mXRecyclerView = recyclerView;
        if (mInnerAdapter.hasStableIds()) {
            setHasStableIds(true);
        }
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    public int getDefaultOccupiedHeadCount() {
        return (isHavePullRefresh() ? 1 : 0) + (isHaveHeader() ? 1 : 0) + (mXRecyclerView.isShowDataEmpty ? 1 : 0);
    }

    public int getDefaultOccupiedFootCount() {
        return (isHaveFooter() ? 1 : 0) + (isHaveLoadMore() ? 1 : 0);
    }

    public boolean isHavePullRefresh() {
        return PullRefreshStyle.NULL!=mXRecyclerView.getPullRefreshStyle() && null!=mXRecyclerView.getIPullRefresh() && !mXRecyclerView.isHidePullRefresh;
    }

    public boolean isHaveHeader() {
        return null!=mXRecyclerView.getHeaderView();
    }

    public boolean isHaveFooter() {
        return null!=mXRecyclerView.getFooterView();
    }

    public boolean isHaveLoadMore() {
        return LoadMoreStyle.NULL!=mXRecyclerView.getLoadMoreStyle() && null!=mXRecyclerView.getILoadMore() && !mXRecyclerView.isHideLoadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder-->viewType:" + viewType);
        switch (viewType) {
            case ITEM_TYPE_PULL_REFRESH:
                return new ViewHolder(mXRecyclerView.getIPullRefresh().getView());
            case ITEM_TYPE_HEADER:
                return new ViewHolder(mXRecyclerView.getHeaderView());
            case ITEM_TYPE_EMPTY:
                return new ViewHolder(mXRecyclerView.getDataEmptyView());
            case ITEM_TYPE_FOOTER:
                return new ViewHolder(mXRecyclerView.getFooterView());
            case ITEM_TYPE_LOAD_MORE:
                return new ViewHolder(mXRecyclerView.getILoadMore().getView());
            default:
                return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder1-->position:" + position);
        int itemViewType = getItemViewType(position);
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            final int innerPosition = position - getDefaultOccupiedHeadCount();
            mInnerAdapter.onBindViewHolder(holder, innerPosition);
            if (mXRecyclerView.getOnItemClickListener() != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener()  {
                    @Override
                    public void onClick(View v)
                    {
                        mXRecyclerView.getOnItemClickListener().onItemClick(innerPosition, holder.itemView, mXRecyclerView);
                    }
                });
            }

            if (mXRecyclerView.getOnItemLongClickListener() != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        mXRecyclerView.getOnItemLongClickListener().onItemLongClick(innerPosition, holder.itemView, mXRecyclerView);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, List payloads) {
//        Log.d(TAG, "onBindViewHolder2-->position:" + position);
        int itemViewType = getItemViewType(position);
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            final int innerPosition = position - getDefaultOccupiedHeadCount();
            mInnerAdapter.onBindViewHolder(holder, innerPosition, payloads);
            if (mXRecyclerView.getOnItemClickListener() != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener()  {
                    @Override
                    public void onClick(View v)
                    {
                        mXRecyclerView.getOnItemClickListener().onItemClick(innerPosition, holder.itemView, mXRecyclerView);
                    }
                });
            }

            if (mXRecyclerView.getOnItemLongClickListener() != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        mXRecyclerView.getOnItemLongClickListener().onItemLongClick(innerPosition, holder.itemView, mXRecyclerView);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mInnerAdapter.getItemCount();
        itemCount += getDefaultOccupiedHeadCount();
        itemCount += getDefaultOccupiedFootCount();

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHavePullRefresh()) {
            if(0 == position) return ITEM_TYPE_PULL_REFRESH;

            if (isHaveHeader()) {
                if (1 == position) {
                    return ITEM_TYPE_HEADER;
                } else if (2 == position && mXRecyclerView.isShowDataEmpty) {
                    return ITEM_TYPE_EMPTY;
                }
            } else {
                if (1 == position && mXRecyclerView.isShowDataEmpty) {
                    return ITEM_TYPE_EMPTY;
                }

            }
        } else {
            if (isHaveHeader()) {
                if (0 == position) {
                    return ITEM_TYPE_HEADER;
                } else if (1 == position && mXRecyclerView.isShowDataEmpty) {
                    return ITEM_TYPE_EMPTY;
                }
            } else {
                if (0 == position && mXRecyclerView.isShowDataEmpty) {
                    return ITEM_TYPE_EMPTY;
                }
            }
        }

        if (isHaveLoadMore()) {
            if (getItemCount() - 1 == position) {
                return ITEM_TYPE_LOAD_MORE;
            } else if (getItemCount() - 2 == position && isHaveFooter()) {
                return ITEM_TYPE_FOOTER;
            }
        } else {
            if (getItemCount() - 1 == position && isHaveFooter()) {
                return ITEM_TYPE_FOOTER;
            }
        }

        int innerPosition = position - getDefaultOccupiedHeadCount();
        return mInnerAdapter.getItemViewType(innerPosition);
    }

    @Override
    public long getItemId(int position) {
        if (!hasStableIds()) {
            return RecyclerView.NO_ID;
        }

        int itemViewType = getItemViewType(position);
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            int innerPosition = position - getDefaultOccupiedHeadCount();
            return mInnerAdapter.getItemId(innerPosition);
        } else {
            return itemViewType;
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            mInnerAdapter.onViewRecycled(holder);
        } else {
            super.onViewRecycled(holder);
        }
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        int itemViewType = holder.getItemViewType();
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            return mInnerAdapter.onFailedToRecycleView(holder);
        } else {
            return super.onFailedToRecycleView(holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
//        Log.d(TAG, "onViewAttachedToWindow");
        int itemViewType = holder.getItemViewType();
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            mInnerAdapter.onViewAttachedToWindow(holder);
        } else {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            super.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
//        Log.d(TAG, "onViewDetachedFromWindow");
        int itemViewType = holder.getItemViewType();
        if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
            mInnerAdapter.onViewDetachedFromWindow(holder);
        } else {
            super.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        Log.d(TAG, "onAttachedToRecyclerView");
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            final GridLayoutManager.SpanSizeLookup innerSpanSizeLookup = gridManager.getSpanSizeLookup();
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemViewType = getItemViewType(position);
                    if (ITEM_TYPE_PULL_REFRESH != itemViewType && ITEM_TYPE_HEADER != itemViewType && ITEM_TYPE_EMPTY != itemViewType && ITEM_TYPE_FOOTER != itemViewType && ITEM_TYPE_LOAD_MORE != itemViewType) {
                        int innerPosition = position - getDefaultOccupiedHeadCount();
                        return innerSpanSizeLookup.getSpanSize(innerPosition);
                    } else {
                        return gridManager.getSpanCount();
                    }
                }
            });
        }
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
//        Log.d(TAG, "onDetachedFromRecyclerView");
        mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
