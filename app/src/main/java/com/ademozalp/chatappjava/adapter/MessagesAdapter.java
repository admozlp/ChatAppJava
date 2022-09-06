package com.ademozalp.chatappjava.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.ademozalp.chatappjava.R;
import com.ademozalp.chatappjava.databinding.RecyclerrowmessageBinding;
import com.ademozalp.chatappjava.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;

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


        ConstraintLayout constraintLayout = holder.recyclerrowmessageBinding.ccLayout;

        if(messageModelArrayList.get(position).sender.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.clear(R.id.messagerow,ConstraintSet.LEFT);
            constraintSet.connect(R.id.messagerow, ConstraintSet.RIGHT,R.id.ccLayout, ConstraintSet.RIGHT, 0);
            constraintSet.applyTo(constraintLayout);
        }else{
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.clear(R.id.messagerow,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.messagerow, ConstraintSet.LEFT,R.id.ccLayout, ConstraintSet.LEFT, 0);
            constraintSet.applyTo(constraintLayout);
        }
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
