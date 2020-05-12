package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText firstNameEt,lastNameEt,birthdayEt,universityEt,graduateEt;
    Button pickdateBtn,updateBtn,gradDateBtn;
    ImageView profileImageView,diplomaImageView;
    RadioGroup genderRgBtn;
    ProgressBar progressBar;

    String firstName,lastName,birthday,gender,profileImage,university,graduatedate,diplomaImage,accType;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    Bitmap selectedImage,selectedDiploma;
    Uri uri,diplomaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firstNameEt = findViewById(R.id.update_firstNameEt);
        lastNameEt = findViewById(R.id.update_lastNameEt);
        birthdayEt = findViewById(R.id.update_birthdayEt);
        universityEt = findViewById(R.id.update_universityEt);
        graduateEt = findViewById(R.id.update_graduateEt);
        pickdateBtn = findViewById(R.id.update_pickdateBtn);
        updateBtn = findViewById(R.id.update_updateBtn);
        gradDateBtn = findViewById(R.id.update_updateGradDateBtn);
        profileImageView = findViewById(R.id.update_userImage);
        diplomaImageView = findViewById(R.id.update_diplomaImageView);
        genderRgBtn = findViewById(R.id.update_genderRgBtn);
        progressBar = findViewById(R.id.update_progressbar);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        final Intent intent = getIntent();
        accType = intent.getStringExtra("accType");


        if (accType.equals("Users")){
            graduateEt.setVisibility(View.GONE);
            universityEt.setVisibility(View.GONE);
            diplomaImageView.setVisibility(View.GONE);
            gradDateBtn.setVisibility(View.GONE);
        }

        getInfo();

        pickdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(birthdayEt);
            }
        });

        gradDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(graduateEt);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(UpdateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){

                    ActivityCompat.requestPermissions(UpdateProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20);

                }else {
                    Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery, 21);
                }

            }
        });

        diplomaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(UpdateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){

                    ActivityCompat.requestPermissions(UpdateProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 50);

                }else {
                    Intent opentgalleryForDiploma = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(opentgalleryForDiploma, 51);
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 20 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, 21);
        }else if (requestCode == 50 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Intent opentgalleryForDiploma = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(opentgalleryForDiploma, 51);

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 21 ){

           if (resultCode == RESULT_OK && data != null){
               uri = data.getData();

               try {

                   if (Build.VERSION.SDK_INT >= 28){

                       ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                       selectedImage = ImageDecoder.decodeBitmap(source);
                       profileImageView.setImageBitmap(selectedImage);

                   }else {

                       selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                       profileImageView.setImageBitmap(selectedImage);

                   }

               }catch (Exception e){
                   Toasty.error(this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
               }
           }

        }else if (requestCode == 51  ){

            if ( resultCode == RESULT_OK && data != null){
                diplomaUri = data.getData();

                try {


                    if (Build.VERSION.SDK_INT >= 28){

                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), diplomaUri);
                        selectedDiploma = ImageDecoder.decodeBitmap(source);
                        diplomaImageView.setImageBitmap(selectedDiploma);

                    }else {

                        selectedDiploma = MediaStore.Images.Media.getBitmap(this.getContentResolver(), diplomaUri);
                        diplomaImageView.setImageBitmap(selectedDiploma);

                    }

                }catch (Exception e){

                    Toasty.error(this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getInfo(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (accType.equals("Doctors")){
                        Doctors doctor = dataSnapshot.getValue(Doctors.class);
                        firstNameEt.setText(doctor.getFirstName());
                        lastNameEt.setText(doctor.getLastName());
                        birthdayEt.setText(doctor.getBirthday());
                        universityEt.setText(doctor.getUniversity());
                        graduateEt.setText(doctor.getGraduate());
                        if (doctor.getGender().equals("male")){
                            genderRgBtn.check(R.id.update_maleRBtn);
                        }else if (doctor.getGender().equals("female")){
                            genderRgBtn.check(R.id.update_femaleRBtn);
                        }else {
                            genderRgBtn.check(R.id.update_otherRBtn);
                        }
                        Picasso.get().load(doctor.getDiploma()).into(diplomaImageView);

                        if (!TextUtils.isEmpty(doctor.getProfileImage()))
                            Picasso.get().load(doctor.getProfileImage()).into(profileImageView);

                        progressBar.setVisibility(View.GONE);

                    }else{
                        User user = dataSnapshot.getValue(User.class);
                        firstNameEt.setText(user.getFirstName());
                        lastNameEt.setText(user.getLastName());
                        birthdayEt.setText(user.getBirthday());
                        if (user.getGender().equals("male")){
                            genderRgBtn.check(R.id.update_maleRBtn);
                        }else if (user.getGender().equals("female")){
                            genderRgBtn.check(R.id.update_femaleRBtn);
                        }else {
                            genderRgBtn.check(R.id.update_otherRBtn);
                        }
                        if (!TextUtils.isEmpty(user.getProfileImage()))
                            Picasso.get().load(user.getProfileImage()).into(profileImageView);

                        progressBar.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(UpdateProfileActivity.this, databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }


    private void pickDate(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        closeKeyboard(); // close picker dialog

        // -> create dialog with date picker
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int dayOfMonth) {
                editText.setText(dayOfMonth + "/" + (mMonth+1) + "/" + mYear); // print picked date to EditText
            }
        }, year, month, day);
        dpd.show();
    }

    private void closeKeyboard(){
        View view = UpdateProfileActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void updateInfo(){
        progressBar.setVisibility(View.VISIBLE);
        closeKeyboard();
        firstName = firstNameEt.getText().toString().trim();
        lastName = lastNameEt.getText().toString().trim();
        birthday = birthdayEt.getText().toString().trim();
        int g = genderRgBtn.getCheckedRadioButtonId();
        switch (g){
            case R.id.update_maleRBtn:
                gender = "male";
                break;
            case R.id.update_femaleRBtn:
                gender = "female";
                break;
            case R.id.update_otherRBtn:
                gender = "other";
                break;
        }
        if (accType.equals("Doctors")){

            university = universityEt.getText().toString().trim();
            graduatedate = graduateEt.getText().toString().trim();

            if (selectedImage == null && selectedDiploma == null){

                //ne profile ne de diploma seçildi

                DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
                boolean con = checkDoctorInfo(firstName,lastName,university);
                if (con == false){
                    Map<String, Object> details = new HashMap<>();
                    details.put("firstName",firstName);
                    details.put("lastName",lastName);
                    details.put("birthday", birthday);
                    details.put("gender", gender);
                    details.put("University", university);
                    details.put("Graduate", graduatedate);
                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.success(UpdateProfileActivity.this, "Updated successfully", Toasty.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
                }
            }else if (selectedImage != null && selectedDiploma == null){
                //profile fotoğrafı seçildi diploma seçilmedi

                boolean con = checkDoctorInfo(firstName, lastName, university);
                if (con == false){

                    //saving image in firebase storage
                    UUID uuid = UUID.randomUUID();
                    final String path = "Users/ProfileImages/" + firebaseAuth.getUid() + "/" + uuid.toString() + ".jpg";
                    StorageReference storageReference = firebaseStorage.getReference(path);

                    storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            //getting images download url

                            StorageReference downloadReference = firebaseStorage.getReference(path);

                            downloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //saving details in realtime

                                    DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
                                    Map<String, Object> details = new HashMap<>();

                                    details.put("firstName", firstName);
                                    details.put("lastName", lastName);
                                    details.put("gender", gender);
                                    details.put("birthday", birthday);
                                    details.put("university", university);
                                    details.put("profileImage", uri.toString());
                                    details.put("graduate", graduatedate);

                                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.success(UpdateProfileActivity.this, "Updated successfully", Toasty.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
                }

            }else if (selectedImage == null && selectedDiploma != null){
                //profile seçilmedi diploma seçildi

                boolean con = checkDoctorInfo(firstName, lastName, university);

                if (con == false){

                    UUID uuid = UUID.randomUUID();
                    final String path = "DoctorsProof/" + firebaseAuth.getUid() + "/" + uuid.toString() + ".jpg";
                    final Map<String, Object> details = new HashMap<>();
                    details.put("firstName", firstName);
                    details.put("lastName", lastName);
                    details.put("gender", gender);
                    details.put("birthday", birthday);
                    details.put("university", university);
                    details.put("graduate", graduatedate);

                    StorageReference storageReference = firebaseStorage.getReference(path);

                    storageReference.putFile(diplomaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            StorageReference downloadReference = firebaseStorage.getReference(path);
                            downloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    details.put("diploma", uri.toString());
                                    DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
                                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.success(UpdateProfileActivity.this,
                                                    "Updated successfully", Toasty.LENGTH_LONG).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(UpdateProfileActivity.this,
                                                    e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
                }

            }else if (selectedDiploma != null && selectedImage != null){

                boolean con = checkDoctorInfo(firstName, lastName, university);

                if (con == false){

                    UUID uuid = UUID.randomUUID();
                    UUID uuid1 = UUID.randomUUID();

                    final String path = "Users/ProfileImages/" + firebaseAuth.getUid() + "/" + uuid.toString() + ".jpg";
                    final String path1 = "DoctorsProof/" + firebaseAuth.getUid() + "/" + uuid.toString() + ".jpg";

                    final Map<String, Object> details = new HashMap<>();
                    details.put("firstName", firstName);
                    details.put("lastName", lastName);
                    details.put("gender", gender);
                    details.put("birthday", birthday);
                    details.put("university", university);
                    details.put("graduate", graduatedate);

                    StorageReference storageReference = firebaseStorage.getReference(path);
                    storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            StorageReference proDownloadReference = firebaseStorage.getReference(path);

                            proDownloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    details.put("profileImage", uri.toString());

                                    StorageReference dipoReference = firebaseStorage.getReference(path1);
                                    dipoReference.putFile(diplomaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            StorageReference dipoDownloadReference = firebaseStorage.getReference(path1);

                                            dipoDownloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    details.put("diploma", uri.toString());

                                                    DatabaseReference reference = firebaseDatabase
                                                            .getReference(accType).child(firebaseAuth.getUid());
                                                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            progressBar.setVisibility(View.GONE);
                                                            Toasty.success(UpdateProfileActivity.this,
                                                                    "Updated successfully", Toasty.LENGTH_LONG).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            progressBar.setVisibility(View.GONE);
                                                            Toasty.error(UpdateProfileActivity.this,
                                                                    e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    progressBar.setVisibility(View.GONE);
                                                    Toasty.error(UpdateProfileActivity.this,
                                                            e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(UpdateProfileActivity.this,
                                                    e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(UpdateProfileActivity.this,
                                            e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                        }
                    });

                }

            }
        }else {

            if (selectedImage == null){
                //profile fotoğrafı seçilmediyse
                boolean con = checUserkInfo(firstName, lastName);
                if (con == false){

                    Map<String, Object> details = new HashMap<>();

                    details.put("firstName", firstName);
                    details.put("lastName", lastName);
                    details.put("gender", gender);
                    details.put("birthday", birthday);

                    DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());

                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.success(UpdateProfileActivity.this, "Updated successfully", Toasty.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                        }
                    });

                }


            }else {

                boolean con = checUserkInfo(firstName, lastName);

                if (con == false){

                    final Map<String, Object> details = new HashMap<>();

                    details.put("firstName", firstName);
                    details.put("lastName", lastName);
                    details.put("gender", gender);
                    details.put("birthday", birthday);

                    UUID uuid = UUID.randomUUID();
                    final String path = "Users/ProfileImages/" + firebaseAuth.getUid() + "/" + uuid.toString() + ".jpg";

                    StorageReference storageReference = firebaseStorage.getReference(path);
                    storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            StorageReference downloadReference = firebaseStorage.getReference(path);
                            downloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    details.put("profileImage", uri.toString());

                                    DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
                                    reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.success(UpdateProfileActivity.this, "Updated successfully", Toasty.LENGTH_LONG).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateProfileActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();

                        }
                    });

                }

            }

        }
    }

    private boolean checUserkInfo(String firstName, String lastName){
        boolean con = false;
        if (TextUtils.isEmpty(firstName)){
            con = true;
            firstNameEt.setError("Empty!");
            firstNameEt.requestFocus();
        }
        if (TextUtils.isEmpty(lastName)){
            con = true;
            lastNameEt.setError("Empty!");
            lastNameEt.requestFocus();
        }
        if (firstName.contains("0") ||firstName.contains("1") || firstName.contains("2") || firstName.contains("3")
        ||firstName.contains("4") ||firstName.contains("5") ||firstName.contains("6") ||firstName.contains("7")
        || firstName.contains("8") ||firstName.contains("9")){
            con = true;
            firstNameEt.setError("Wrong character entered!");
            firstNameEt.requestFocus();
        }
        if (lastName.contains("0") ||lastName.contains("1") ||lastName.contains("2") ||lastName.contains("3")
        || lastName.contains("4") ||lastName.contains("5") ||lastName.contains("6") || lastName.contains("7")
        || lastName.contains("8") || lastName.contains("9")){
            con = true;
            lastNameEt.setError("Wrong character entered!");
            lastNameEt.requestFocus();
        }
        return con;
    }

    private boolean checkDoctorInfo(String firstName, String lastName, String university){
        boolean con = false;

        if (TextUtils.isEmpty(firstName)){
            con = true;
            firstNameEt.setError("Empty!");
            firstNameEt.requestFocus();
        }
        if (TextUtils.isEmpty(lastName)){
            con = true;
            lastNameEt.setError("Empty!");
            lastNameEt.requestFocus();
        }
        if (TextUtils.isEmpty(university)){
            con = true;
            universityEt.setError("Empty!");
            universityEt.requestFocus();
        }
        if (firstName.contains("0") ||firstName.contains("1") || firstName.contains("2") || firstName.contains("3")
                ||firstName.contains("4") ||firstName.contains("5") ||firstName.contains("6") ||firstName.contains("7")
                || firstName.contains("8") ||firstName.contains("9")){
            con = true;
            firstNameEt.setError("Wrong character entered!");
            firstNameEt.requestFocus();
        }
        if (lastName.contains("0") ||lastName.contains("1") ||lastName.contains("2") ||lastName.contains("3")
                || lastName.contains("4") ||lastName.contains("5") ||lastName.contains("6") || lastName.contains("7")
                || lastName.contains("8") || lastName.contains("9")){
            con = true;
            lastNameEt.setError("Wrong character entered!");
            lastNameEt.requestFocus();
        }
        if (university.contains("0") ||university.contains("1") || university.contains("2") ||university.contains("3")
        || university.contains("4") ||university.contains("5") ||university.contains("6") || university.contains("7")
        ||university.contains("8") ||university.contains("9")){
            con = true;
            universityEt.setError("Wrong character entered");
            universityEt.requestFocus();
        }

        return con;
    }


}
