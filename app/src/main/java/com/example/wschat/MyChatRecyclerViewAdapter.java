package com.example.wschat;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wschat.databinding.FragmentItemBinding;
import com.example.wschat.placeholder.ChatItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ChatItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private final List<ChatItem> mValues;
    private static Context ctx;

    public MyChatRecyclerViewAdapter(List<ChatItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }


    public final List<ChatItem> getListItem() {
        return this.mValues;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).content);
        holder.mContentView.setText(mValues.get(position).dateTime);
        holder.lastMex.setText(mValues.get(position).body);
        if(holder.mItem.newMex == 1) {
            holder.new_mex_ic.setVisibility(View.VISIBLE);
        } else {
            holder.new_mex_ic.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView lastMex;
        public final ImageView new_mex_ic;
        public ChatItem mItem;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            lastMex = binding.lastMex;
            new_mex_ic = binding.imgNewMex;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}