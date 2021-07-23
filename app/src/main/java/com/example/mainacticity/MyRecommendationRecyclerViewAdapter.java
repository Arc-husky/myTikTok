package com.example.mainacticity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.mainacticity.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.mainacticity.placeholder.video;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecommendationRecyclerViewAdapter extends RecyclerView.Adapter<MyRecommendationRecyclerViewAdapter.ViewHolder> {


    private List<video> mDataset = new ArrayList<>();
    private IOnItemClickListener mItemClickListener;
    private Fragment mfragment=null;
    private Context mContext=null;

    @Override
    public MyRecommendationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false));
    }

    public static int dip2px(Context context,float dpValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return(int)(dpValue*scale+0.5f);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecommendationRecyclerViewAdapter.ViewHolder holder, int position) {
        video data = mDataset.get(position);
        if(mfragment!=null) {
            CornerTransform transformation = new CornerTransform(mfragment.getContext(), dip2px(mfragment.getContext(), 10));
            Glide.with(mfragment)
                    .load(data.cover)
                    .placeholder(R.drawable.waiting)
                    .transform(transformation)
                    .into(holder.cover);
        }else if(mContext!=null){
            CornerTransform transformation = new CornerTransform(mContext, dip2px(mContext, 10));
            Glide.with(mContext)
                    .load(data.cover)
                    .placeholder(R.drawable.waiting)
                    .transform(transformation)
                    .into(holder.cover);
        }else {
            holder.cover.setImageResource(R.mipmap.cover);
        }
        holder.likeNumber.setText(data.likeNumber);
        holder.username.setText(data.userName);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mDataset.get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongCLick(position, mDataset.get(position));
                }
                return false;
            }

        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLick(int position, video data);

        void onItemLongCLick(int position, video data);
    }

    public MyRecommendationRecyclerViewAdapter(Fragment fragment,List<video> myDataset) {
        mDataset.addAll(myDataset);
        mfragment = fragment;
        mContext = null;
    }

    public MyRecommendationRecyclerViewAdapter(Context context,List<video> myDataset) {
        mDataset.addAll(myDataset);
        mfragment = null;
        mContext = context;
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addData(int position, video data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }

    public void removeData(int position) {
        if (null != mDataset && mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            if (position != mDataset.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mDataset.size() - position);
            }
        }
    }

    public void updateData(List<video> data) {
        mDataset = data;
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView likeNumber;
        View contentView;
        TextView username;

        public ViewHolder(View v) {
            super(v);
            contentView = v;
            cover = v.findViewById(R.id.cover);
            likeNumber = v.findViewById(R.id.Number);
            username=v.findViewById(R.id.usernamex);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                contentView.setOnLongClickListener(listener);
            }
        }
    }
}