package com.ademozalp.chatappjava.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ademozalp.chatappjava.databinding.OldchatrecyclerrowBinding;
import com.ademozalp.chatappjava.model.OldChatModel;
import com.ademozalp.chatappjava.view.ChatScreen;

import java.util.ArrayList;

public class OldChatAdapter extends RecyclerView.Adapter<OldChatAdapter.OldChatViewHolder> {
    ArrayList<OldChatModel> oldChatModelArrayList;

    public OldChatAdapter(ArrayList<OldChatModel> oldChatModelArrayList) {
        this.oldChatModelArrayList = oldChatModelArrayList;
    }

    @NonNull
    @Override
    public OldChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OldchatrecyclerrowBinding binding = OldchatrecyclerrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OldChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OldChatViewHolder holder, int position) {
        holder.binding.oldchatrow.setText(oldChatModelArrayList.get(position).reciever);

        holder.binding.oldchatrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatScreen.class);
                intent.putExtra("recieverEmail",oldChatModelArrayList.get(position).reciever);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //CreateNewChat(modelArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return oldChatModelArrayList.size();
    }

    public class OldChatViewHolder extends RecyclerView.ViewHolder {
        OldchatrecyclerrowBinding binding;
        public OldChatViewHolder(OldchatrecyclerrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
