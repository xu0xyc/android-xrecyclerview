package com.charlie.widget.sample;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.charlie.widget.xrecyclerview.XRecyclerView;
import com.charlie.widget.xrecyclerview.enumer.DataEmptyStyle;
import com.charlie.widget.xrecyclerview.enumer.LoadMoreStyle;
import com.charlie.widget.xrecyclerview.enumer.PullRefreshStyle;
import com.charlie.widget.xrecyclerview.interf.OnItemClickListener;
import com.charlie.widget.xrecyclerview.interf.OnItemLongClickListener;
import com.charlie.widget.xrecyclerview.interf.OnLoadMoreListener;
import com.charlie.widget.xrecyclerview.interf.OnPullRefreshListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener, OnPullRefreshListener, OnLoadMoreListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private XRecyclerView xRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Handler mHandler = new Handler();
    private List<String> mDatas;
    private boolean isNeedFirstRefresh = true;// 是否需要首次进入刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xRecyclerView = (XRecyclerView) findViewById(R.id.xRecyclerView);

        mDatas = new ArrayList<>();

        mDatas.add("我就是我。");
        mDatas.add("我就是我。");
        mDatas.add("你就是你。");
        mDatas.add("你就是你。");
        mDatas.add("我就是我。");
        mDatas.add("世界是我们的，也是你们的，但归根结底，还是你们的！");

        mAdapter = new CommonAdapter<String>(getApplicationContext(), R.layout.item_rv_simple, mDatas) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.tv_title, s);
            }
        };
        xRecyclerView.setAdapter(mAdapter);

        xRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 5;
//                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        xRecyclerView.setOnItemClickListener(this);
        xRecyclerView.setOnItemLongClickListener(this);

        xRecyclerView.setHeaderView(R.layout.header_rv_simple);
        xRecyclerView.setFooterView(R.layout.footer_rv_simple);

        xRecyclerView.setPullRefreshStyle(PullRefreshStyle.DEFAULT, null);
        xRecyclerView.setOnPullRefreshListener(this);
        xRecyclerView.setLoadMoreStyle(LoadMoreStyle.DEFAULT, null);
        xRecyclerView.setOnLoadMoreListener(this);
        xRecyclerView.setDataEmptyStyle(DataEmptyStyle.ALL);
        xRecyclerView.setDataEmptyView(R.layout.layout_empty);

    }

    @Override
    public void onItemClick(int position, View view, ViewGroup parent) {
        Log.d(TAG, "onItemClick:"+position);

    }

    @Override
    public void onItemLongClick(int position, View view, ViewGroup parent) {
        Log.d(TAG, "onItemLongClick:"+position);
    }

    @Override
    public void onPullRefresh() {
        Log.d(TAG, "onPullRefresh");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xRecyclerView.stopRefreshing(true);

                mDatas.clear();

                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("你就是你。");
                mDatas.add("你就是你。");
                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("我就是我。");
                mDatas.add("你就是你。");
                mDatas.add("你就是你。");
                mDatas.add("世界是我们的，也是你们的，但归根结底，还是你们的！");

                xRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatas.add("shfiahfiafi");
                xRecyclerView.getAdapter().notifyItemInserted(xRecyclerView.getAdapter().getItemCount());

//                xRecyclerView.setIsNoMore(true);
//                xRecyclerView.setIsLoadMoreError(true);

            }
        }, 3000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(false);
        if (isNeedFirstRefresh && hasFocus) {
            isNeedFirstRefresh = false;
            xRecyclerView.startRefreshing();
        }
    }
}
