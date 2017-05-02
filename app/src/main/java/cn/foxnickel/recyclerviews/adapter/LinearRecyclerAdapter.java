package cn.foxnickel.recyclerviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.foxnickel.recyclerviews.R;
import cn.foxnickel.recyclerviews.bean.Meizi;

/**
 * Created by Administrator on 2017/5/2.
 */

public class LinearRecyclerAdapter extends RecyclerView.Adapter<LinearRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Meizi> mMeiziList;

    /*构造函数，进行初始化（一般传入context和数据列表）*/
    public LinearRecyclerAdapter(Context context,List<Meizi> meiziList) {
        mContext = context;
        mMeiziList = meiziList;
    }

    /*创建ViewHolder*/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_linear_recycler,parent,false);
        return new ViewHolder(view);
    }

    /*数据绑定到ViewHolder*/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Meizi meizi = mMeiziList.get(position);
        Picasso.with(mContext).load(meizi.getImageUrl()).into(holder.mImageView);
    }

    /*item的数量，一般就是数据List的大小*/
    @Override
    public int getItemCount() {
        return mMeiziList.size();
    }

    /*内部类ViewHolder*/
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_meizi);
        }
    }

    /*添加item的函数*/
    public void addItem(Meizi meizi, int position) {
        mMeiziList.add(position, meizi);
        notifyItemInserted(position);
    }

    /*滑动删除会调用的函数*/
    public void removeItem(final int position) {
        final Meizi removed=mMeiziList.get(position);
        mMeiziList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(mContext,"你删除了第"+position+"个item",Toast.LENGTH_SHORT).show();
    }
}
