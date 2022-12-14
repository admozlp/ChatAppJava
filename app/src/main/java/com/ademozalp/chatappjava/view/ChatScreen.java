package com.ademozalp.chatappjava.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ademozalp.chatappjava.R;
import com.ademozalp.chatappjava.adapter.MessagesAdapter;
import com.ademozalp.chatappjava.databinding.ActivityChatScreenBinding;
import com.ademozalp.chatappjava.model.MessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatScreen extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    String message;
    String sender;
    String reciever;
    String recievername;
    String sendername;
    FirebaseUser currentlyUser;
    ArrayList<MessageModel> MessageModelArrayList;
    MessagesAdapter messagesAdapter;
    ArrayList<MessageModel> SecondModelArrayList;
    ArrayList<MessageModel> FirstModelArrayList;
    public static NotificationManagerCompat notificationManagerCompat;
    public static Notification notification;

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

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("MyUsers").whereEqualTo("username",sender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot document : value.getDocuments()) {
                    Map<String, Object> data = document.getData();
                    sendername = (String) data.get("kuladi");
                }
            }
        });
        Intent intent = getIntent();
        reciever = intent.getStringExtra("recieverEmail");
        recievername = intent.getStringExtra("recieverKuladi");


        getData();
        binding.messagesRecyclerView.setLayoutManager(new LinearLayoutManager(ChatScreen.this));
        messagesAdapter = new MessagesAdapter(MessageModelArrayList);
        binding.messagesRecyclerView.setAdapter(messagesAdapter);

        binding.messageTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.messagesRecyclerView.scrollToPosition(MessageModelArrayList.size() - 1);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("myCh","MyChannel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void send(View view){

        message = binding.messageTxt.getText().toString();
        if(message.matches("")){

        }else{
            binding.messageTxt.setText("");
            HashMap<String, Object> newMessage = new HashMap<>();

            newMessage.put("sender",sender);
            newMessage.put("sendername",sendername);
            newMessage.put("message",message.trim());
            newMessage.put("reciever",reciever);
            newMessage.put("recievername",recievername);
            newMessage.put("date", FieldValue.serverTimestamp());

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("Messages").add(newMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            binding.messagesRecyclerView.scrollToPosition(MessageModelArrayList.size() - 1);
        }
    }

    private void getData(){
        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();

        Query query = firestore1.collection("Messages")
                        .orderBy("date", Query.Direction.ASCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(ChatScreen.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    MessageModelArrayList.clear();
                    messagesAdapter.notifyDataSetChanged();
                    for(DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> data = document.getData();

                        String getsender = (String) data.get("sender");
                        String getreciever = (String) data.get("reciever");
                        String getmessage = (String) data.get("message");

                        MessageModel messageModel = new MessageModel(getsender, getreciever, getmessage);

                        if(getsender.equals(sender)){
                            if(getreciever.equals(reciever)){
                                MessageModelArrayList.add(messageModel);
                            }
                        }
                        else if(getreciever.equals(sender)){
                            if(getsender.equals(reciever)){
                                MessageModelArrayList.add(messageModel);
                            }
                        }
                    }
                    int index = MessageModelArrayList.size() - 1;
                    MessageModel model = MessageModelArrayList.get(index);

                    if(model.reciever.equals(currentlyUser.getEmail())){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatScreen.this, "myCh")
                                .setSmallIcon(R.drawable.iconwp)
                                .setContentTitle(model.sender)
                                .setContentText(model.message);

                        notification = builder.build();
                        notificationManagerCompat = NotificationManagerCompat.from(ChatScreen.this);
                        notificationManagerCompat.notify(1, notification);
                    }

                    messagesAdapter.notifyDataSetChanged();
                    binding.messagesRecyclerView.scrollToPosition(MessageModelArrayList.size()-1);
                }
            }
        });
    }
}