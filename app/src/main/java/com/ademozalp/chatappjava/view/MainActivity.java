package com.ademozalp.chatappjava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ademozalp.chatappjava.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    String email;
    String password;
    String usnam;
    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("myid",MODE_PRIVATE);
        FirebaseUser curentlyUser = auth.getCurrentUser();
        if(curentlyUser != null){
            Intent intent = new Intent(MainActivity.this,ChatsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signIn(View view){
        email = binding.email.getText().toString();
        password = binding.passwordtxt.getText().toString();

        if(email.matches("") || password.matches("")){
            Toast.makeText(this,"Please Fill the fields",Toast.LENGTH_LONG).show();
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(MainActivity.this,ChatsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void signUp(View view){
        email = binding.email.getText().toString();
        password = binding.passwordtxt.getText().toString();
        usnam = binding.username.getText().toString();

        if(email.matches("") || password.matches("") || usnam.matches("")){
            Toast.makeText(this,"Please Fill the fields",Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String, Object> userData = new HashMap<>();

            userData.put("username", email);
            userData.put("password", password);
            userData.put("kuladi",usnam);


            firestore.collection("MyUsers").add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    System.out.println("MyUsers tablosuna kullanıcı başarıyla eklendi");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("eklenmedi");
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(MainActivity.this,ChatsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}