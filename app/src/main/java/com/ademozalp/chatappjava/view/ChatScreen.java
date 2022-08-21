package com.ademozalp.chatappjava.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ademozalp.chatappjava.adapter.MessagesAdapter;
import com.ademozalp.chatappjava.databinding.ActivityChatScreenBinding;
import com.ademozalp.chatappjava.model.MessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatScreen extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    String message;
    String sender;
    String reciever;
    FirebaseUser currentlyUser;
    ArrayList<MessageModel> MessageModelArrayList;
    MessagesAdapter messagesAdapter;
    ArrayList<MessageModel> SecondModelArrayList;
    ArrayList<MessageModel> FirstModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MessageModelArrayList = new ArrayList<>();
        SecondModelArrayList = new ArrayList<>();
        FirstModelArrayList = new ArrayList<>();

        currentlyUser = FirebaseAuth.getInstance().getCurrentUser();
        sender = currentlyUser.getEmail();
        Intent intent = getIntent();
        reciever = intent.getStringExtra("recieverEmail");

        getData();
        binding.messagesRecyclerView.setLayoutManager(new LinearLayoutManager(ChatScreen.this));
        messagesAdapter = new MessagesAdapter(MessageModelArrayList);
        binding.messagesRecyclerView.setAdapter(messagesAdapter);
    }

    public void send(View view){

        message = binding.messageTxt.getText().toString();
        binding.messageTxt.setText("");
        HashMap<String, Object> newMessage = new HashMap<>();

        newMessage.put("sender",sender);
        newMessage.put("message",message);
        newMessage.put("reciever",reciever);
        newMessage.put("date", FieldValue.serverTimestamp());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Messages").add(newMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //oldChatFragment newm = new oldChatFragment();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getData(){
        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();

        Query query = firestore1.collection("Messages")
                        .whereEqualTo("sender", sender)
                        .whereEqualTo("reciever", reciever)
                        .orderBy("date", Query.Direction.ASCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ChatScreen.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null){

                    for(DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> data = document.getData();

                        String getsender = (String) data.get("sender");
                        String getreciever = (String) data.get("reciever");
                        String getmessage = (String) data.get("message");

                        MessageModel messageModel = new MessageModel(getsender, getreciever, getmessage);
                        MessageModelArrayList.add(messageModel);
                    }
                    messagesAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}