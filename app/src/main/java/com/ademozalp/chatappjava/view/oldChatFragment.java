package com.ademozalp.chatappjava.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ademozalp.chatappjava.R;
import com.ademozalp.chatappjava.adapter.OldChatAdapter;
import com.ademozalp.chatappjava.databinding.FragmentOldChatBinding;
import com.ademozalp.chatappjava.model.OldChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class oldChatFragment extends Fragment {
    ArrayList<OldChatModel> oldChatModelArrayList;
    private FragmentOldChatBinding binding;
    OldChatAdapter oldChatAdapter;
    ArrayList<String > userEmailArrayList;


    public oldChatFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldChatModelArrayList = new ArrayList<>();
        userEmailArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOldChatBinding.inflate(inflater, container, false);
        getUsers();
        binding.oldChatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        oldChatAdapter = new OldChatAdapter(oldChatModelArrayList);
        binding.oldChatRecyclerView.setAdapter(oldChatAdapter);

        return binding.getRoot();
    }

    public void getUsers(){
        FirebaseUser currentlyUser = FirebaseAuth.getInstance().getCurrentUser();
        String sender = currentlyUser.getEmail();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("Messages")
                        .whereEqualTo("sender",sender)
                                .orderBy("date", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                else if (value != null){
                    for(DocumentSnapshot document : value.getDocuments()){
                        Map<String, Object> messageEmail = document.getData();

                        String reciever = (String) messageEmail.get("reciever");
                        OldChatModel oldchat = new OldChatModel(reciever);

                        if (!userEmailArrayList.contains(reciever)){
                            oldChatModelArrayList.add(oldchat);
                            userEmailArrayList.add(reciever);
                        }
                    }
                    oldChatAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}