package com.example.mainacticity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainacticity.placeholder.PlaceholderContent;
import com.example.mainacticity.placeholder.Topic;


import java.util.ArrayList;
import java.util.List;

public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.ViewHolder>{



    private List<Topic> mDataset = new ArrayList<>();
    private HorizontalListAdapter.IOnItemClickListener mItemClickListener;

    @Override
    public HorizontalListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        return new HorizontalListAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalListAdapter.ViewHolder holder, int position) {
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

        void onItemCLick(int position, Topic data);

        void onItemLongCLick(int position, Topic data);
    }

    public HorizontalListAdapter(List<Topic> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void setOnItemClickListener(HorizontalListAdapter.IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addData(int position, Topic data) {
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
        private TextView topic;
        private View contentView;


        public ViewHolder(View v) {
            super(v);
            contentView = v;
            cover = v.findViewById(R.id.topicCover);
            topic = v.findViewById(R.id.topic);
        }

        public void onBind(int position, Topic data) {
            cover.setImageResource(data.cover);
            topic.setText(data.topic);
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
