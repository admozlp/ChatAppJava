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

import com.ademozalp.chatappjava.adapter.UsersAdapter;
import com.ademozalp.chatappjava.databinding.FragmentUsersBinding;
import com.ademozalp.chatappjava.model.usersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class usersFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    ArrayList<usersModel> UsersArrayList;
    private FragmentUsersBinding binding;
    UsersAdapter usersAdapter;

    public usersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UsersArrayList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater,container,false);

        getUsers();
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        usersAdapter = new UsersAdapter(UsersArrayList);
        binding.userRecyclerView.setAdapter(usersAdapter);

        return binding.getRoot();
    }

    private void getUsers(){
        FirebaseUser currentlyUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentlyUserUsername = currentlyUser.getEmail();
        firestore.collection("MyUsers").whereNotEqualTo("username", currentlyUserUsername).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    //Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Kullanıcılar çekilirken hata verdi");
                }
                if(value != null){
                    for(DocumentSnapshot document : value.getDocuments()){
                        Map<String, Object> data = document.getData();
                        String email = (String) data.get("username");

                        usersModel user = new usersModel(email);
                        UsersArrayList.add(user);
                    }
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });

    }


}