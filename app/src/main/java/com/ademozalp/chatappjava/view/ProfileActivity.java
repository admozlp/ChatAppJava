package com.ademozalp.chatappjava.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ademozalp.chatappjava.R;
import com.ademozalp.chatappjava.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lusfold.spinnerloading.SpinnerLoading;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ActivityProfileBinding binding;
    FirebaseAuth auth;
    Uri imageData;
    String newKulAdi;
    FirebaseUser currentlyUser;
    String currentlyEmail;
    SpinnerLoading spl;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerLauncher();

        auth =FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        spinner = (ProgressBar)findViewById(R.id.progressBar1);


        getProfile();
    }

    public void getProfile(){
        spinner.setVisibility(View.VISIBLE);
        currentlyUser = auth.getCurrentUser();
        currentlyEmail = currentlyUser.getEmail();
        db.collection("MyUsers").whereEqualTo("username",currentlyEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                int a = 0;
                for(Object veri : documentSnapshot.getData().values()){
                    if(a == 1){
                        try{
                            Picasso.get().load(veri.toString()).into(binding.profilePhoto);
                            spinner.setVisibility(View.GONE);
                        }catch (Exception e){
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
                    if(a == 2){
                        binding.nametext.setText(veri.toString());
                    }
                    a++;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        });
    }

    public void update(View view){
        spinner.setVisibility(View.VISIBLE);
        newKulAdi = binding.nametext.getText().toString();

        if(imageData != null && !newKulAdi.equals("")){
            /*spl = binding.spinner;
            spl.setPaintMode(0);
            spl.setCircleRadius(15);*/
            UUID uuid = UUID.randomUUID();
            String imagePath = "images/" + uuid + ".jpg";
            storageReference.child(imagePath).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("foto uploaded");
                    StorageReference newRefrance =firebaseStorage.getReference(imagePath);
                    newRefrance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("kuladi", newKulAdi);
                            userData.put("imageUrl", imageUrl);
                            db.collection("MyUsers").whereEqualTo("username", currentlyEmail)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                String documnetID = documentSnapshot.getId();

                                                db.collection("MyUsers").document(documnetID)
                                                        .update(userData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(ProfileActivity.this, "Bilgiler Başarıyla Güncellendi", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(ProfileActivity.this, ChatsActivity.class);
                                                                spinner.setVisibility(View.GONE);
                                                                startActivity(intent);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                System.out.println(e.getLocalizedMessage());// veriler güncellenemedi
                                                            }
                                                        });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println(e.getLocalizedMessage());// giriş yapan kullnıcının verileri çekilemedi
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getLocalizedMessage());// fotoğraf url indirilemedi
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getLocalizedMessage());// fotoğraf yüklenmedi
                }
            });
        }
    }

    public void selectphoto(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Needed permission for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //intentTo gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            resultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        binding.profilePhoto.setImageURI(imageData);
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //intent to gallery
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    resultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(ProfileActivity.this, "Needed permission", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}