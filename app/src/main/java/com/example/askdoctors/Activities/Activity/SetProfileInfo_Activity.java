package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class SetProfileInfo_Activity extends AppCompatActivity {

    ImageView userImageView;
    Uri uri;
    Bitmap selectedImage;
    String accType;
    ProgressBar progressBar;


    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_info_);
        userImageView = findViewById(R.id.profileInfo_ImageView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        TextView nameTV = findViewById(R.id.proInfo_userNameTv);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent= getIntent();
        accType = intent.getStringExtra("accType");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        nameTV.setText(firstName + " " + lastName);


    }

    public void selectImageFromGallery(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20);
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 21);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 20 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 21);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 21 && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    userImageView.setImageBitmap(selectedImage);
                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    userImageView.setImageBitmap(selectedImage);
                }
            }catch (Exception e){
                Toasty.error(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveImageInFirebase(View view){
        if (selectedImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            final UUID uuid = UUID.randomUUID();
            final String path = "Users/ProfileImages/" + firebaseAuth.getUid() + "/" + uuid + ".jpg";
            StorageReference reference = firebaseStorage.getReference(path);
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference storageReference = firebaseStorage.getReference(path);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map<String, Object> profileImageUrl = new HashMap<>();
                            profileImageUrl.put("profileImage", uri.toString());
                            DatabaseReference databaseReference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());

                            databaseReference.updateChildren(profileImageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressBar.setVisibility(View.GONE);
                                    if (accType.equals("Users")){

                                        Toasty.success(SetProfileInfo_Activity.this, "Account" +
                                                " created successfully", Toasty.LENGTH_LONG).show();
                                        Intent intent = new Intent(SetProfileInfo_Activity.this, HomeActivity.class);
                                        intent.putExtra("user", accType);
                                        startActivity(intent);
                                        finish();

                                    }else {

                                        Toasty.info(SetProfileInfo_Activity.this, "Account" +
                                                " created successfully, Please wait while admin confirms your account", Toasty.LENGTH_LONG).show();
                                        Intent intent = new Intent(SetProfileInfo_Activity.this, LoginActivity.class);
                                        firebaseAuth.signOut();
                                        startActivity(intent);
                                        finish();
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(SetProfileInfo_Activity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(SetProfileInfo_Activity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toasty.error(SetProfileInfo_Activity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {

            if (accType.equals("Users")){
                Intent intent = new Intent(this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user", accType);
                startActivity(intent);

            }else {

                Toasty.info(SetProfileInfo_Activity.this, "Account" +
                        " created successfully, Please wait while admin confirms your account", Toasty.LENGTH_LONG).show();
                Intent intent = new Intent(SetProfileInfo_Activity.this, LoginActivity.class);
                firebaseAuth.signOut();
                startActivity(intent);
                finish();
            }

        }
    }
}
