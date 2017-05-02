package cn.foxnickel.recyclerviews.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import cn.foxnickel.recyclerviews.adapter.LinearRecyclerAdapter;
import cn.foxnickel.recyclerviews.bean.Meizi;
import cn.foxnickel.recyclerviews.util.OkHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinearRecyclerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinearRecyclerFragment extends Fragment {

    private static final String TAG = "LinearRecyclerFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LinearRecyclerAdapter mLinearRecyclerAdapter;
    private List<Meizi> mMeiziList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int lastVisibleItem;
    private int page = 1;
    private ItemTouchHelper mItemTouchHelper;

    public LinearRecyclerFragment() {
        Log.i(TAG, "LinearRecyclerFragment: ");
    }

    public static LinearRecyclerFragment newInstance() {
        Log.i(TAG, "newInstance: ");
        LinearRecyclerFragment fragment = new LinearRecyclerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mRootView = inflater.inflate(R.layout.fragment_linear_recycler, container, false);
        initView();
        setListener();
        new GetMeiziTask().execute("http://gank.io/api/data/福利/10/1");
        return mRootView;
    }

    /*初始化布局*/
    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_linear);//初始化recyclerview
        mLayoutManager = new LinearLayoutManager(getContext());//初始化布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);//设置布局管理器

        /*初始化下拉刷新控件*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_linear_recycler);
        //mSwipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));//调整SwipeRefreshLayout的位置
    }

    private void setListener() {

        /*设置item加载或移除时的动画*/
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /*swipeRefreshLayout刷新监听*/
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetMeiziTask().execute("http://gank.io/api/data/福利/10/1 ");
            }
        });

        /*item触摸助手类，用于实现滑动删除等操作*/
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            //用于设置拖拽和滑动的方向
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

            //长摁item拖拽时会回调这个方法
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                /*int from=viewHolder.getAdapterPosition();
                int to=target.getAdapterPosition();
                Meizi moveItem=mMeiziList.get(from);
                mMeiziList.remove(from);
                mMeiziList.add(to,moveItem);//交换数据链表中数据的位置
                mLinearRecyclerAdapter.notifyItemMoved(from,to);//更新适配器中item的位置*/
                int from=viewHolder.getAdapterPosition();
                int to=target.getAdapterPosition();
                Collections.swap(mMeiziList,from,to);
                mLinearRecyclerAdapter.notifyItemMoved(from,to);
                return true;
            }

            /*滑动删除*/
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mLinearRecyclerAdapter.removeItem(viewHolder.getAdapterPosition());
            }
        });

        /*下拉加载更多*/
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*
                    0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                    滑动状态停止并且剩余两个item时自动加载
                */
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem +2>=mLayoutManager.getItemCount()) {
                    new GetMeiziTask().execute("http://gank.io/api/data/福利/10/"+(++page));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    /*从网络获取妹子数据*/
    private class GetMeiziTask extends AsyncTask<String,Integer,String>{

        /*后台方法开始之前的一系列准备操作*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);//显示刷新按钮
        }

        @Override
        protected String doInBackground(String... params) {
            return OkHttpUtil.getJsonFromServer(params[0]);
        }

        /*在主线程运行，网络数据加载完毕之后进行的操作（如更新视图等）*/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*获取到的json不为空的时候进行解析*/
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

                    if(mLinearRecyclerAdapter==null){
                        mRecyclerView.setAdapter(mLinearRecyclerAdapter = new LinearRecyclerAdapter(getContext(),mMeiziList));
                        mItemTouchHelper.attachToRecyclerView(mRecyclerView);//触摸事件关联到RecyclerView
                    }else{
                        mLinearRecyclerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);//获取到了数据，取消刷新按钮
        }
    }

}
