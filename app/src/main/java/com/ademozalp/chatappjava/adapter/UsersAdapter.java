package com.ademozalp.chatappjava.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ademozalp.chatappjava.view.ChatScreen;
import com.ademozalp.chatappjava.databinding.RecyclerrowuserBinding;
import com.ademozalp.chatappjava.model.usersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder>{

    ArrayList<usersModel> modelArrayList;
    FirebaseAuth auth;

    public UsersAdapter(ArrayList<usersModel> modelArrayList){
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerrowuserBinding recyclerrowuserBinding = RecyclerrowuserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UsersHolder(recyclerrowuserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        holder.recyclerrowuserBinding.usersText.setText(modelArrayList.get(position).email);

        holder.recyclerrowuserBinding.usersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // action ile gitmeli
                Intent intent = new Intent(holder.itemView.getContext(), ChatScreen.class);
                intent.putExtra("recieverEmail",modelArrayList.get(position).email);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //CreateNewChat(modelArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class UsersHolder extends RecyclerView.ViewHolder {
        RecyclerrowuserBinding recyclerrowuserBinding;
        public UsersHolder(RecyclerrowuserBinding recyclerrowuserBinding) {
            super(recyclerrowuserBinding.getRoot());
            this.recyclerrowuserBinding = recyclerrowuserBinding;
        }
    }


    private void CreateNewChat(usersModel model){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("Kim :" + user.getEmail());
        System.out.println("Kime :" + model.email);
    }
}
