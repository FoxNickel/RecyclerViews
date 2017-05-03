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
 * Created by Administrator on 2017/5/3.
 */

public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<StaggeredRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Meizi> mMeiziList;

    public StaggeredRecyclerAdapter(Context context,List<Meizi> meiziList) {
        mContext = context;
        mMeiziList = meiziList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_starrged_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Meizi meizi = mMeiziList.get(position);
        Picasso.with(mContext).load(meizi.getImageUrl()).into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"You clicked item "+holder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeiziList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_meizi);
        }
    }
}
