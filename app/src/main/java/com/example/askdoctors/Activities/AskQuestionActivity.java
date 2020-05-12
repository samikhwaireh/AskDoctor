package com.example.askdoctors.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class AskQuestionActivity extends AppCompatActivity {
    Button askBtn;
    EditText diseaseEt, questionEt;
    TextView userNameTv;
    ImageView userImageView,ImageView;
    Uri uri;
    Bitmap selectedImage;
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    User user;
    String userName, profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        askBtn = findViewById(R.id.Ask_askBtn);
        diseaseEt = findViewById(R.id.Ask_diseaseEt);
        questionEt = findViewById(R.id.Ask_questionEt);
        userNameTv = findViewById(R.id.Ask_userNameTv);
        userImageView = findViewById(R.id.Ask_userImageView);
        ImageView = findViewById(R.id.Ask_ImageView);
        progressBar = findViewById(R.id.Ask_prograssbar);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        DatabaseReference reference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                userNameTv.setText(user.getFirstName() + " " + user.getLastName());
                if (!TextUtils.isEmpty(user.getProfileImage()))
                     Picasso.get().load(user.getProfileImage()).into(userImageView);

                userName = user.getFirstName() + " " + user.getLastName();
                profileImage = user.getProfileImage();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(AskQuestionActivity.this, databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });

    }


    public void Ask(View view){
        closeKeyboard();
        askBtn.setEnabled(false);

        final String disease = diseaseEt.getText().toString().trim();
        final String question = questionEt.getText().toString().trim();

        boolean con;
        //Eğer image seçildiyse
        if (selectedImage != null){
            progressBar.setVisibility(View.VISIBLE);
            con = checkEntries(disease,question);
            if (con == false){



                UUID uuid1 = UUID.randomUUID();
                final String path = "Questions/" + firebaseAuth.getUid() + "/" + uuid1.toString() + ".jpg";
                StorageReference storageReference = firebaseStorage.getReference(path);

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        StorageReference downloadReference = firebaseStorage.getReference(path);
                        downloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, Object> QuestionDetails = new HashMap<>();
                                QuestionDetails.put("question", question);
                                QuestionDetails.put("disease", disease);
                                QuestionDetails.put("image", uri.toString());
                                QuestionDetails.put("userName", userName);
                                QuestionDetails.put("profileImage", profileImage);
                                QuestionDetails.put("date",-1*new Date().getTime());
                                QuestionDetails.put("id", firebaseAuth.getUid());

                                UUID uuid = UUID.randomUUID();
                                DatabaseReference reference = firebaseDatabase.getReference("Questions").child(uuid.toString());
                                reference.setValue(QuestionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toasty.success(AskQuestionActivity.this, "Question asked successfully",
                                                Toasty.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(AskQuestionActivity.this, HomeActivity.class);
                                        intent.putExtra("user","Users");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        askBtn.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Toasty.error(AskQuestionActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                askBtn.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Toasty.error(AskQuestionActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        askBtn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(AskQuestionActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                    }
                });

            }else {
                askBtn.setEnabled(true);
            }

        }else {

            con = checkEntries(disease,question);
            if (con == false){

                progressBar.setVisibility(View.VISIBLE);
                UUID uuid = UUID.randomUUID();
                DatabaseReference reference = firebaseDatabase.getReference("Questions").child(uuid.toString());
                Map<String, Object> QuestionDetails = new HashMap<>();
                QuestionDetails.put("question", question);
                QuestionDetails.put("disease", disease);
                QuestionDetails.put("image", "noImage");
                QuestionDetails.put("userName", userName);
                QuestionDetails.put("profileImage", profileImage);
                QuestionDetails.put("date",-1*new Date().getTime());
                QuestionDetails.put("id", firebaseAuth.getUid());

                reference.setValue(QuestionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(AskQuestionActivity.this, "Question asked successfully",
                                Toasty.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(AskQuestionActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user","Users");
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        askBtn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(AskQuestionActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                    }
                });


            }else {
                askBtn.setEnabled(true);
            }

        }

    }

    private boolean checkEntries(String disease, String question){

        boolean con = false;

        if (TextUtils.isEmpty(disease)){
            diseaseEt.setError("Empty!");
            diseaseEt.requestFocus();
            con = true;
        }
        if (TextUtils.isEmpty(question)){
            questionEt.setError("Empty!");
            questionEt.requestFocus();
            con = true;
        }

        return con;
    }

    public void openGalleryForImage(View view){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 30);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 31);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 30 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 31);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==31 && resultCode == RESULT_OK && data != null){

            uri = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    ImageView.setImageBitmap(selectedImage);
                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ImageView.setImageBitmap(selectedImage);
                }

            }catch (Exception e){
                Toasty.error(AskQuestionActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeKeyboard(){
        View view = AskQuestionActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
