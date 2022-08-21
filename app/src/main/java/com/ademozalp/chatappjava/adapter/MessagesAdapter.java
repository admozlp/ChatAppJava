package com.ademozalp.chatappjava.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ademozalp.chatappjava.databinding.RecyclerrowmessageBinding;
import com.ademozalp.chatappjava.model.MessageModel;

import java.util.ArrayList;
import java.util.Comparator;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {

    ArrayList<MessageModel> messageModelArrayList;

    public MessagesAdapter(ArrayList<MessageModel> messageModelArrayList) {
        this.messageModelArrayList = messageModelArrayList;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerrowmessageBinding recyclerrowmessageBinding = RecyclerrowmessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new MessagesHolder(recyclerrowmessageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        holder.recyclerrowmessageBinding.messagerow.setText(messageModelArrayList.get(position).message);
        holder.recyclerrowmessageBinding.messagerow.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return messageModelArrayList.size();
    }

    public class MessagesHolder extends RecyclerView.ViewHolder {
        RecyclerrowmessageBinding recyclerrowmessageBinding;
        public MessagesHolder(RecyclerrowmessageBinding recyclerrowmessageBinding) {
            super(recyclerrowmessageBinding.getRoot());
            this.recyclerrowmessageBinding = recyclerrowmessageBinding;
        }
    }
}
