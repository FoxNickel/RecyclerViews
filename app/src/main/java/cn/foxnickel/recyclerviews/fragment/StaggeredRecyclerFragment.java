package cn.foxnickel.recyclerviews.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import cn.foxnickel.recyclerviews.R;
import cn.foxnickel.recyclerviews.adapter.StaggeredRecyclerAdapter;
import cn.foxnickel.recyclerviews.bean.Meizi;
import cn.foxnickel.recyclerviews.util.OkHttpUtil;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaggeredRecyclerFragment extends Fragment {

    private View mRootView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private StaggeredRecyclerAdapter mStaggeredRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Meizi> mMeiziList;
    private int mLastVisibleItem;//最后一个可见的item，用于滑动加载
    private int page = 1;//加载的数据页号
    private ItemTouchHelper mItemTouchHelper;

    public StaggeredRecyclerFragment() {
        // Required empty public constructor
    }

    public static StaggeredRecyclerFragment newInstance(){
        return new StaggeredRecyclerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_staggered_recycler, container, false);
        initView();
        setListener();
        new GetMeiziTask().execute("http://gank.io/api/data/福利/10/1");
        return mRootView;
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_staggered);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_staggered_recycler);
    }

    private void setListener() {
        /*下拉刷新*/
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetMeiziTask().execute("http://gank.io/api/data/福利/10/1");
            }
        });
        /*滑动加载*/
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem >= mStaggeredRecyclerAdapter.getItemCount()-2){
                    new GetMeiziTask().execute("http://gank.io/api/data/福利/10/"+(++page));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions= mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
                mLastVisibleItem = Math.max(positions[0],positions[1]);//根据StaggeredGridLayoutManager设置的列数来定
            }
        });
        /*触摸事件*/
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags=0,swipeFlags=0;
                if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager ||recyclerView.getLayoutManager() instanceof GridLayoutManager){
                    //网格式布局有4个方向
                    dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                    //线性式布局有2个方向
                    dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;

                    swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END; //设置侧滑方向为从两个方向都可以
                }
                return makeMovementFlags(dragFlags,swipeFlags);//swipeFlags 为0的话item不滑动
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(mMeiziList,from,to);
                mStaggeredRecyclerAdapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
    }

    private class GetMeiziTask extends AsyncTask<String,Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return OkHttpUtil.getJsonFromServer(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s)){
                try {
                    /*解析json数据*/
                    JSONObject jsonObject = new JSONObject(s);
                    String results = jsonObject.getString("results");
                    Log.i(TAG, "onPostExecute: results "+results);
                    Gson gson = new Gson();

                    if(mMeiziList==null||mMeiziList.size()==0){
                        mMeiziList= gson.fromJson(results, new TypeToken<List<Meizi>>() {}.getType());
                    }else{
                        List<Meizi> more= gson.fromJson(results, new TypeToken<List<Meizi>>() {}.getType());
                        mMeiziList.addAll(more);
                    }

                    if(mStaggeredRecyclerAdapter==null){
                        mRecyclerView.setAdapter(mStaggeredRecyclerAdapter = new StaggeredRecyclerAdapter(getContext(),mMeiziList));
                        mItemTouchHelper.attachToRecyclerView(mRecyclerView);//触摸事件关联到RecyclerView
                    }else{
                        mStaggeredRecyclerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);//获取到了数据，取消刷新按钮
        }
    }
}
