package com.ademozalp.chatappjava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ademozalp.chatappjava.R;
import com.ademozalp.chatappjava.databinding.ActivityChatsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ChatsActivity extends AppCompatActivity {
    private ActivityChatsBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wpmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if(item.getItemId() == R.id.new_chat){
                NavDirections action = oldChatFragmentDirections.actionOldChatFragmentToUsersFragment();
                Navigation.findNavController(this,R.id.fragmentContainer).navigate(action);
            }
            else if(item.getItemId() == R.id.logout){
                auth.signOut();
                Intent intent = new Intent(ChatsActivity.this,MainActivity.class);
                startActivity(intent);
                //finish();
            }
            else if(item.getItemId() == R.id.profile){
                Intent intent = new Intent(ChatsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        return super.onOptionsItemSelected(item);
    }
}