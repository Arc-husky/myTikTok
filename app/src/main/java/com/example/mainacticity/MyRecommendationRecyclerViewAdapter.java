package com.example.mainacticity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public MyRecommendationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecommendationRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.onBind(position, mDataset.get(position));
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

    public MyRecommendationRecyclerViewAdapter(List<video> myDataset) {
        mDataset.addAll(myDataset);
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




    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cover;
        private TextView likeNumber;
        private View contentView;


        public ViewHolder(View v) {
            super(v);
            contentView = v;
            cover = v.findViewById(R.id.cover);
            likeNumber = v.findViewById(R.id.Number);
        }

        public void onBind(int position, video data) {
            cover.setImageResource(data.cover);
            likeNumber.setText(data.likeNumber);
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